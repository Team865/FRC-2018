package ca.warp7.robot;

import static ca.warp7.robot.Constants.*;

import ca.warp7.robot.auto.AutonomousBase;
import ca.warp7.robot.controls.ControlsBase;
import ca.warp7.robot.controls.DualRemote;
import ca.warp7.robot.subsystems.Climber;
import ca.warp7.robot.subsystems.Drive;
import ca.warp7.robot.subsystems.Navx;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Robot extends IterativeRobot  {
	
	public static Drive drive;
	public static Climber climber;
	
	private static AutonomousBase auto;
	private static ControlsBase controls;
	
	//shutup >:(
	public static Compressor compressor;
	
	public static Navx navx;
	
	private DriverStation driverStation;		
	
	public void robotInit() {
		System.out.println("Hello me is robit");
		
		drive = new Drive();
		navx = new Navx();
		auto = new AutonomousBase(drive,navx);
		//climber = new Climber();
		
		//shutup >:(
		compressor = new Compressor(COMPRESSOR_PIN);
		
		driverStation = DriverStation.getInstance();
		
	}
	
	public void autonomousInit(){
		String gameData = driverStation.getGameSpecificMessage();
		auto.autonomousInit(gameData);
	}
	
	public void autonomousPeriodic(){
		auto.periodic();
	}
	
	public void teleopInit() {
		navx.startUpdateDisplacement(60);
		compressor.setClosedLoopControl(false);
	}

	public void teleopPeriodic(){
        controls = new DualRemote();

		if(driverStation.isFMSAttached()){}
			
        
		 while (isOperatorControl() && isEnabled()) {
			controls.periodic();
			drive.periodic();
			SmartDashboard.putNumber("DispX", navx.getDispX());
			SmartDashboard.putNumber("DispY", navx.getDispY());
			Timer.delay(0.005);
		 }
	}
	
	public void disabledInit() {
		navx.stopUpdateDisplacement();
	}

}

