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
	private int ticks;

	private Drive drive = Robot.drive;
	private Navx navx = Robot.navx;
	private Limelight limelight = Robot.limelight;
	private boolean angleReset;
	private boolean distanceReset;
	public int totalTicks = 0;// for testing, delete this
	private static final double speed = 1;
	private double speedLimit = 1;
	public double wantedAngle = 0;

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
		
//DISTANCE ---------------------------------
		
		//distancePID is used for calculating how fast to drive forward
		//only used a couple times in the autos because driveDistanceNoStop sets its own drive speed
		//tuned practice bot:    (0.02, 0.0013, 0.22)
		distancePID = new MiniPID(0.02, 0.0013, 0.23);
		distancePID.setOutputLimits(1);
		//Practice bot value:    (0.01)
		distancePID.setMaxIOutput(0.01); //MAX I OUTPUT: stacks up too high if uncapped and causes overshoots

		angleReset = true;
		distanceReset = true;
	}
	
	//DRIVE DIST NO STOP
	public boolean driveDistanceNoStop(double dist, double angle) {
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
			if (dist > 0)
				driveSpeed = 0.55;
			else
				driveSpeed = -0.55;
		} else if (dist > 0)
			driveSpeed = 1;
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
	public boolean driveDistanceNoStop(double dist, double angle, Runnable func) {
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
			driveSpeed = 0.5;
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
	public boolean driveDistance(double dist, double angle) {
		if (distanceReset) {
			navx.resetAngle();
			drive.resetDistance();
			distancePID.setSetpoint(dist);
			ticks = 0;
			driveTurnPID.setSetpoint(angle);
			distanceReset = false;
			System.out.println("drive reset complete");
			return false;
		}
		double turnSpeed = driveTurnPID.getOutput(navx.getAngle() % 360, 0);
		double curDistance = getOverallDistance();
		double driveSpeed = distancePID.getOutput(curDistance, dist);
		System.out.println("driving. curDist= " + curDistance + "setPoint= " + dist + " deltaAng= "
				+ (0 - (navx.getAngle() % 360)));
		if (within(curDistance, dist, 15))
			ticks++;
		else
			ticks = 0;
		if ((within(curDistance, dist, 15)) && ticks > 20) {
			autoDrive(0, 0);
			distanceReset = true;
			System.out.println("driving complete");
			return true;
		} else {
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
	public boolean angleRelTurnLiftUpNoShoot(double setP) {
		if (angleReset) {
			totalTicks = 0;// test, delete this
			navx.resetAngle();
			Timer.delay(0.05);
			ticks = 0;
			angleReset = false;
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

	public boolean alignIntakeCube(double dist, double angleThresh) { //the only PID used is distance
		if (distanceReset) {
			navx.resetAngle();
			drive.resetDistance();
			distancePID.setSetpoint(dist);
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
// UNUSED OLD METHODS BELOW HERE. FOR REFERENCE.
/*
 -----Old AngleRelTurn, not used
 
	public boolean angleRelTurn(double setP) {
		if (angleReset) {
			totalTicks = 0;// test, delete this
			navx.resetAngle();
			Timer.delay(0.05);
			ticks = 0;
			stopTurnPID.setSetpoint(setP);
			angleReset = false;
			System.out.println("turn reset complete");
			// delete this if u want turning tuned for the march 24 LLL RRR 2cube scale
			// switch
			stopTurnPID.setP(0.0298); // 0.0347 0.0017 0.2597 --- //0.0172
			stopTurnPID.setD(0.425); // --- 0.36
			return false;
		} else {
			totalTicks++;// test, delete this
			double curAngle = navx.getAngle() % 360;
			double turnSpeed = stopTurnPID.getOutput(curAngle);
			if (within(curAngle, setP, 1.25)) {
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
 
DRIVE DISTANCE RUNNABLE. -------------------------
public boolean driveDistance(double dist, Runnable func) {
	if (distanceReset) {
		navx.resetAngle();
		drive.resetDistance();
		distancePID.setSetpoint(dist);
		ticks = 0;
		driveTurnPID.setSetpoint(0);
		distanceReset = false;
		wantedAngle = 0;
		System.out.println("drive reset complete");
		// turn pid i term fix
		return false;
	}
	func.run();
	double turnSpeed = driveTurnPID.getOutput(navx.getAngle() % 360, wantedAngle);
	double curDistance = getOverallDistance();
	double driveSpeed = distancePID.getOutput(curDistance, dist);
	System.out.println("driving. curDist= " + curDistance + "setPoint= " + dist + " deltaAng= "
			+ (0 - (navx.getAngle() % 360)));
	if (within(curDistance, dist, 15))
		ticks++;
	else
		ticks = 0;
	if ((within(curDistance, dist, 15)) && ticks > 20) {
		autoDrive(0, 0);
		distanceReset = true;
		System.out.println("driving complete");
		return true;
	} else {
		if (turnSpeed < 0) {// turn left
			turnSpeed = -(turnSpeed);

			autoDrive(driveSpeed - turnSpeed, driveSpeed);
		} else { // turn right
			autoDrive(driveSpeed, driveSpeed - turnSpeed);
		}
	}
	return false;
}
*/
/*
ANGLE REL TURN METHODS. OBSELETE. WE ONLY DO REL TURN ON THE SPOT WHEN THE LIFT IS UP, SO THE angleRelTurnLiftUpNoShoot
may or may not be tuned for when the lift is down. dont try without testing first.

this is calling the old turn method, needs to be switched over to stopTurn if we want to use it.

public boolean angleRelTurnNoStop(double setP, Runnable func) {
	if (angleReset) {
		totalTicks = 0;// test, delete this
		navx.resetAngle();
		Timer.delay(0.05);
		driveTurnPID.setSetpoint(setP);
		angleReset = false;
		System.out.println("turn reset complete");
		return false;
	} else {
		func.run();
		totalTicks++;// test, delete this
		double curAngle = navx.getAngle() % 360;
		double turnSpeed = driveTurnPID.getOutput(curAngle);
		if (within(curAngle, setP, 1.25)) {
			ticks++;
			turnSpeed = 0;
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


public boolean angleRelTurn(double setP, Runnable func) {
	if (angleReset) {
		totalTicks = 0;// test, delete this
		navx.resetAngle();
		Timer.delay(0.05);
		ticks = 0;
		driveTurnPID.setSetpoint(setP);
		angleReset = false;
		System.out.println("turn reset complete");
		return false;
	} else {
		func.run();
		totalTicks++;// test, delete this
		double curAngle = navx.getAngle() % 360;
		double turnSpeed = driveTurnPID.getOutput(curAngle);
		if (within(curAngle, setP, 1.25)) {
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
*/