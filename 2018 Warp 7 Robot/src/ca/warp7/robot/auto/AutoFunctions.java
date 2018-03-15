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
	private Intake intake = Robot.intake;
	private Lift lift = Robot.lift;

	private static final double speed = 1;
	private double speedLimit = 1;

	public AutoFunctions() { // 0.0123 for relturn tested 20 0.02 i original
		turnPID = new MiniPID(0.0155, 0.0029, 0.23); // at speed=1, p constant must be between 0.01 and 0.015
		turnPID.setOutputLimits(1);
		turnPID.setOutputRampRate(0.08);

		distancePID = new MiniPID(0.02, 0.0013, 0.23);
		distancePID.setOutputLimits(1);
		distancePID.setMaxIOutput(0.01);
	}

	public boolean driveDistance(double dist, double wantedAngle) {
		double turnSpeed = turnPID.getOutput(navx.getAngle() % 360, wantedAngle);
		double curDistance = getOverallDistance();
		double driveSpeed = distancePID.getOutput(curDistance,dist);
		System.out.println(
				"drDistRunning. curDist=" + dist + " deltaAng=" + (wantedAngle - (navx.getAngle() % 360)));
		if (within(curDistance, dist, 15))
			ticks++;
		else
			ticks=0;
		if ((within(curDistance, dist, 15)) && ticks > 20) {
			autoDrive(0, 0);
			return true;
		} else {
			if (turnSpeed < 0) {// turn left
				turnSpeed = -(turnSpeed);
		
				autoDrive(driveSpeed-turnSpeed,driveSpeed);
			} else { // turn right
				autoDrive(driveSpeed,driveSpeed-turnSpeed);
			}
		}
		return false;
	}

	public boolean angleRelTurn(double angle, double angleTolerance) {
		double curAngle = navx.getAngle() % 360;
		double turnSpeed = turnPID.getOutput(curAngle);

		if (within(curAngle, navx.getAngle() % 360 + angle, 2)) {
			ticks++;
			System.out.println("ticks=" + ticks);
			if (ticks > 200) {
				autoDrive(0, 0);
				return true;
			}
		} else {
			autoDrive(turnSpeed, -turnSpeed);
			ticks = 0;
		}
		return false;
	}

	public void setAngleTarget(double angle) {
		navx.resetAngle();
		turnPID.setSetpoint(navx.getAngle() % 360 + angle);
		ticks = 0;
	}

	public void setDistanceTarget(double distance) {
		drive.resetDistance();
		distancePID.setSetpoint(distance);
		ticks = 0;
	}

	private boolean within(double angle, double setAngle, double thresh) {
		return (setAngle - thresh) < angle && (setAngle + thresh) > angle;
	}

	private double getOverallDistance() {
		return (-drive.getLeftDistance() + -drive.getRightDistance()) / 2;
	}

	private double distancePredictor(double area) {
		return CUBE_DISTANCE_B - CUBE_DISTANCE_M * area;
	}

	public boolean angleRelTurn2(double setP, boolean reset) {
		double curAngle = navx.getAngle() % 360;
		double turnSpeed = 0;
		if (reset) {
			navx.resetAngle();
			turnPID.setSetpoint(setP);
			turnPID.setMaxIOutput(0.32);
			return true;
		} else {
			curAngle = navx.getAngle() % 360;
			turnSpeed = turnPID.getOutput(curAngle);
			if (within(curAngle, setP, 0.4)) {
				ticks++;
				turnSpeed = 0;
			} else
				ticks = 0;
			System.out.println("ticks " + ticks);
			if (ticks > 7) {
				return true;
			} else {
				System.out.println("cAn= " + curAngle + " setP= " + setP + " TS=" + turnSpeed + " pLeft= "
						+ speed * turnSpeed + " pRight= " + -speed * turnSpeed);

				autoDrive(turnSpeed, -turnSpeed);

			}
		}
		return false;

	}
	
	public boolean angleRelTurnAngleOutake(double setP, boolean reset, double angleOutake) {
		double curAngle = navx.getAngle() % 360;
		double turnSpeed = 0;
		
		if (within(curAngle,angleOutake,20))
			intake.setSpeed(-0.85);
		else
			intake.setSpeed(0.2);
		
		if (reset) {
			navx.resetAngle();
			turnPID.setSetpoint(setP);
			turnPID.setMaxIOutput(0.32);
			return true;
		} else {
			curAngle = navx.getAngle() % 360;
			turnSpeed = turnPID.getOutput(curAngle);
			if (within(curAngle, setP, 0.4)) {
				ticks++;
				turnSpeed = 0;
			} else
				ticks = 0;
			System.out.println("ticks " + ticks);
			if (ticks > 7) {
				return true;
			} else {
				System.out.println("cAn= " + curAngle + " setP= " + setP + " TS=" + turnSpeed + " pLeft= "
						+ speed * turnSpeed + " pRight= " + -speed * turnSpeed);

				autoDrive(turnSpeed, -turnSpeed);

			}
		}
		return false;

	}
	
	public boolean alignIntakeCube(double dist, double angleThresh) {		
		double cubeAngleOffset = limelight.getXOffset();
		double turnSpeed = 1-Math.abs(cubeAngleOffset/angleThresh);
		if (turnSpeed < 0)
			turnSpeed = 0;
		
		double curDistance = getOverallDistance();
		double driveSpeed = distancePID.getOutput(curDistance,dist);
		System.out.println(cubeAngleOffset + ":" + turnSpeed);
		if (within(curDistance, dist, 15))
			ticks++;
		else
			ticks=0;
		if ((within(curDistance, dist, 15)) && ticks > 20) {
			autoDrive(0, 0);
			return true;
		} else {
			if (cubeAngleOffset >= 0)//turn right
				autoDrive(driveSpeed,driveSpeed*turnSpeed);
			else { //turn left
				autoDrive(driveSpeed*turnSpeed,driveSpeed);
			}
		}
		return false;
	}
	
	private void autoDrive(double left, double right) {
		speedLimit = Math.abs(speedLimit);
		if (left > speedLimit)
			left = speedLimit;
		else if (left < -speedLimit)
			left = -speedLimit;
		
		if (right > speedLimit)
			right = speedLimit;
		else if (right < -speedLimit)
			right = -speedLimit;
		
		drive.tankDrive(speed*left,speed*right);
	}
	
	public void setSpeedLimit(double speedLimit) {
		this.speedLimit = speedLimit;
	}
}
