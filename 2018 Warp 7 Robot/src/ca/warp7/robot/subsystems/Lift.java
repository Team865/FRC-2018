package ca.warp7.robot.subsystems;

import static ca.warp7.robot.Constants.LIFT_MOTOR_RIGHT_IDS;
import static ca.warp7.robot.Constants.DRIVE_INCHES_PER_TICK;
import static ca.warp7.robot.Constants.LIFT_DRIVE_ENCODER_A;
import static ca.warp7.robot.Constants.LIFT_DRIVE_ENCODER_B;
import static ca.warp7.robot.Constants.LIFT_MOTOR_LEFT_IDS;
import static ca.warp7.robot.Constants.LIFT_HEIGHT;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import ca.warp7.robot.misc.MotorGroup;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;

public class Lift {
	private MotorGroup LiftMotorRight;
	private MotorGroup LiftMotorLeft;
	private Encoder liftEncoder;
	private double setLocation;
	
	public Lift(){
		LiftMotorLeft = new MotorGroup(LIFT_MOTOR_LEFT_IDS, WPI_VictorSPX.class);
		LiftMotorRight = new MotorGroup(LIFT_MOTOR_RIGHT_IDS, WPI_VictorSPX.class);
		LiftMotorRight.setInverted(true);
		
		liftEncoder =  new Encoder(LIFT_DRIVE_ENCODER_A, LIFT_DRIVE_ENCODER_B, false, EncodingType.k4X);
		liftEncoder.setDistancePerPulse(DRIVE_INCHES_PER_TICK);
	}
	
	private double ramp = 0;
	private final double rampSpeed = 6;
	public void setSpeed(double speed){
		ramp += (speed - ramp)/rampSpeed;
		LiftMotorLeft.set(ramp);
		LiftMotorRight.set(ramp);
	}
	
	public void setLoc(double loc){
		setLocation = loc;
	}
	
	private static final double tolerance = 0.10;
	private double scaledLift = 0;
	public void periodic(){
		if (false) //zero switch is active zero encoder
			scaledLift = 0;
		else
			scaledLift = LIFT_HEIGHT/liftEncoder.getDistance();
		
		double speed = 1+(setLocation-scaledLift-tolerance)/tolerance;
		//if (scaledLift >= setLocation-tolerance && scaledLift <= setLocation+tolerance)
		setSpeed(speed);
	}
	
	public double getEncoderVal() {
		return liftEncoder.getDistance(); 
	}
	
	public void zeroEncoder() {
		liftEncoder.reset();
	}
}
