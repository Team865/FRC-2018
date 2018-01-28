package ca.warp7.robot.subsystems;

import static ca.warp7.robot.Constants.CLIMBER_MOTOR_PINS;

import ca.warp7.robot.misc.MotorGroup;
import edu.wpi.first.wpilibj.VictorSP;

public class Climber {

	private MotorGroup climbMotors;
	private final double rampSpeed = 6;
	
	public Climber(){
		climbMotors = new MotorGroup(CLIMBER_MOTOR_PINS, VictorSP.class);
	}
	
	private double ramp = 0.0;
	public void setSpeed(double speed){
		// Ramp to prevent brown outs
		ramp += (speed - ramp)/rampSpeed;
		climbMotors.set(ramp);
	}
	
}
