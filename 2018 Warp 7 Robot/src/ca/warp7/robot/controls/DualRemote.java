package ca.warp7.robot.controls;

import static ca.warp7.robot.controls.Control.DOWN;
import static ca.warp7.robot.controls.Control.PRESSED;
import static ca.warp7.robot.controls.Control.UP;
import static edu.wpi.first.wpilibj.GenericHID.Hand.kLeft;
import static edu.wpi.first.wpilibj.GenericHID.Hand.kRight;

import ca.warp7.robot.Robot;
import ca.warp7.robot.misc.DataPool;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

public class DualRemote extends ControlsBase {
	
	private boolean intakeTracking = false;
	
	@Override
	public void periodic() {
		if(driver.getTrigger(kRight) == DOWN){//intake
			intake.rampSpeed(0.75);
		}else if (driver.getTrigger(kLeft) == DOWN) {//out take
			intake.rampSpeed(-0.5);
		}else if (driver.getDpad(90) == DOWN){
			lift.overrideIntake = true;
			intake.rampSpeed(0.75);
		}else if (driver.getDpad(270) == DOWN){
			lift.overrideIntake = true;
			intake.rampSpeed(-0.75);
		}else{
			lift.overrideIntake = false;
			intake.rampSpeed(0);
		}
		
		if (driver.getDpad(180) == PRESSED)
			intakeTracking = !intakeTracking;
		
		if(driver.getStickButton(kRight) == PRESSED)
			drive.setDrivetrainReversed(!drive.driveReversed());
		
		if (driver.getAButton() == PRESSED)
			intake.pistonToggle();
		
		if(driver.getXButton() == PRESSED){
				Robot.limelight.switchCamera();
				System.out.println("switching camera");
		}
		
		if(operator.getXButton() == DOWN){
			lift.setLoc(0.11);
		}
		
		if(operator.getTrigger(kRight) == DOWN){
			lift.setLoc(0.4);
		}
		
		if(operator.getAButton() == DOWN)
			lift.setLoc(operator.getY(kLeft));
		
		if(operator.getBButton() == DOWN)
			climber.setSpeed(operator.getY(kRight)*-1);
		
		if(driver.getBButton() == DOWN){
			climber.setSpeed(driver.getY(kLeft)*-1);
		}else {
			//drive.tankDrive(driver.getY(Hand.kLeft), driver.getY(Hand.kLeft));
			if (intakeTracking && lift.isBottom() && intake.getSpeed() > 0.4)
				drive.trackCube(driver.getY(kLeft),4);
	        else
	        	drive.cheesyDrive(-driver.getX(kRight), driver.getY(kLeft), driver.getBumper(kLeft) == DOWN, false, driver.getBumper(kRight) != DOWN);
		}
	}
}
