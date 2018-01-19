package ca.warp7.robot;

import static ca.warp7.robot.Constants.*;

import ca.warp7.robot.auto.AutonomousBase;
import ca.warp7.robot.controls.ControlsBase;
import ca.warp7.robot.controls.DualRemote;
import ca.warp7.robot.subsystems.Climber;
import ca.warp7.robot.subsystems.Drive;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobotBase;
import edu.wpi.first.wpilibj.Timer;


public class Robot extends IterativeRobotBase {
	
	public static Drive drive;
	public static Climber climber;
	
	private static AutonomousBase auto;
	private static ControlsBase controls;
	
	//shutup >:(
	public static Compressor compressor;
	
	private DriverStation driverStation;
		
	
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
	public void autonomousInit() {
	
	}
	
	@Override
	public void autonomousPeriodic() {
	
	}
	
	@Override
	public void teleopPeriodic() {
		controls = new DualRemote();
		if(driverStation.isFMSAttached())
			compressor.setClosedLoopControl(false);
        else 
        	compressor.setClosedLoopControl(true);
		
		 while (isOperatorControl() && isEnabled()) {
			 controls.periodic();
			 periodic();
			 
	         Timer.delay(0.005);
		 }
	
	}

	@Override
	public void testPeriodic() {
	
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
	}

	@Override
	public void startCompetition() {
		// TODO Auto-generated method stub
		
	}
}
