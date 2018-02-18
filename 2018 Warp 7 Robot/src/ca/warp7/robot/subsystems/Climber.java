package ca.warp7.robot.subsystems;

import static ca.warp7.robot.Constants.CLIMBER_MOTORS_IDS;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import ca.warp7.robot.misc.MotorGroup;
import edu.wpi.first.wpilibj.VictorSP;

public class Climber {

	private MotorGroup ClimberMotors;
	
	public Climber(){
		ClimberMotors = new MotorGroup(CLIMBER_MOTORS_IDS, WPI_VictorSPX.class);
	}
	
	private double ramp = 0;
	private final double rampSpeed = 6;
	public void setSpeed(double speed){
		// Ramp to prevent brown outs
		ramp += (speed - ramp)/rampSpeed;
		ClimberMotors.set(ramp);
	}
	
}
