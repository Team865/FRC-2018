package ca.warp7.robot;

import static ca.warp7.robot.Constants.*;

import ca.warp7.robot.auto.AutonomousBase;
import ca.warp7.robot.controls.ControlsBase;
import ca.warp7.robot.controls.DualRemote;
import ca.warp7.robot.misc.RTS;
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
		//climber = new Climber();
		
		//shutup >:(
		compressor = new Compressor(COMPRESSOR_PIN);
		
		driverStation = DriverStation.getInstance();
		//navx.startUpdateDisplacement(60);
		
		String jsonPaths = null;
		while (jsonPaths == null)
			SmartDashboard.getString("PathData", jsonPaths);
		
		auto = new AutonomousBase(jsonPaths);
	}
	
	public void autonomousInit(){
		String gameData = null;
		while (gameData == null)
			gameData = driverStation.getGameSpecificMessage();

		auto.autonomousInit(gameData);
		String jsonPaths = null;
		while (jsonPaths == null)
			SmartDashboard.getString("PathData", jsonPaths);
		
		auto.autonomousInit(gameData);
	}
	
	public void autonomousPeriodic(){
		auto.periodic();
		drive.tankDrive(-1,-1);
		SmartDashboard.putNumber("Right", drive.b);
		SmartDashboard.putNumber("Left", drive.a);
	}
	
	public void teleopInit() {
		//navx.startUpdateDisplacement(60);
		navx.resetDisplacement();
		compressor.setClosedLoopControl(false);
	}

	public void teleopPeriodic(){
        controls = new DualRemote();

		if(driverStation.isFMSAttached()){}
			
        
		 while (isOperatorControl() && isEnabled()) {
			controls.periodic();
			//drive.periodic();
			updateStuffs();
			Timer.delay(0.005);
		 }
	}
	
	public void updateStuffs() {
		SmartDashboard.putNumber("DispX", navx.getDispX());
		SmartDashboard.putNumber("DispY", navx.getDispY());
		
		RTS dispUpdater = navx.getDisplacementUpdater();
		SmartDashboard.putNumber(dispUpdater.getName()+": Hz", dispUpdater.getHz());
		SmartDashboard.putNumber("Hyp ahrs", Math.hypot(navx.getDispX(), navx.getDispY()));
	}
	
	
	public void disabledInit() {
		//if (navx.getDisplacementUpdater().isRunning())
			//navx.stopUpdateDisplacement();
	}

}

