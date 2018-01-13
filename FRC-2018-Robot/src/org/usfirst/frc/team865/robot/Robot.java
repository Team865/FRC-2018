package org.usfirst.frc.team865.robot;

import static org.usfirst.frc.team865.robot.Constants.*;

import org.usfirst.frc.team865.robot.auto.AutonomousBase;
import org.usfirst.frc.team865.robot.controls.ControlsBase;
import org.usfirst.frc.team865.robot.controls.DualRemote;
import org.usfirst.frc.team865.robot.subsystems.Climber;
import org.usfirst.frc.team865.robot.subsystems.Drive;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


@SuppressWarnings("deprecation")
public class Robot extends SampleRobot {
	
	public static Drive drive;
	public static Climber climber;
	
	private static AutonomousBase auto;
	private static ControlsBase controls;
	
	//shutup >:(
	public static Compressor compressor;
	
	private DriverStation driverStation;
	
	private DigitalInput s4;
	private DigitalInput s5;
	private DigitalInput s6;
	private DigitalInput s7;
	private DigitalInput s8;
	
	
	@Override
	public void robotInit() {
		System.out.println("Hello me is robit");
		
		drive = new Drive();
		climber = new Climber();
		
		//shutup >:(
		compressor = new Compressor(COMPRESSOR_PIN);
		
		driverStation = DriverStation.getInstance();
		
	}

	@Override
	public void autonomous() {
	
	}
	@Override
	public void operatorControl() {
		controls = new DualRemote();
		/*
		if(driverStation.isFMSAttached())
			compressor.setClosedLoopControl(false);
        else 
        	compressor.setClosedLoopControl(true);
        */
		 while (isOperatorControl() && isEnabled()) {
			 controls.periodic();
			 periodic();
			 
	         Timer.delay(0.005);
		 }
	
	}
	@Override
	public void test() {
	
	}
	
	public void disabled(){
		while (!isEnabled()) {
			periodic();
			Timer.delay(0.005);
		}
	}
	
	public void periodic(){
		drive.periodic();
		compressor.setClosedLoopControl(false);
		/*
		try{
			light.set(true);
		}catch(Exception e){
			light.set(false);
		}
		
		try{
			autoPool.logInt("Step", auto.step);
		}catch(Exception e){
			autoPool.logInt("Step", 0);
		}
		
		autoPool.logData("I AM HERE", "true");
		
		if(!s4.get())
			autoPool.logInt("Switch Key", 4);
		else if(!s5.get())
			autoPool.logInt("Switch Key", 5);			
		else if(!s6.get())
			autoPool.logInt("Switch Key", 6);
		else if(!s7.get())
			autoPool.logInt("Switch Key", 7);
		else if(!s8.get())
			autoPool.logInt("Switch Key", 8);
		else
			autoPool.logInt("Switch Key", -1);
		
		DataPool.collectAllData();
		*/
	}
}
