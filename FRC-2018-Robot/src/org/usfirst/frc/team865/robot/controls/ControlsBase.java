package org.usfirst.frc.team865.robot.controls;

import static org.usfirst.frc.team865.robot.Constants.DRIVER_ID;
import static org.usfirst.frc.team865.robot.Constants.OPERATOR_ID;

import org.usfirst.frc.team865.robot.Robot;
import org.usfirst.frc.team865.robot.misc.DataPool;
import org.usfirst.frc.team865.robot.subsystems.Climber;
import org.usfirst.frc.team865.robot.subsystems.Drive;

import edu.wpi.first.wpilibj.Timer;

public abstract class ControlsBase {

	public static DataPool controlPool = new DataPool("controls");
	
	protected XboxControllerPlus driver;
	protected XboxControllerPlus operator;
	protected Climber climber;
	protected Drive drive;
	
	
	public ControlsBase(){
		driver = new XboxControllerPlus(DRIVER_ID);
		operator = new XboxControllerPlus(OPERATOR_ID);
		
		climber = Robot.climber;
		drive = Robot.drive;
	}
	
	abstract public void periodic();
	
	protected double timer = -1;
	protected boolean timePassed(double seconds) {
		if(timer <= 0)
			timer = Timer.getFPGATimestamp();
		
		if(Timer.getFPGATimestamp() - timer >= seconds){
			timer = -1;
			return true;
		}else{
			return false;
		}
	}
    
}