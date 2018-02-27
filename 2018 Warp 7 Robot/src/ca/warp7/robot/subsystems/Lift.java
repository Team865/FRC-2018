package ca.warp7.robot.subsystems;

import static ca.warp7.robot.Constants.LIFT_MOTOR_RIGHT_IDS;
import static ca.warp7.robot.Constants.DRIVE_INCHES_PER_TICK;
import static ca.warp7.robot.Constants.LIFT_ENCODER_A;
import static ca.warp7.robot.Constants.LIFT_ENCODER_B;
import static ca.warp7.robot.Constants.LIFT_MOTOR_LEFT_IDS;
import static ca.warp7.robot.Constants.LIFT_HEIGHT;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import ca.warp7.robot.Robot;
import ca.warp7.robot.misc.MotorGroup;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;

public class Lift {
	private MotorGroup LiftMotorRight;
	private MotorGroup LiftMotorLeft;
	private Encoder liftEncoder;
	private double setLocation;
	
	protected Intake intake = Robot.intake;
	
	public Lift(){
		LiftMotorLeft = new MotorGroup(LIFT_MOTOR_LEFT_IDS, WPI_VictorSPX.class);
		LiftMotorRight = new MotorGroup(LIFT_MOTOR_RIGHT_IDS, WPI_VictorSPX.class);
		LiftMotorRight.setInverted(true);
		
		liftEncoder =  new Encoder(LIFT_ENCODER_A, LIFT_ENCODER_B, false, EncodingType.k4X);
		liftEncoder.setDistancePerPulse(1);
		zeroEncoder();
	}
	
	private double ramp = 0;
	private final double rampSpeed = 6;
	public void setSpeed(double speed){
		ramp += (speed - ramp)/rampSpeed;
		LiftMotorLeft.set(ramp);
		LiftMotorRight.set(ramp);
	}
	
	public void setLoc(double loc){
		setLocation = Math.abs(loc);
	}
	
	private static final double tolerance = 0.5;
	private double scaledLift = 0;
	public void periodic(){
		if (false) //zero switch is active zero encoder
			scaledLift = 0;
		else
			scaledLift = liftEncoder.getDistance()/LIFT_HEIGHT;
		System.out.println("scaledL: "+scaledLift);
		System.out.println("setL: "+setLocation);
		double speed = 1+(setLocation-scaledLift-tolerance)/tolerance;
		//if (scaledLift >= setLocation-tolerance && scaledLift <= setLocation+tolerance)
		System.out.println("speed: "+speed);
		//setSpeed(speed);
	}
	
	public double getEncoderVal() {
		return Math.abs(liftEncoder.getDistance()); 
	}
	
	public void zeroEncoder() {
		liftEncoder.reset();
	}
}
