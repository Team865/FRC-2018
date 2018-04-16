package ca.warp7.robot.auto;

import static ca.warp7.robot.Constants.CUBE_DISTANCE_B;
import static ca.warp7.robot.Constants.CUBE_DISTANCE_M;

import com.stormbots.MiniPID;

import ca.warp7.robot.Robot;
import ca.warp7.robot.subsystems.Drive;
import ca.warp7.robot.subsystems.Intake;
import ca.warp7.robot.subsystems.Lift;
import ca.warp7.robot.subsystems.Limelight;
import ca.warp7.robot.subsystems.Navx;
import edu.wpi.first.wpilibj.Timer;

public class AutoFunctions {
	private MiniPID driveTurnPID;
	private MiniPID stopTurnPID;
	private MiniPID distancePID;
	private MiniPID radiusCurvePID;
	private int ticks;

	private Drive drive = Robot.drive;
	private Navx navx = Robot.navx;
	private Limelight limelight = Robot.limelight;
	private boolean angleReset;
	private boolean distanceReset;
	private boolean angleCurveReset=true;
	private double[] dFCaFC;
	public int totalTicks = 0;// for testing, delete this
	private static final double speed = 1;
	private double speedLimit = 1;
	public double wantedAngle = 0;
	private int angleSetpointSign;

	public AutoFunctions() {
		
//DRIVE TURN PID -----------------------------
		//Practice bot values:    (0.016, 0.3, 0.39)
		driveTurnPID = new MiniPID(0.016, 0.14, 0.46); //TUNE DRIVING WHILE TURNING WITH THESE
		
		//MAX I OUTPUT IMPORTANT FOR OVERCOMING FRICTION BUT WILL CAUSE OVERSHOOTS IF D ISNT ADJUSTED
		driveTurnPID.setMaxIOutput(0.21); 
		//Practice bot value:     (0.21)
		
		driveTurnPID.setOutputLimits(1);
		driveTurnPID.setOutputRampRate(0.086); //set output ramp rate : prevents chatter
		
//STOP TURN PID------------------------------
		//Practice bot values:   (0.0175, 0.3, 0.27)
		//These values undershoot at low voltage (0.0255, 0.3, 0.3);
		stopTurnPID = new MiniPID(0.0268, 0.3, 0.28); //TUNE TURNING ON THE SPOT WITHOUT DRIVING FORWARD/BACKWARD WITH THESE
		//MAX I OUTPUT IMPORTANT FOR OVERCOMING FRICTION BUT WILL CAUSE OVERSHOOTS IF D ISNT ADJUSTED
		stopTurnPID.setMaxIOutput(0.3);
		//Practice bot value:    (0.24)
		
		stopTurnPID.setOutputLimits(1);
		stopTurnPID.setOutputRampRate(0.086); //set output ramp rate : prevents chatter
		
//DISTANCE PID ---------------------------------
		
		//distancePID is used for calculating how fast to drive forward
		//only used a couple times in the autos because driveDistanceNoStop sets its own drive speed
		//tuned practice bot:    (0.02, 0.0013, 0.22)
		distancePID = new MiniPID(0.02, 0.0013, 0.23);
		distancePID.setOutputLimits(1);
		//Practice bot value:    (0.01)
		distancePID.setMaxIOutput(0.01); //MAX I OUTPUT: stacks up too high if uncapped and causes overshoots

		angleReset = true;
		distanceReset = true;
//radiusCurve PID ---------------------------------
		radiusCurvePID=new MiniPID(0.1,0,0);
	}
	
	
	//radiusCurve
	//new curve method by Kaelan. may or may not use. 
	//has zero effect on any of the other autos obviously
	public double radiusCurve(double masterDistSP, double radius, double angle, char direction, boolean noStop) {
		if (angleCurveReset) {
			if (angle > 0) {
				masterDistSP = 2 * Math.PI * radius / (360 / angle);
			}
			navx.resetAngle();
			drive.resetDistance();
			
			if (direction == 'L') {
				angleSetpointSign = -1;
			} else {
				angleSetpointSign = 1;
			}
			ticks = 0;
			angleCurveReset = false;
			radiusCurvePID.reset();
			radiusCurvePID.setOutputLimits(2 * speedLimit);
			radiusCurvePID.setSetpoint(0);
			if (!(noStop)) {
			distancePID.reset();
				distancePID.setSetpoint(masterDistSP);
			}
		}
		double lDist = drive.getLeftDistance();
		double rDist = drive.getRightDistance();
		double masterDistCur;
		if (direction=='L') {
			masterDistCur=rDist;
			angleSetpointSign = -1;
		}
		else {
			angleSetpointSign = 1;
			masterDistCur=lDist;
		}
		double absValCurAngSetP=Math.abs((masterDistCur/(2 * Math.PI * radius))*360);
		double curAng=navx.getAngle();
		double absValCurAng=Math.abs(curAng%360);
		int gyroSign;
		if (curAng%360>=0) {
			gyroSign=1;
		}
		else {
			gyroSign=-1;
		}
		double undershootDegrees;
		if (gyroSign==angleSetpointSign) {
			undershootDegrees=absValCurAngSetP-absValCurAng;
		}
		else {
			undershootDegrees=absValCurAngSetP+absValCurAng;
		}//constant divided by radius
		radiusCurvePID.setF(0.4/radius);
		double velocitySubtract=radiusCurvePID.getOutput(undershootDegrees);
		if (velocitySubtract<0) {
			System.out.println("there is a fuckup. either an incorrect sign or a big overcorrection");
			angleSetpointSign*=-1;
		}
		double masterVelocity;
		if (noStop) {
			masterVelocity=speedLimit;
		}
		else {
			masterVelocity=distancePID.getOutput(masterDistCur);
		}
		double slaveVelocity=masterVelocity-velocitySubtract;
		
		if (angleSetpointSign==1) {//turn right
			drive.tankDrive(masterVelocity, slaveVelocity);
		}
		else { //turn left
			drive.tankDrive(slaveVelocity, masterVelocity);
		}
		return (masterDistSP-masterDistCur);
	}
	
