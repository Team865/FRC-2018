package ca.warp7.robot.subsystems;

import static ca.warp7.robot.Constants.LIFT_MOTOR_RIGHT_IDS;
import static ca.warp7.robot.Constants.LIFT_ENCODER_A;
import static ca.warp7.robot.Constants.LIFT_ENCODER_B;
import static ca.warp7.robot.Constants.LIFT_MOTOR_LEFT_IDS;
import static ca.warp7.robot.Constants.LIFT_HEIGHT;
import static ca.warp7.robot.Constants.HALL_DIO;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import ca.warp7.robot.Robot;
import ca.warp7.robot.misc.MotorGroup;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;

public class Lift {
	private MotorGroup LiftMotorRight;
	private MotorGroup LiftMotorLeft;
	private Encoder liftEncoder;
	private double setLocation;
	private DigitalInput liftHallaffect;
	
	private Intake intake = Robot.intake;
	
	public Lift(){
		LiftMotorLeft = new MotorGroup(LIFT_MOTOR_LEFT_IDS, WPI_VictorSPX.class);
		LiftMotorRight = new MotorGroup(LIFT_MOTOR_RIGHT_IDS, WPI_VictorSPX.class);
		//LiftMotorLeft.setInverted(true);
		
		liftEncoder =  new Encoder(LIFT_ENCODER_A, LIFT_ENCODER_B, false, EncodingType.k4X);
		liftEncoder.setDistancePerPulse(1);
		liftHallaffect = new DigitalInput(HALL_DIO);
		zeroEncoder();
	}
	
	private double ramp = 0;
	private final double rampSpeed = 6;
	public void setSpeed(double speed){
		LiftMotorLeft.set(speed);
		LiftMotorRight.set(speed);
	}
	
	public void rampSpeed(double speed){
		ramp += (speed - ramp)/rampSpeed;
		
		if (false && speed > 0)//is max limit hit
			ramp = 0;
		LiftMotorLeft.set(ramp);
		LiftMotorRight.set(ramp);
	}
	
	public void setLoc(double loc){
		setLocation = Math.abs(loc);
	}
	
	private static final double SPEED_OFFSET = 0;
	private static final double SPEED_OFFSET_CUBE = 0;
	private static final double tolerance = 0.2;
	private double scaledLift = 0;
	public void periodic(){
		if (isBottom()){ //zero switch is active zero encoder
			scaledLift = 0;
			zeroEncoder();
		}else
			scaledLift = getEncoderVal()/LIFT_HEIGHT;
		
		System.out.println("Encoder Lift Val: "+ getEncoderVal());
		System.out.println("scaledL: "+scaledLift);
		System.out.println("setL: "+setLocation);
		double speed = 1+(setLocation-scaledLift-tolerance)/tolerance;
		if (speed > 1)
			speed = 1;
		else if (speed < -0.5)
			speed = -0.5;
		System.out.println("speed: "+speed);
		//if (intake.hasCube())
			//rampSpeed(speed+SPEED_OFFSET_CUBE);
		//else
			//rampSpeed(speed+SPEED_OFFSET);
	}
	
	public double getEncoderVal() {
		return Math.abs(liftEncoder.getDistance()); 
	}
	
	public void zeroEncoder() {
		liftEncoder.reset();
	}
	
	public boolean isBottom(){
		return liftHallaffect.get();//is lift at bottom
	}
}
