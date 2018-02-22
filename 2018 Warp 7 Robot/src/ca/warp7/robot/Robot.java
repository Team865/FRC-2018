package ca.warp7.robot;

import static ca.warp7.robot.Constants.*;

import ca.warp7.robot.auto.AutonomousBase;
import ca.warp7.robot.controls.ControlsBase;
import ca.warp7.robot.controls.DualRemote;
import ca.warp7.robot.misc.RTS;
import ca.warp7.robot.subsystems.Climber;
import ca.warp7.robot.subsystems.Drive;
import ca.warp7.robot.subsystems.Intake;
import ca.warp7.robot.subsystems.Lift;
import ca.warp7.robot.subsystems.Limelight;
import ca.warp7.robot.subsystems.Navx;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Robot extends IterativeRobot  {
	
	public static Drive drive;
	public static Climber climber;
	public static Intake intake;
	public static Lift lift;
	
	private static AutonomousBase auto;
	private static ControlsBase controls;
	
	//shutup >:(
	public static Compressor compressor;
	
	public static Navx navx;
	
	public static Limelight limelight;
	
	private DriverStation driverStation;	
	
	private DigitalInput s4;
	private DigitalInput s5;
	private DigitalInput s6;
	
	private AnalogInput a0;
	private AnalogInput a1;
	private AnalogInput a2;
	private AnalogInput a3;
	
	public void robotInit() {
		System.out.println("Hello me is robit");
		
		drive = new Drive();
		navx = new Navx();
		intake = new Intake();
		lift = new Lift();
		climber = new Climber();
		
		//shutup >:(
		compressor = new Compressor(COMPRESSOR_PIN);
		
		driverStation = DriverStation.getInstance();
		//navx.startUpdateDisplacement(60);
		
		auto = new AutonomousBase();
		
		limelight = new Limelight("limelight");
		
		s4 = new DigitalInput(4);
		s5 = new DigitalInput(5);
		s6 = new DigitalInput(6);
		
		a0 = new AnalogInput(0);
		a1 = new AnalogInput(1);
		a2 = new AnalogInput(2);
		a3 = new AnalogInput(3);
	}
	
	public void autonomousInit(){
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
		gameData = "oklul";
		auto.autonomousInit(gameData,jsonPaths);
	
		
		//double 
		//while () {
			
		//}
		auto.periodic();
	}
	
	public void autonomousPeriodic(){
		
	}
	
	public void teleopInit() {
		//navx.startUpdateDisplacement(60);
		//navx.resetDisplacement();
		compressor.setClosedLoopControl(true);
		drive.resetDistance();
		//a();
	}

	public void teleopPeriodic(){
        controls = new DualRemote();

		if(driverStation.isFMSAttached()){}
			        
		 while (isOperatorControl() && isEnabled()) {
			controls.periodic();
			limelight.mutiPipeline();
			intake.periodic();
			
			//lift.periodic();
			//drive.periodic();
			SmartDashboard.putNumber("Lift", lift.getEncoderVal());
			SmartDashboard.putNumber("Analog 0", a0.getAverageVoltage());
			SmartDashboard.putNumber("Analog 1", a1.getAverageVoltage());
			SmartDashboard.putNumber("Analog 2", a2.getAverageVoltage());
			SmartDashboard.putNumber("Analog 3", a3.getAverageVoltage());
			SmartDashboard.putNumber("Analog 3", a3.getAverageVoltage());
			//updateStuffs();
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
			SmartDashboard.putNumber("LeftOffset", 1);
			SmartDashboard.putNumber("RightOffset", right/left);
		}else {
			SmartDashboard.putNumber("LeftOffset", offset);
			SmartDashboard.putNumber("RightOffset", 1);
		}
		
	}
	public void testPeriodic(){
		//SmartDashboard.putNumber("Left", drive.getLeftDistance());
		//SmartDashboard.putNumber("Right", drive.getRightDistance());
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