	//DRIVE DIST NO STOP
	public boolean driveDistanceNoStop(double dist, double angle,  boolean usingPIDLimit) {
		if (distanceReset) {
			navx.resetAngle();
			drive.resetDistance();
			ticks = 0;
			driveTurnPID.setSetpoint(angle);
			distanceReset = false;
			System.out.println("drive reset complete");
			return false;
		}
		double turnSpeed = driveTurnPID.getOutput(navx.getAngle() % 360);
		double curDistance = getOverallDistance();
		double driveSpeed;

		if (Math.abs(curDistance) + 80 > Math.abs(dist)) {
			if (dist > 0) {
				if (usingPIDLimit && speedLimit<0.55)
					driveSpeed=speedLimit;
				else
					driveSpeed = 0.55;
				}
			else
				driveSpeed = -0.55;
		} else if (dist > 0) {
			if (usingPIDLimit)
				driveSpeed=speedLimit;
			else
				driveSpeed = 1;
		}
		else
			driveSpeed = -1;
		System.out.println("driving. curDist= " + curDistance + "setPoint= " + dist +" ts= "+turnSpeed+" deltaAng= "
				+ (Math.abs(angle) - Math.abs(navx.getAngle() % 360)));
		if (turnSpeed < 0) {// turn left
			turnSpeed = -(turnSpeed);
			autoDrive(driveSpeed - turnSpeed, driveSpeed);
		} else { // turn right
			autoDrive(driveSpeed, driveSpeed - turnSpeed);
		}
		if (Math.abs(curDistance) > Math.abs(dist)) {
			drive.tankDrive(0, 0);
			distanceReset = true;
			return true;
		}
		return false;
	}
	
