package ca.warp7.robot.auto;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ca.warp7.robot.Robot;
import ca.warp7.robot.misc.DataPool;
import ca.warp7.robot.misc.RTS;
import ca.warp7.robot.subsystems.Drive;
import ca.warp7.robot.subsystems.Navx;

import edu.wpi.first.wpilibj.Timer;

public class AutonomousBase {
	private Mapper mapper = new Mapper();
	
	public int step;
	public static DataPool autoPool = new DataPool("auto");
	
	protected Drive drive = Robot.drive;
	protected Navx navx = Robot.navx;
	
	private Path path;
	
	public AutonomousBase(String jsonPaths){
		path = loadJson(jsonPaths);
	}
	
	public void autonomousInit(String gameData) {
		/*
		 load autonomous data (robot types)
		 load FMS data here
		 calculate best fit path
		 */
		path.calculateSpline();
	}
	
	private Path loadJson(String jsonPaths){
		JSONParser parser = new JSONParser();
		Object obj = null;
		try {
			obj = parser.parse(jsonPaths);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		JSONObject jsonObject = (JSONObject) obj;
		return new Path(jsonObject);
	}
	private static final double speed = 1;
	private static final double slowThresh = 0.9;
	public void periodic(){
		for(int i=0;i<path.getNumberOfPoints();i++){
			Point point = path.points[i];
			
			drive.resetDistance();
			
			for (Method method : point.startMethods)
				mapper.mapMethod(method).run();
			
			double slowThreshCurr;
			if (point.slowStop)
				slowThreshCurr = slowThresh;
			else
				slowThreshCurr = 1.1;
			
			drive.resetDistance();
			
			RTS scaledRuntime = new RTS("scaledRuntime",8);
			for (Method method : point.scaledMethods)
				scaledRuntime.addTask(mapper.mapMethod(method));
			scaledRuntime.start();
			
			double scaledLocation=0;
			while (point.distance > getOverallDistance()) {//exit out when robot has gone distance
				scaledLocation = getOverallDistance()/point.distance;
				mapper.currentDistance = getOverallDistance();
				mapper.scaledLocation = scaledLocation;
				
				double derivativesPresent[] = path.derivative(i+scaledLocation);
				double derivativesFuture[] = path.derivative(i+scaledLocation+0.0001);
				
				double slopePresent = derivativesPresent[1]/derivativesPresent[0];
				double slopeFuture = derivativesFuture[1]/derivativesFuture[0];
				
				double secondDerivative = slopeFuture-slopePresent;
				
				double navAngle = navx.getAngle();
				double turnSpeed = Math.abs(slopePresent);
				turnSpeed = navAngle - Math.atan(turnSpeed);
				turnSpeed = 1 - Math.abs(turnSpeed / navAngle);
				
				if ((derivativesPresent[0] >= 0 && secondDerivative >= 0) || (derivativesPresent[0] < 0 && secondDerivative < 0))
					if (scaledLocation >= slowThreshCurr){
						double sens = 1-(scaledLocation-slowThresh)*10;
						drive.tankDrive(turnSpeed*speed*sens,speed*sens);
					}else
						drive.tankDrive(turnSpeed*speed,speed);
				else
					if (scaledLocation >= slowThreshCurr){
						double sens = 1-(scaledLocation-slowThresh)*10;
						drive.tankDrive(speed*sens,turnSpeed*speed*sens);
					}else
						drive.tankDrive(speed,turnSpeed*speed);
			}
			scaledRuntime.stop();
			//we should have a speed of zero here if theres nothing in methods array and be at our point
			for (Method method : point.endMethods)
				mapper.mapMethod(method).run();
		}
		
		drive.tankDrive(0,0);
	}
	
	private double getOverallDistance() {
		return (drive.getLeftDistance()+drive.getRightDistance())/2;
	}
	
	private double errorOld = 0.0;
	private boolean resetT = true;
	private double offset = 0.0;
	private double errorSum = 0.0;
	private int counterR = 0;
	private int done = 0;
	
	/**
	 * Rotate relative to current rotation
	 * 
	 * negative values turn counter clockwise, positive clockwise
	 * 
	 * @param degrees
	 *            relative (0 is where you are)
	 *            Positive is to the right
	 */
	protected boolean relTurn(double degrees, double maxSpeed) {
		maxSpeed = Math.abs(maxSpeed);
		if(resetT){
			errorSum = 0.0;
			offset = drive.getRotation();
			counterR = 0;
			done = 0;
		}
		
		double angle = drive.getRotation()-offset;
		double kp = 8.0;
		double kd = 20.0;
		double ki = 0.00075;
		
		double error = degrees - angle;
		double speed = (kp*error/180) + ((error-errorOld)*kd/180) + (errorSum*ki/180);
		if(Math.abs(error) < 1)speed = 0;
		
		autoPool.logDouble("gyro error", error);
		autoPool.logBoolean("Turn in Tolerance", Math.abs(error) < 3);
				
		speed = Math.max(-maxSpeed, Math.min(maxSpeed, speed));
		drive.autoMove(speed, -speed);
		errorOld = error;
		
		errorSum += error;

		if(counterR >= 1000)
			errorSum = 0.0;
		
		counterR++;
		
		if(Math.abs(error) < 3){
			if(Math.abs(error) < 2)
				drive.moveRamped(0, 0);
			if(Math.abs(degrees-(drive.getRotation()-offset)) < 3)
				done++;
			else
				done = 0;
			
			if(done >= 20){
				done = 0;
				resetT = true;
				return true;
			}
			
			resetT = false;
			return false;
		}else{
			resetT = false;
			return false;
		}
	}
	
	protected boolean relTurn(double degrees, Direction dir, double maxSpeed){
		if(dir == Direction.CLOCKWISE){
			return relTurn(degrees, maxSpeed);
		}else{
			return relTurn(-degrees, maxSpeed);
		}
	}
	
	protected boolean relTurn(double degrees, Direction dir){
		if(dir == Direction.CLOCKWISE){
			return relTurn(degrees, 0.65);
		}else{
			return relTurn(-degrees, 0.65);
		}
	}
	
	protected boolean relTurn(double degrees){
		return relTurn(degrees, 0.65);
	}
	
	private boolean resetD = true;
	private double distance = 0.0;
	private double sumL = 0.0;
	private double sumR = 0.0;
	private int counter = 0;
	private double lStart = 0.0;
	private double rStart = 0.0;
	private double oldErrorL = 0.0;
	private double oldErrorR = 0.0;
	private int doneT = 0;
	/**
	 * Drive a distance straight
	 * 
	 * @param toTravel in inches
	 * @return if it is done
	 */
	protected boolean travel(double toTravel, double maxSpeed){
		maxSpeed = Math.abs(maxSpeed);
		if(resetD){
			lStart = drive.getLeftDistance();
			rStart = drive.getRightDistance();
			distance = toTravel;
			sumL = 0;
			sumR = 0;
			doneT = 0;
		}
		
		double kp = 64;
		double leftPAdd = 0.0;
		double kd = 0.0;
		double ki = 0.0;

		double errorL = toTravel - (drive.getLeftDistance()-lStart);
		double errorR = toTravel - (drive.getRightDistance()-rStart);
		sumL += errorL;
		sumR += errorR;
		autoPool.logDouble("errorL", errorR);
		autoPool.logDouble("errorR", errorL);
		double speedL = (kp*errorL)/Math.abs(lStart+distance) + ((errorL-oldErrorL)*kd/Math.abs(lStart+distance))+ki*sumL;
		double speedR = ((leftPAdd+kp)*errorR)/Math.abs(rStart+distance) + ((errorR-oldErrorR)*kd/Math.abs(rStart+distance))+ki*sumR;
		if(Math.abs(errorL) < 0.3)speedL = 0;
		if(Math.abs(errorR) < 0.3)speedR = 0;
			
		autoPool.logBoolean("L Encoder in Tolerance", Math.abs(errorR) < 0.3);
		autoPool.logBoolean("R Encoder in Tolerance", Math.abs(errorL) < 0.3);

		speedL = Math.max(-maxSpeed, Math.min(maxSpeed, speedL));
		speedR = Math.max(-maxSpeed, Math.min(maxSpeed, speedR));
		
		drive.autoMove(-speedL,  -speedR);
		oldErrorL = errorL;
		oldErrorR = errorR;
		
		if(counter >= 1000){
			sumL = 0.0;
			sumR = 0.0;
		}
		
		if(Math.abs(errorL) < 0.3 && Math.abs(errorR) < 0.3){
			if(Math.abs(toTravel - (drive.getLeftDistance()-lStart)) < 0.3 && Math.abs(toTravel - (drive.getRightDistance()-rStart)) < 0.3)
				doneT++;
			else
				doneT = 0;
			
			if(doneT >= 20){
				doneT = 0;
				resetD = true;
				return true;
			}
			resetD = false;
			return false;
		}else{
			resetD = false;
			return false;
		}
	}
	
	protected boolean travel(double toTravel){
		return travel(toTravel, 0.75);
	}
	
	
	private double timer = -1;
	protected boolean timePassed(double seconds) {
		if(timer <= 0)
			timer = Timer.getFPGATimestamp();
		
		if(Timer.getFPGATimestamp() - timer >= seconds){
			timer = -1;
			return true;
		}else{
			return false;
		}
	}
	
	private void resetValues(){
		resetD = true;
		resetT = true;
		timer = -1;
		sumL = 0.0;
		sumR = 0.0;
		counter = 0;
		counterR = 0;
		done = 0;
		doneT = 0;
		errorSum = 0.0;
		lStart = 0.0;
		rStart = 0.0;
		oldErrorL = 0.0;
		oldErrorR = 0.0;
		offset = 0.0;
		errorOld = 0.0;
	}
	
	protected enum Direction{
		CLOCKWISE, COUNTER_CLOCKWISE;
	}
}