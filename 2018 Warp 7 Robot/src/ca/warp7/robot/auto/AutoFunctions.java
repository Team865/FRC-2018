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
	private double distanceO;

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
		double driveSpeed = distancePID.getOutput(curDistance);
		System.out.println(
				"drDistRunning. curDist=" + curDistance + " deltaAng=" + (wantedAngle - (navx.getAngle() % 360)));
		if (within(curDistance, distanceO+dist, 0.5))
			ticks++;
		else
			ticks=0;
		if ((within(curDistance, distanceO+dist, 1)) && ticks > 20) {
			drive.tankDrive(0, 0);
			return true;
		} else {

			if (turnSpeed < 0) {// turn left
				turnSpeed = -(turnSpeed);
		
				drive.tankDrive(speed*driveSpeed-turnSpeed,speed*driveSpeed);
			} else { // turn right
				drive.tankDrive(speed*driveSpeed,speed*driveSpeed-turnSpeed);
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
				drive.tankDrive(0, 0);
				return true;
			}
		} else {
			drive.tankDrive(speed * turnSpeed, -speed * turnSpeed);
			ticks = 0;
		}
		return false;
	}

	public void setAngleTarget(double angle) {
		turnPID.setSetpoint(navx.getAngle() % 360 + angle);
		ticks = 0;
	}

	public void setDistanceTarget(double distance) {
		distancePID.setSetpoint(getOverallDistance() + distance);
		distanceO = getOverallDistance() + distance;
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

				drive.tankDrive(turnSpeed, -turnSpeed);

			}
		}
		return false;

	}
}
