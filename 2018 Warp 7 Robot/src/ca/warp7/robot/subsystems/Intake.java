package ca.warp7.robot.subsystems;

import static ca.warp7.robot.Constants.INTAKE_MOTOR_RIGHT_IDS;
import static ca.warp7.robot.Constants.INTAKE_MOTOR_LEFT_IDS;
import static ca.warp7.robot.Constants.INTAKE_PISTONS;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import ca.warp7.robot.misc.MotorGroup;
import ca.warp7.robot.misc.LimelightPhotosensor;
import edu.wpi.first.wpilibj.Solenoid;

public class Intake {
	private MotorGroup intakeMotorRight;
	private MotorGroup intakeMotorLeft;
	private Solenoid intakePistons;
	private LimelightPhotosensor photosensor;
	
	public Intake(){
		intakeMotorLeft = new MotorGroup(INTAKE_MOTOR_LEFT_IDS, WPI_VictorSPX.class);
		intakeMotorRight = new MotorGroup(INTAKE_MOTOR_RIGHT_IDS, WPI_VictorSPX.class);
		intakeMotorRight.setInverted(true);
		intakePistons = new Solenoid(INTAKE_PISTONS);
		
		photosensor = new LimelightPhotosensor(1);
	}
	
	private double ramp = 0;
	private final double rampSpeed = 6;
	public void setSpeed(double speed){
		// Ramp to prevent brown outs
		ramp += (speed - ramp)/rampSpeed;
		System.out.println(ramp);
		intakeMotorLeft.set(ramp);
		intakeMotorRight.set(ramp);
	}
	
	public void pistonSet(boolean b){
		intakePistons.set(b);
	}
	
	public void pistonToggle(){
		intakePistons.set(!intakePistons.get());
	}
	
	public boolean hasCube() {
		return photosensor.isTriggered();
	}
	
	public void periodic() {
		photosensor.update();
	}
}
