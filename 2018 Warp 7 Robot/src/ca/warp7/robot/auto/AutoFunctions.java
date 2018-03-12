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
	
	public AutoFunctions(){
		turnPID = new MiniPID(0.0123,0,0.04); //at speed=1, p constant must be between 0.01 and 0.015
		turnPID.setOutputLimits(1);
		
		distancePID = new MiniPID(0.02,0,0);
		distancePID.setOutputLimits(1);
	}
	
	public boolean driveDistance(double dist, double wantedAngle) {
		double turnSpeed = turnPID.getOutput(navx.getAngle()%360);
		double curDistance = getOverallDistance();
		double driveSpeed = distancePID.getOutput(curDistance);
		if (within(curDistance,dist,2)) 
			ticks++;
		if ((within(curDistance,dist,2)) && ticks > 200) {
			drive.tankDrive(0,0);
			return true;
		}
		else {
			drive.tankDrive(speed*driveSpeed,speed*driveSpeed);
			/*
			if (turnSpeed >= 0 )//turn right
				drive.tankDrive(speed*driveSpeed,speed*driveSpeed+(turnSpeed-1));
			else //turn left
				drive.tankDrive(speed*driveSpeed+(turnSpeed-1),speed*driveSpeed);
			*/
			ticks=0;
		}
		return false;
	}
	
	public boolean angleRelTurn(double angle, double angleTolerance) {
		double curAngle = navx.getAngle()%360;
		double turnSpeed = turnPID.getOutput(curAngle);
		if (within(curAngle,angle,2)) {
			ticks++;
			if (ticks > 200) {
				drive.tankDrive(0,0);
				return true;
			}
		}
		else {
			drive.tankDrive(speed*turnSpeed,-speed*turnSpeed);
			ticks=0;
		}
		return false;
	}
	
	public void setAngleTarget(double angle) {
		turnPID.setSetpoint(navx.getAngle()%360+angle);
		ticks=0;
	}
	
	public void setDistanceTarget(double distance) {
		distancePID.setSetpoint(getOverallDistance()+distance);
		ticks=0;
	}
	
	private boolean within(double angle,double setAngle, double thresh) {
		return (setAngle-thresh)<angle && (setAngle+thresh)>angle;
	}
	
	private double getOverallDistance() {
		return (-drive.getLeftDistance() + -drive.getRightDistance())/2;
	}
	
	private double distancePredictor(double area){
		return CUBE_DISTANCE_B - CUBE_DISTANCE_M * area;
	}
}
