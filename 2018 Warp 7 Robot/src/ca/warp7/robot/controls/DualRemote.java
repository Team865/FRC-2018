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
	
	@Override
	public void periodic() {
		if(driver.getTrigger(kRight) == DOWN){//intake
			intake.rampSpeed(0.75);
		}else if (driver.getTrigger(kLeft) == DOWN) {//out take
			intake.rampSpeed(-0.5);
		}else if (driver.getDpad(90) == DOWN){
			intake.setSpeedRev(0.75);
		}else if (driver.getDpad(270) == DOWN){
			intake.setSpeedRev(-0.75);
		}else{
			intake.rampSpeed(0);
		}
		
		if(driver.getStickButton(kRight) == PRESSED)
			drive.setDrivetrainReversed(!drive.driveReversed());
		
		if (driver.getAButton() == PRESSED)
			intake.pistonToggle();
		
		if(driver.getXButton() == PRESSED){
				Robot.limelight.switchCamera();
				System.out.println("switching camera");
		}
		
		if(operator.getBackButton() == PRESSED){
			
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
			drive.cheesyDrive(-driver.getX(kRight), driver.getY(kLeft), driver.getBumper(kLeft) == DOWN, false, driver.getBumper(kRight) != DOWN);
		}
	}
}
