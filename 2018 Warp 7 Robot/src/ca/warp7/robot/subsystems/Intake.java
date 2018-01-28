package ca.warp7.robot.subsystems;

import static ca.warp7.robot.Constants.INTAKE_MOTOR_PINS;
import static ca.warp7.robot.Constants.INTAKE_PISTONS;

import ca.warp7.robot.misc.MotorGroup;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.Solenoid;

public class Intake {
	private MotorGroup intakeMotors;
	private Solenoid intakePistons;
	private final double rampSpeed = 6;
	
	public Intake(){
		intakeMotors = new MotorGroup(INTAKE_MOTOR_PINS, VictorSP.class);
		intakePistons = new Solenoid(INTAKE_PISTONS);
	}
	
	private double ramp = 0.0;
	public void setSpeed(double speed){
		// Ramp to prevent brown outs
		ramp += (speed - ramp)/rampSpeed;
		intakeMotors.set(ramp);
	}
	
	public void pistonSet(boolean b){
		intakePistons.set(b);
	}
	
	public void pistonToggle(){
		intakePistons.set(!intakePistons.get());
	}
	
}
