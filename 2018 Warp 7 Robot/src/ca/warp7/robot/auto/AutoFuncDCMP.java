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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoFuncDCMP {
	private MiniPID driveTurnPID;
	private MiniPID stopTurnPID;
	private MiniPID distancePID;
	private int ticks;

	private Drive drive = Robot.drive;
	private Intake intake = Robot.intake;
	private Navx navx = Robot.navx;
	private Limelight limelight = Robot.limelight;
	private boolean angleReset;
	private boolean distanceReset;
	public int totalTicks = 0;// for testing, delete this
	private static final double speed = 1;
	private double speedLimit = 1;
	public double wantedAngle = 0;
	public boolean dropLift=false;
	public boolean overrideIntake=false;

	public AutoFuncDCMP() {
		
//DRIVE TURN PID -----------------------------
		//Practice bot values:    (0.016, 0.3, 0.39)
		//comp bot values: driveTurnPID = new MiniPID(0.016, 0.14, 0.46); //TUNE DRIVING WHILE TURNING WITH THESE
		driveTurnPID = new MiniPID(0.016, 0.3, 0.39); //TUNE DRIVING WHILE TURNING WITH THESE
		
		//MAX I OUTPUT IMPORTANT FOR OVERCOMING FRICTION BUT WILL CAUSE OVERSHOOTS IF D ISNT ADJUSTED
		driveTurnPID.setMaxIOutput(0.21); 
		//Practice bot value:     (0.21)
		
		driveTurnPID.setOutputLimits(1);
		driveTurnPID.setOutputRampRate(0.086); //set output ramp rate : prevents chatter
		
//STOP TURN PID------------------------------
		//Practice bot values:   (0.0175, 0.3, 0.27) //windsor d=0.28
		stopTurnPID = new MiniPID(0.0268, 0.3, 0.35); //TUNE TURNING ON THE SPOT WITHOUT DRIVING FORWARD/BACKWARD WITH THESE
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
		double turnSpeed = driveTurnPID.getOutput(navx.getAngle() % 360, angle);
		double curDistance = getOverallDistance();
		double driveSpeed = distancePID.getOutput(curDistance, dist);
		System.out.println("driving. curDist= " + curDistance + "setPoint= " + dist + " deltaAng= "
				+ (angle - (navx.getAngle() % 360)));
		if (within(curDistance, dist, 65)) {
			turnSpeed/=4;
		}
		if (within(curDistance, dist, 15)) {
			ticks++;
			turnSpeed/=2;
		}
		
		else
			ticks = 0;
		if ((within(curDistance, dist, 15)) && ticks > 7) {
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
	
	public boolean driveDistanceIntake(double dist, double angle) {
		if (distanceReset) {
			navx.resetAngle();
			Timer.delay(0.005);
			drive.resetDistance();
			distancePID.setSetpoint(dist);
			ticks = 0;
			driveTurnPID.setSetpoint(angle);
			distanceReset = false;
			intake.pistonToggle();
			intake.setSpeed(1);
			System.out.println("drive reset complete");
			return false;
		}
		SmartDashboard.putNumber("drive distanceL", drive.getLeftDistance());
		SmartDashboard.putNumber("delta Angle=", (angle - (navx.getAngle() % 360)));
		double turnSpeed = driveTurnPID.getOutput(navx.getAngle() % 360, angle);
		double curDistance = getOverallDistance();
		double driveSpeed = distancePID.getOutput(curDistance, dist);
		System.out.println("driving. curDist= " + curDistance + "setPoint= " + dist + " deltaAng= "
				+ (angle - (navx.getAngle() % 360)));
		if (within(curDistance, dist, 65)) {
			turnSpeed/=4;
			if(driveSpeed>0.3) {
			driveSpeed=0.3;
			}
		}
		if (within(curDistance, dist, 20)) {
			ticks++;
			turnSpeed/=2;
		}
		else
			ticks = 0;
		if ((within(curDistance, dist, 15)) && ticks > 20) {
			intake.pistonToggle();
			intake.setSpeed(0.6);
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
	public boolean angleRelTurnNoShoot(double setP) {
		if (angleReset) {
			totalTicks = 0;// test, delete this
			navx.resetAngle();
			Timer.delay(0.005);
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
	
	//ANGLE REL TURN LIFT UP NO SHOOT
		//this is tuned with the lift up, so it might be out of tune for lift down
		public boolean turnAngleShotspeedShotangleDropangle(double setP, double shotSpeed, double shotAngle, double dropAngle) {
			if (angleReset) {
				stopTurnPID.setD(0.45);
				totalTicks = 0;// test, delete this
				navx.resetAngle();
				Timer.delay(0.01);
				ticks = 0;
				angleReset = false;
				stopTurnPID.setSetpoint(setP);
				System.out.println("turn reset complete");
				dropLift=false;
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
				
				if (shotAngle>0) {
					if (curAngle>shotAngle) {
						intake.setSpeed(shotSpeed);
						if (!dropLift&&curAngle>dropAngle) {
							dropLift=true;
							Robot.lift.setLoc(0);
						}
					}
				}
				else if (shotAngle<0) {
					if (curAngle<shotAngle) {
						intake.setSpeed(shotSpeed);
						if (!dropLift&&curAngle<dropAngle) {
							dropLift=true;
							Robot.lift.setLoc(0);
						}
					}
				}
				
				if (ticks > 16) {
					angleReset = true;
					System.out.println("turn complete after ticks=" + totalTicks); // test, delete this
					autoDrive(0, 0);
					intake.setSpeed(0.8);
					dropLift=false;
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