	//DRIVE DIST NOSTOP WITH RUNNABLE
	public boolean driveDistanceNoStop(double dist, double angle, boolean usingPIDLimit, Runnable func) {
		if (distanceReset) {
			navx.resetAngle();
			drive.resetDistance();
			driveTurnPID.setP(0.192);
			driveTurnPID.setMaxIOutput(0.19);
			driveTurnPID.setD(0.47);
			ticks = 0;
			driveTurnPID.setSetpoint(angle);
			//in here, we used to manually set the p value to 0.0172 but ONLY for the runnable. hopefully we dont need to
			//in here, we used to manually set the p value to 0.36 but ONLY for the runnable. hopefully we dont need to
			distanceReset = false;
			System.out.println("drive reset complete");
			return false;
		}
		double turnSpeed = driveTurnPID.getOutput(navx.getAngle() % 360);
		double curDistance = getOverallDistance();
		double driveSpeed;
		func.run();
		if (curDistance + 65 > dist)
			if (usingPIDLimit&&speedLimit<0.5)
				driveSpeed=speedLimit;
			else
				driveSpeed = 0.5;
		else
			if (usingPIDLimit)
				driveSpeed=speedLimit;
			else
				driveSpeed = 1;
		System.out.println("drDisNoStRunnable curDist= " + curDistance + "setPoint= " + dist + " deltaAng= "
				+ (Math.abs(angle) - Math.abs(navx.getAngle() % 360)));
		if (turnSpeed < 0) {// turn left
			turnSpeed = -(turnSpeed);
			autoDrive(driveSpeed - turnSpeed, driveSpeed);
		} else { // turn right
			autoDrive(driveSpeed, driveSpeed - turnSpeed);
		}
		if (curDistance > dist) {
			drive.tankDrive(0, 0);
			distanceReset = true;
			return true;
		}
		return false;
	}
	
	//DRIVE DIST
	public boolean driveDistance(double dist, double angle, double tol, boolean usingPIDLimit) {
		if (distanceReset) {
			navx.resetAngle();
			drive.resetDistance();
			distancePID.setSetpoint(dist);
			ticks = 0;
			driveTurnPID.setSetpoint(angle);
			
			if (usingPIDLimit)
				distancePID.setOutputLimits(speedLimit);
			else
				distancePID.setOutputLimits(1);
			 
			distanceReset = false;
			System.out.println("drive reset complete");
			return false;
		}
		double turnSpeed = driveTurnPID.getOutput(navx.getAngle() % 360);
		double curDistance = getOverallDistance();
		double driveSpeed = distancePID.getOutput(curDistance);
		System.out.println("driving. curDist= " + curDistance + "setPoint= " + dist + " deltaAng= "
				+ (angle - (navx.getAngle() % 360)));
		if (within(curDistance, dist, tol))
			ticks++;
		else
			ticks = 0;
		if ((within(curDistance, dist, tol)) && ticks > 20) {
			autoDrive(0, 0);
			distanceReset = true;
			System.out.println("driving complete");
			return true;
		} else {
			//turnSpeed=0;// REMEMBER TO REMOVE THIS AHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
			if (turnSpeed < 0) {// turn left
				turnSpeed = -(turnSpeed);
				autoDrive(driveSpeed - turnSpeed, driveSpeed);
			} else { // turn right
				autoDrive(driveSpeed, driveSpeed - turnSpeed);
			}
		}
		return false;
	}
	
