package ca.warp7.robot.auto;

import static ca.warp7.robot.Constants.CUBE_DISTANCE_B;
import static ca.warp7.robot.Constants.CUBE_DISTANCE_M;

import ca.warp7.robot.Robot;
import ca.warp7.robot.subsystems.Drive;
import ca.warp7.robot.subsystems.Intake;
import ca.warp7.robot.subsystems.Lift;
import ca.warp7.robot.subsystems.Limelight;
import ca.warp7.robot.subsystems.Navx;

public class CustomFunctions {
	
	private Drive drive = Robot.drive;
	private Navx navx = Robot.navx;
	private Limelight limelight = Robot.limelight;
	private AutoFunctions autoFunc = Robot.auto.autoFunc; 
	private Intake intake = Robot.intake;
	private Lift lift = Robot.lift;
	
	public void turnToScale(double driveLocationTurn1,double angle1,double driveLocationTurn2,double angle2,double liftLocation, double liftScale) {
		double dist = getOverallDistance();
		if (withinMiddle(dist,driveLocationTurn1,20))
			autoFunc.wantedAngle = angle1;
		if (withinMiddle(dist,driveLocationTurn2,20))
			autoFunc.wantedAngle = angle2;
		
		if (withinMiddle(dist,liftLocation,20))
			lift.setLoc(liftScale);
	}
	
	public void driveTurn(double driveLocationTurn1,double angle1) {
		double dist = getOverallDistance();
		if (withinMiddle(dist,driveLocationTurn1,20))
			autoFunc.wantedAngle = angle1;
	}
	
	public void driveIntakeUp(double driveLocation, double liftLocation){
		double dist = getOverallDistance();
		if (withinMiddle(dist,driveLocation,20))
			lift.setLoc(liftLocation);
	}
	
	public void turnDrop(double angleOuttake, double angleDrop) {
		double curAngle = navx.getAngle() % 360;
		
		if (withinMiddle(curAngle,angleOuttake,15))
			intake.setSpeed(-0.8);
		else
			intake.setSpeed(0.2);
		
		if (withinMiddle(curAngle,angleDrop,15))
			lift.setLoc(0);
	}
	
	public void outtakeDistance_outtakeSpeed(double outDist,double outSpeed){
		double dist = getOverallDistance();
		if (withinFront(dist,outDist,25))
			intake.setSpeed(outSpeed);
		else
			intake.setSpeed(0.2);
	}
	
	public void outtakeAngle_outtakeSpeed_dropLift(double outAngle,double outSpeed, double angleDrop) {
		double curAngle = navx.getAngle() % 360;
		if (outAngle>0) {
			if (curAngle>outAngle) {
				intake.setSpeed(outSpeed);
				if (curAngle>angleDrop) {
					lift.setLoc(0);
				}
			}
		}
		else if (outAngle<0) {
			if (curAngle<outAngle) {
				intake.setSpeed(outSpeed);
				if (curAngle<angleDrop) {
					lift.setLoc(0);
				}
			}
		}
	}	
	
	private boolean withinMiddle(double angle, double setAngle, double thresh) {
		return (setAngle - thresh) < angle && (setAngle + thresh) > angle;
	}

	private boolean withinFront(double angle, double setAngle, double thresh) {
		return setAngle < angle && (setAngle + thresh) > angle;
	}

	private double getOverallDistance() {
		return (drive.getLeftDistance() + drive.getRightDistance()) / -2;
	}

	private double distancePredictor(double area) {
		return CUBE_DISTANCE_B - CUBE_DISTANCE_M * area;
	}
}
