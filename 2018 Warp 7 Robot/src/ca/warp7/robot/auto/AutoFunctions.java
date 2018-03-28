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
	private MiniPID turnPID;
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

	// friday march 16ish (0.009,0.01, 0.21);
	public AutoFunctions() { // march 16 working = 0.0155, 0.0029, 0.23
		turnPID = new MiniPID(0.016, 0.3, 0.22);//0.016, 0.2, 0.22
		turnPID.setOutputLimits(1);
		turnPID.setOutputRampRate(0.086);
		turnPID.setMaxIOutput(0.175);

		distancePID = new MiniPID(0.02, 0.0013, 0.23);
		distancePID.setOutputLimits(1);
		distancePID.setMaxIOutput(0.01);

		angleReset = true;
		distanceReset = true;
	}

	public boolean driveDistanceNoStop(double dist, double angle) {
		if (distanceReset) {
			navx.resetAngle();
			drive.resetDistance();
			ticks = 0;
			turnPID.setSetpoint(angle); 
			turnPID.setP(0.02);//0.02 0.0013 0.22
			turnPID.setD(0.3);
			turnPID.setMaxIOutput(0.21);
			distanceReset = false;
			System.out.println("drive reset complete");
			// turn pid i term fix
			return false;
		}
		double turnSpeed = turnPID.getOutput(navx.getAngle() % 360);
		double curDistance = getOverallDistance();
		double driveSpeed;

		if (Math.abs(curDistance)+80 > Math.abs(dist)) {
			if (dist>0)
				driveSpeed = 0.55;
			else
				driveSpeed=-0.55;
		}	
		else
			if (dist>0)
				driveSpeed = 1;
			else
				driveSpeed=-1;
		System.out.println("driving. curDist= " + curDistance + "setPoint= " + dist + " deltaAng= "
				+ (Math.abs(angle) - Math.abs(navx.getAngle() % 360)));
		if (turnSpeed < 0) {// turn left
			turnSpeed = -(turnSpeed);
			autoDrive(driveSpeed - turnSpeed, driveSpeed);
		} else { // turn right
			autoDrive(driveSpeed, driveSpeed -turnSpeed);
		}
		if (Math.abs(curDistance) > Math.abs(dist)) {
			drive.tankDrive(0, 0);
			distanceReset=true;
			return true;
		}
		return false;
	}

	public boolean driveDistanceNoStop(double dist, double angle, Runnable func) {
		if (distanceReset) {
			navx.resetAngle();
			drive.resetDistance();
			ticks = 0;
			turnPID.setSetpoint(angle);
			turnPID.setP(0.0172);
			turnPID.setD(0.36);
			turnPID.setMaxIOutput(0.19);
			distanceReset = false;
			System.out.println("drive reset complete");
			// turn pid i term fix
			return false;
		}
		double turnSpeed = turnPID.getOutput(navx.getAngle() % 360);
		double curDistance = getOverallDistance();
		double driveSpeed;
		func.run();
		if (curDistance + 85 > dist)
			driveSpeed = 0.35;
		else
			driveSpeed = 1;
		System.out.println("driving. curDist= " + curDistance + "setPoint= " + dist + " deltaAng= "
				+ (Math.abs(angle) - Math.abs(navx.getAngle() % 360)));
		if (turnSpeed < 0) {// turn left
			turnSpeed = -(turnSpeed);
			autoDrive(driveSpeed - turnSpeed, driveSpeed);
		} else { // turn right
			autoDrive(driveSpeed, driveSpeed -turnSpeed);
		}
		if (curDistance > dist) {
			drive.tankDrive(0, 0);
			distanceReset=true;
			return true;
		}
		return false;
	}
	
	public boolean driveDistance(double dist) {
		if (distanceReset) {
			navx.resetAngle();
			drive.resetDistance();
			distancePID.setSetpoint(dist);
			ticks = 0;
			turnPID.setSetpoint(0);
			distanceReset = false;
			System.out.println("drive reset complete");
			// turn pid i term fix
			return false;
		}
		double turnSpeed = turnPID.getOutput(navx.getAngle() % 360, 0);
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

	public boolean driveDistance(double dist, Runnable func) {
		if (distanceReset) {
			navx.resetAngle();
			drive.resetDistance();
			distancePID.setSetpoint(dist);
			ticks = 0;
			turnPID.setSetpoint(0);
			distanceReset = false;
			wantedAngle = 0;
			System.out.println("drive reset complete");
			// turn pid i term fix
			return false;
		}
		func.run();
		double turnSpeed = turnPID.getOutput(navx.getAngle() % 360, wantedAngle);
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

	public boolean angleRelTurn(double setP) {
		if (angleReset) {
			totalTicks = 0;// test, delete this
			navx.resetAngle();
			Timer.delay(0.05);
			ticks = 0;
			turnPID.setSetpoint(setP);
			angleReset = false;
			System.out.println("turn reset complete");
			//delete this if u want turning tuned for the march 24 LLL RRR 2cube scale switch
			turnPID.setP(0.0298); //0.0347 0.0017 0.2597 --- //0.0172
			turnPID.setD(0.425); // --- 0.36
			return false;
		} else {
			totalTicks++;// test, delete this
			double curAngle = navx.getAngle() % 360;
			double turnSpeed = turnPID.getOutput(curAngle);
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
	
	public boolean angleRelTurnNoStop(double setP, Runnable func) {
		if (angleReset) {
			totalTicks = 0;// test, delete this
			navx.resetAngle();
			Timer.delay(0.05);
			turnPID.setSetpoint(setP);
			angleReset = false;
			System.out.println("turn reset complete");
			return false;
		} else {
			func.run();
			totalTicks++;// test, delete this
			double curAngle = navx.getAngle() % 360;
			double turnSpeed = turnPID.getOutput(curAngle);
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
			turnPID.setSetpoint(setP);
			angleReset = false;
			System.out.println("turn reset complete");
			return false;
		} else {
			func.run();
			totalTicks++;// test, delete this
			double curAngle = navx.getAngle() % 360;
			double turnSpeed = turnPID.getOutput(curAngle);
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
	public boolean angleRelTurnLiftUpNoShoot(double setP) {
		if (angleReset) {
			totalTicks = 0;// test, delete this
			navx.resetAngle();
			Timer.delay(0.05);
			ticks = 0;
			turnPID.setSetpoint(setP);
			turnPID.setP(0.0175);
			turnPID.setD(0.27);
			turnPID.setMaxIOutput(0.24);
			angleReset = false;
			System.out.println("turn reset complete");
			return false;
		} else {
			totalTicks++;// test, delete this
			double curAngle = navx.getAngle() % 360;
			double turnSpeed = turnPID.getOutput(curAngle);
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
	
	public boolean alignIntakeCube(double dist, double angleThresh) {
		if (distanceReset) {
			navx.resetAngle();
			drive.resetDistance();
			distancePID.setSetpoint(dist);
			ticks = 0;
			totalTicks=0;
			distanceReset = false;
			System.out.println("align intake drive reset complete");
			return false;
		}
		double cubeAngleOffset = limelight.getXOffset();
		double turnSpeed = 1 - Math.abs(cubeAngleOffset / angleThresh);
		totalTicks++;
		System.out.println("alignIntake ticks= "+totalTicks);
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

		System.out.println("Left: "+left+ "  Right: "+ right);
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