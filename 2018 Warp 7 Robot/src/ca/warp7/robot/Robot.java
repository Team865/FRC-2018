package ca.warp7.robot;

import static ca.warp7.robot.Constants.*;

import ca.warp7.robot.auto.AutonomousBase;
import ca.warp7.robot.controls.ControlsBase;
import ca.warp7.robot.controls.DualRemote;
import ca.warp7.robot.subsystems.Climber;
import ca.warp7.robot.subsystems.Drive;
import ca.warp7.robot.subsystems.Intake;
import ca.warp7.robot.subsystems.Lift;
import ca.warp7.robot.subsystems.Limelight;
import ca.warp7.robot.subsystems.Navx;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Robot extends IterativeRobot  {
	public static Limelight limelight;
	
	public static Drive drive;
	public static Climber climber;
	public static Intake intake;
	public static Lift lift;
	private static boolean isAutonomous;
	
	private static AutonomousBase auto;
	private static ControlsBase controls;
	
	//shutup >:(
	public static Compressor compressor;
	
	public static Navx navx;
	
	private DriverStation driverStation;	
	
	private AnalogInput a0;
	private AnalogInput a1;
	private AnalogInput a2;
	private AnalogInput a3;
	
	public void robotInit() {
		System.out.println("Hello me is robit");
		
		limelight = new Limelight();
		navx = new Navx();
		drive = new Drive();
		intake = new Intake();
		lift = new Lift();
		climber = new Climber();
		
		//shutup >:(
		compressor = new Compressor(COMPRESSOR_PIN);
		
		driverStation = DriverStation.getInstance();
		//navx.startUpdateDisplacement(60);
		
		isAutonomous = false;
		auto = new AutonomousBase();
		
		
		a0 = new AnalogInput(0);
		a1 = new AnalogInput(1);
		a2 = new AnalogInput(2);
		a3 = new AnalogInput(3);
	}
	
	public void autonomousInit(){
		isAutonomous = true;
		String jsonPaths = "None";
		/*if(!s4.get())
			jsonPaths = "Left";
		else if(!s5.get())
			jsonPaths = "Middle";
		else if(!s6.get())
			jsonPaths = "Right";			
		*/
		String gameData = null;
		//while (gameData == null)
		//	gameData = driverStation.getGameSpecificMessage();
		drive.resetDistance();
		navx.resetAngle();
		gameData = "LLL";
		auto.autonomousInit(gameData,jsonPaths);
		drive.tankDrive(0,0);
		//auto.periodic();
	}
	
	public void autonomousPeriodic(){
		
	}
	
	public void teleopInit() {
		isAutonomous = false;
		//navx.startUpdateDisplacement(60);
		//navx.resetDisplacement();
		compressor.setClosedLoopControl(true);
		drive.resetDistance();
		//a();
		limelightthing();
	}
	
	private void limelightthing() {
		while (isOperatorControl() && isEnabled()) {
			SmartDashboard.putNumber("limelight area",limelight.getArea());
		}
	}
	
	public void teleopPeriodic(){
        controls = new DualRemote();

		if(driverStation.isFMSAttached()){}
		double a = 0;
		 while (isOperatorControl() && isEnabled()) {
			controls.periodic();
			limelight.mutiPipeline();
			intake.periodic();
			double b = lift.getEncoderVal();
			if (a < b)
				a = b;
			SmartDashboard.putNumber("pipeline id", limelight.getPipeline());
			SmartDashboard.putBoolean("inake hasCube", intake.hasCube());
			lift.periodic();
			//drive.periodic();
			SmartDashboard.putNumber("Lift", a);
			SmartDashboard.putNumber("Drive Right Dist", drive.getRightDistance());
			SmartDashboard.putNumber("Analog 0", a0.getAverageVoltage());
			SmartDashboard.putNumber("Analog 1", a1.getAverageVoltage());
			SmartDashboard.putNumber("Analog 2", a2.getAverageVoltage());
			SmartDashboard.putNumber("Analog 3", a3.getAverageVoltage());
			SmartDashboard.putNumber("pitch", navx.getPitch());
			Timer.delay(0.005);
		 }
	}
	
	public void a() {
		double i=0;
		while (i<1) {
			Timer.delay(0.05);
			drive.tankDrive(i,i);
			i+=0.01;
		}
		drive.resetDistance();
		Timer.delay(5);
		while (i>0) {
			Timer.delay(0.05);
			drive.tankDrive(i,i);
			i-=0.01;
		}
		drive.tankDrive(0,0);
		Timer.delay(2);
		double left = drive.getLeftDistance();
		double right = drive.getRightDistance();
		double offset = left/right;
		if (offset >= 1) {
			SmartDashboard.putNumber("LeftOffset", right/left);
			SmartDashboard.putNumber("RightOffset", 1);
		}else {
			SmartDashboard.putNumber("LeftOffset", 1);
			SmartDashboard.putNumber("RightOffset", offset);
		}
		
	}
	
	public void disabledInit() {
		isAutonomous = false;
		//if (navx.getDisplacementUpdater().isRunning())
			//navx.stopUpdateDisplacement();
	}
	
	public static boolean isAutonomousActive(){
		return isAutonomous;
	}
}

