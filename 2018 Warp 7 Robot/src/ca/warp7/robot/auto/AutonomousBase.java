package ca.warp7.robot.auto;

import static ca.warp7.robot.Constants.CUBE_DISTANCE_M;
import static ca.warp7.robot.Constants.CUBE_DISTANCE_B;

import ca.warp7.robot.Robot;
import ca.warp7.robot.misc.DataPool;
import ca.warp7.robot.misc.RTS;
import ca.warp7.robot.subsystems.Drive;
import ca.warp7.robot.subsystems.Intake;
import ca.warp7.robot.subsystems.Lift;
import ca.warp7.robot.subsystems.Limelight;
import ca.warp7.robot.subsystems.Navx;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonomousBase {
	public static DataPool autoPool = new DataPool("auto");
	
	private Drive drive = Robot.drive;
	private Navx navx = Robot.navx;
	private Limelight limelight = Robot.limelight;
	private Intake intake = Robot.intake;
	private Lift lift = Robot.lift;
	
	public AutonomousBase(){
		
	}
	
	
	private static final double speed = 0.25;
	public void autonomousInit(String gameData, String jsonPaths) {
		RTS liftRTS = new RTS("liftRTS", 8);
		Runnable liftPer = () -> lift.periodic();
		liftRTS.addTask(liftPer);
		liftRTS.start();
		
		if (jsonPaths.equals("None"))
			if (gameData.equals("LLL"))
				None_LLL();
		
		liftRTS.stop();
	}
	
	private void None_LLL() {
		navx.resetAngle();
		drive.resetDistance();
		//intake.setSpeed(0.1);
		driveDistance(120*2.54+420-110+40-20,50,10);
		turnRel(45,45/2,15);
		lift.setLoc(0.8);
		Timer.delay(7.5);
		//intake.setSpeed(1);
		//Timer.delay(0.05);
		//intake.setSpeed(0);
		turnRel(90+45,45/2,10);
		lift.setLoc(0);
		/*
		drive.resetDistance();
		intake.setSpeed(-1);
		driveDistance(100,50,10);
		drive.resetDistance();
		/*
		alignIntakeCube(50,4,20);
		lift.setLoc(0.5);
		Timer.delay(1);
		intake.setSpeed(1);
		Timer.delay(0.05);
		intake.setSpeed(0);
		*/
	}
	
	private void alignIntakeCube(double distThresh, double angleThresh, double distanceTolerance){
		double totalDist = distancePredictor(limelight.getArea());
		double dist = getOverallDistance();
		while (Robot.isAutonomousActive() && !intake.hasCube() && !within(dist,totalDist,distanceTolerance)) {
			double driveSpeed = 1+(totalDist-dist-distThresh)/distThresh;
			if (driveSpeed > 1)
				driveSpeed = 1;
			
			double cubeAngleOffset = limelight.getXOffset();
			double turnSpeed = 1-Math.abs(cubeAngleOffset/angleThresh);
			if (turnSpeed < 0)
				turnSpeed = 0;
				
			if (cubeAngleOffset >= 0)//turn right
				drive.tankDrive(speed*driveSpeed,speed*driveSpeed*turnSpeed);
			else //turn left
				drive.tankDrive(speed*driveSpeed*turnSpeed,speed*driveSpeed);
			
			dist = getOverallDistance();
			
			//dist = getOverallDistance()/2;
			//double b = limelight.getArea();
			//if (b != 0)
			//	totalDist = distancePredictor(b)/2;
		}
	}
	
	private double distancePredictor(double area){
		return CUBE_DISTANCE_B - CUBE_DISTANCE_M * area;
	}
	
	private void driveDistance(double totalDist, double distThresh, double distanceTolerance) {
		double dist = getOverallDistance();
		while (Robot.isAutonomousActive() && !within(dist,totalDist,distanceTolerance)) {
			double driveSpeed = 1+(totalDist-dist-distThresh)/distThresh;
			if (driveSpeed > 1)
				driveSpeed = 1;
			drive.tankDrive(speed*driveSpeed,driveSpeed*speed);
			dist = getOverallDistance();
			//System.out.println(dist);
		}
		drive.tankDrive(0,0);
	}
	
	private void angleTurn(double requiredAngle, double angleThresh, double angleTolerance) {
		double navAngle = navx.getAngle()%360;
		while (Robot.isAutonomousActive() && !within(navAngle,requiredAngle,angleTolerance)){
			double turnSpeed = 1+(requiredAngle-navAngle-angleThresh)/angleThresh;
			if (turnSpeed > 1)
				turnSpeed = 1;
			navAngle = navx.getAngle()%360;
			//System.out.println(turnSpeed);
			//System.out.println(turnSpeed*speed);
			if (turnDirection(requiredAngle,navAngle))
				drive.tankDrive(speed*turnSpeed,turnSpeed*speed*-1);
			else
				drive.tankDrive(turnSpeed*speed*-1,turnSpeed*speed);
				
		}
		drive.tankDrive(0,0);
	}
	
	private void turnRel(double angle, double angleThresh, double angleTolerance) {
		double wantedAngle = navx.getAngle()+angle;
		double navAngle = navx.getAngle();
		while (Robot.isAutonomousActive() && !within(navAngle,wantedAngle,angleTolerance)){
			double turnSpeed = 1+(wantedAngle-navAngle-angleThresh)/angleThresh;
			if (turnSpeed > 1)
				turnSpeed = 1;
			navAngle = navx.getAngle();
			//System.out.println(turnSpeed);
			//System.out.println(turnSpeed*speed);
			if (wantedAngle >= 0)
				drive.tankDrive(speed*turnSpeed,turnSpeed*speed*-1);
			else
				drive.tankDrive(turnSpeed*speed*-1,turnSpeed*speed);
				
		}
		drive.tankDrive(0,0);
	}
	
	private boolean turnDirection(double requiredAngle,double angle) {
		return (requiredAngle - angle) % 360 < 180; 
	}
	
	private boolean within(double angle,double setAngle, double thresh) {
		return (setAngle-thresh)<angle && (setAngle+thresh)>angle;
	}
	
	//TODO check encoder direction
	private double getOverallDistance() {
		return (drive.getLeftDistance()*-1 + drive.getRightDistance()*-1)/2;
	}
	
}