	//ANGLE REL TURN LIFT UP NO SHOOT
	//this is tuned with the lift up, so it might be out of tune for lift down
	public boolean angleRelTurnLiftUpNoShoot(double setP, boolean usingPIDLimit) {
		if (angleReset) {
			totalTicks = 0;// test, delete this
			navx.resetAngle();
			Timer.delay(0.05);
			ticks = 0;
			angleReset = false;
			if (usingPIDLimit)
				stopTurnPID.setOutputLimits(speedLimit);
			else
				stopTurnPID.setOutputLimits(1);			
			stopTurnPID.setSetpoint(setP);
			
			
			
			System.out.println("turn reset complete");
			return false;
		} else {
			totalTicks++;// test, delete this
			double curAngle = navx.getAngle() % 360;
			double turnSpeed = stopTurnPID.getOutput(curAngle);
			if (within(curAngle, setP, 2.5)) {
				ticks++;
				turnSpeed = 0;
			} else
				ticks = 0;
			System.out.println("ticks " + ticks);
			if (ticks > 5) {
				angleReset = true;
				System.out.println("turn complete after ticks=" + totalTicks); // test, delete this
				autoDrive(0, 0);
				return true;
			} else {
				System.out.println("turning. cAn= " + curAngle + " setP= " + setP + " TS=" + turnSpeed + "totTicks= "
						+ totalTicks);

				autoDrive(turnSpeed, -turnSpeed);

			}
		}
		return false;
	}
	//ANGLE REL TURN LIFT UP WITH RUNNABLE
		//this is tuned with the lift up, so it might be out of tune for lift down
		public boolean angleRelTurnLiftUpRunnable(double setP, boolean usingPIDLimit, Runnable func) {
			if (angleReset) {
				totalTicks = 0;// test, delete this
				navx.resetAngle();
				Timer.delay(0.05);
				ticks = 0;
				angleReset = false;
				if (usingPIDLimit)
					stopTurnPID.setOutputLimits(speedLimit);
				else
					stopTurnPID.setOutputLimits(1);				
				stopTurnPID.setSetpoint(setP);
				System.out.println("turn reset complete");
				return false;
			} else {
				func.run();
				totalTicks++;// test, delete this
				double curAngle = navx.getAngle() % 360;
				double turnSpeed = stopTurnPID.getOutput(curAngle);
				if (within(curAngle, setP, 2.5)) {
					ticks++;
					turnSpeed = 0;
				} else
					ticks = 0;
				System.out.println("ticks " + ticks);
				if (ticks > 5) {
					angleReset = true;
					System.out.println("turn complete after ticks=" + totalTicks); // test, delete this
					autoDrive(0, 0);
					return true;
				} else {
					System.out.println("turning. cAn= " + curAngle + " setP= " + setP + " TS=" + turnSpeed + "totTicks= "
							+ totalTicks);

					autoDrive(turnSpeed, -turnSpeed);

				}
			}
			return false;
		}
	
	public boolean alignIntakeCube(double dist, double angleThresh, boolean usingPIDLimit) { //the only PID used is distance
		if (distanceReset) {
			navx.resetAngle();
			drive.resetDistance();
			distancePID.setSetpoint(dist);
			
			if (usingPIDLimit) {
				distancePID.setOutputLimits(speedLimit);
			}else {
				distancePID.setOutputLimits(1);
			}
			
			ticks = 0;
			totalTicks = 0; 
			distanceReset = false;
			System.out.println("align intake drive reset complete");
			return false;
		}
		double cubeAngleOffset = limelight.getXOffset();
		double turnSpeed = 1 - Math.abs(cubeAngleOffset / angleThresh);
		totalTicks++;
		System.out.println("alignIntake ticks= " + totalTicks);
		if (turnSpeed < 0)
			turnSpeed = 0;
		double curDistance = getOverallDistance();
		double driveSpeed = distancePID.getOutput(curDistance);
		System.out.println(cubeAngleOffset + ":" + turnSpeed);
		if (within(curDistance, dist, 30))
			ticks++;
		else
			ticks = 0;
		if ((within(curDistance, dist, 30)) && ticks > 30) {
			autoDrive(0, 0);
			distanceReset = true;
			return true;
		} else {
			if (cubeAngleOffset >= 0)// turn right
				autoDrive(driveSpeed, driveSpeed * turnSpeed);
			else { // turn left
				autoDrive(driveSpeed * turnSpeed, driveSpeed);
			}
		}
		return false;
	}

	private void autoDrive(double left, double right) {
		if (left > speedLimit)
			left = speedLimit;
		else if (left < -speedLimit)
			left = -speedLimit;

		if (right > speedLimit)
			right = speedLimit;
		else if (right < -speedLimit)
			right = -speedLimit;
		// }
		System.out.println("Left: " + left + "  Right: " + right);
		drive.tankDrive(speed * left, speed * right);
	}

	public void setSpeedLimit(double speedLimit) {
		this.speedLimit = Math.abs(speedLimit);
	}

	private boolean within(double angle, double setAngle, double thresh) {
		return (setAngle - thresh) < angle && (setAngle + thresh) > angle;
	}

	private double getOverallDistance() {
		return (drive.getLeftDistance() + drive.getRightDistance()) / 2;
	}
}
