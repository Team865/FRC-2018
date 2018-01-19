package ca.warp7.robot.controls;

import static ca.warp7.robot.Constants.pixelOffset;
import static ca.warp7.robot.controls.Control.DOWN;
import static ca.warp7.robot.controls.Control.PRESSED;
import static ca.warp7.robot.controls.Control.UP;
import static edu.wpi.first.wpilibj.GenericHID.Hand.kLeft;
import static edu.wpi.first.wpilibj.GenericHID.Hand.kRight;

import ca.warp7.robot.misc.DataPool;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

public class DualRemote extends ControlsBase {

	private double rpm = 4450;
	
	
	public DualRemote() {
		super();
		
		rpm = 4450;
	}
	
	@Override
	public void periodic() {
		if(driver.getTrigger(kLeft) == UP){ // are we doing shooter auto stuff
			
			if(driver.getStickButton(kRight) == PRESSED)
				drive.setDrivetrainReversed(!drive.driveReversed());
				
			if(driver.getTrigger(kRight) == DOWN){
				
			}else if(driver.getTrigger(kRight) == UP){
				
			}
			
			if(operator.getBumper(kRight) == DOWN){
				
			}else if(operator.getBumper(kLeft) == DOWN){
				
			}else{
				
			}
			
			if(operator.getBackButton() == PRESSED){
				
			}
			
			if(operator.getBButton() == DOWN){
				
			}
			else if(operator.getTrigger(kLeft) == DOWN){
				
			}
			else if(operator.getTrigger(kLeft) == UP){
				
			}
			
			if (operator.getDpad(90) == DOWN){
				
			}else if(operator.getAButton() == DOWN){
				
			}else if(operator.getAButton() == UP){
				
			}
			
			 //drive.tankDrive(driver.getY(Hand.kLeft), driver.getY(Hand.kLeft));
			drive.cheesyDrive(-driver.getX(kRight), driver.getY(kLeft), driver.getBumper(kLeft) == DOWN, false, driver.getBumper(kRight) != DOWN);
		}else if(operator.getYButton() == DOWN){
			
			
			if(operator.getBButton() == DOWN){
				
			}
			else if(operator.getTrigger(kLeft) == DOWN){
				
			}
			else if(operator.getTrigger(kLeft) == UP){
				
			}
			
			try{
				boolean found = DataPool.getBooleanData("vision", "D_found");
				if(found){
					drive.autoMove(Math.min(0.75, Math.max(DataPool.getDoubleData("vision", "D_left"), -0.75)), Math.min(0.75, Math.max(DataPool.getDoubleData("vision", "D_right"), -0.75)));
				}else{
					drive.cheesyDrive(-driver.getX(kRight), driver.getY(kLeft), driver.getBumper(kLeft) == DOWN, driver.getTrigger(kLeft) == DOWN, false);
				}
			}catch(Exception e){
				System.err.println("WARNING JETSON FAILED");
				drive.cheesyDrive(-driver.getX(kRight), driver.getY(kLeft), driver.getBumper(kLeft) == DOWN, driver.getTrigger(kLeft) == DOWN, false);
			}
		}else{
			
			if(operator.getBButton() == DOWN){
				
			}
			else if(operator.getTrigger(kLeft) == DOWN){
				
			}
			else if(operator.getTrigger(kLeft) == UP){
				
			}
			
			try{
				boolean found = DataPool.getBooleanData("vision", "S_found");
				if(found){
					drive.autoMove(DataPool.getDoubleData("vision", "S_left"), DataPool.getDoubleData("vision", "S_right"));
					double pixelHeight = DataPool.getDoubleData("vision", "S_dist")+pixelOffset;
					if(pixelHeight > 534 && found){
						rpm = 4425;
						driver.setRumble(RumbleType.kLeftRumble, 1.0);
						driver.setRumble(RumbleType.kRightRumble, 1.0);
						operator.setRumble(RumbleType.kLeftRumble, 1.0);
						operator.setRumble(RumbleType.kRightRumble, 1.0);
					}else if(pixelHeight < 312 && found){
						rpm = 5350;
						driver.setRumble(RumbleType.kLeftRumble, 1.0);
						driver.setRumble(RumbleType.kRightRumble, 1.0);
						operator.setRumble(RumbleType.kLeftRumble, 1.0);
						operator.setRumble(RumbleType.kRightRumble, 1.0);
					}else if(found){
						rpm = 0.018*Math.pow(pixelHeight, 2)-19.579*pixelHeight+9675.03;
						driver.setRumble(RumbleType.kLeftRumble, 0.0);
						driver.setRumble(RumbleType.kRightRumble, 0.0);
						operator.setRumble(RumbleType.kLeftRumble, 0.0);
						operator.setRumble(RumbleType.kRightRumble, 0.0);
					}else{
						rpm = 4706;
						driver.setRumble(RumbleType.kLeftRumble, 0.0);
						driver.setRumble(RumbleType.kRightRumble, 0.0);
						operator.setRumble(RumbleType.kLeftRumble, 0.0);
						operator.setRumble(RumbleType.kRightRumble, 0.0);
					}
				}else{
					drive.cheesyDrive(-driver.getX(kRight), driver.getY(kLeft), driver.getBumper(kLeft) == DOWN, driver.getTrigger(kLeft) == DOWN, true);
					rpm = 4706;
					driver.setRumble(RumbleType.kLeftRumble, 0.0);
					driver.setRumble(RumbleType.kRightRumble, 0.0);
					operator.setRumble(RumbleType.kLeftRumble, 0.0);
					operator.setRumble(RumbleType.kRightRumble, 0.0);
				}
			}catch(Exception e){
				System.err.println("WARNING JETSON FAILED");
				rpm = 4706;
				driver.setRumble(RumbleType.kLeftRumble, 0.0);
				driver.setRumble(RumbleType.kRightRumble, 0.0);
				operator.setRumble(RumbleType.kLeftRumble, 0.0);
				operator.setRumble(RumbleType.kRightRumble, 0.0);
			}
			
		}
	}
}
