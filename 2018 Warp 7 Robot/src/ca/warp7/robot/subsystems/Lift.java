package ca.warp7.robot.subsystems;

import static ca.warp7.robot.Constants.LIFT_MOTOR_RIGHT_IDS;
import static ca.warp7.robot.Constants.LIFT_ENCODER_A;
import static ca.warp7.robot.Constants.LIFT_ENCODER_B;
import static ca.warp7.robot.Constants.LIFT_MOTOR_LEFT_IDS;
import static ca.warp7.robot.Constants.LIFT_HEIGHT;
import static ca.warp7.robot.Constants.HALL_DIO;
import static ca.warp7.robot.Constants.SPEED_OFFSET;
import static ca.warp7.robot.Constants.SPEED_OFFSET_CUBE;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.stormbots.MiniPID;

import ca.warp7.robot.Robot;
import ca.warp7.robot.misc.MotorGroup;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;

public class Lift {
	private MotorGroup LiftMotorRight;
	private MotorGroup LiftMotorLeft;
	private Encoder liftEncoder;
	private DigitalInput liftHallaffect;
	private MiniPID liftPID;
	private double targetH;
	
	private Intake intake = Robot.intake;
	private Drive drive = Robot.drive;
	public boolean overrideIntake=false;
	public boolean disableSpeedLimit = false;
	
	public Lift(){
		LiftMotorLeft = new MotorGroup(LIFT_MOTOR_LEFT_IDS, WPI_VictorSPX.class);
		LiftMotorRight = new MotorGroup(LIFT_MOTOR_RIGHT_IDS, WPI_VictorSPX.class);
		LiftMotorLeft.setInverted(true);
		LiftMotorRight.setInverted(true);
		
		liftEncoder =  new Encoder(LIFT_ENCODER_A, LIFT_ENCODER_B, false, EncodingType.k4X);
		liftEncoder.setDistancePerPulse(1);
		liftHallaffect = new DigitalInput(HALL_DIO);
		zeroEncoder();
		liftPID = new MiniPID(6.5,0,20);
		liftPID.setOutputLimits(-0.4,1); //kaelan--i changed this on april 24 to let the lift go further down before turning during auto. used to be limited to -0.55
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
	
	public void setLoc(double scale) {
		double target = Math.abs(scale);
		if (target <= 0.1)
			target = 0;
		targetH=target;
		SmartDashboard.putNumber("loc dfliusafusd", target);
		liftPID.setSetpoint(target);
	}
	
	public void periodic(){
		if (isBottom()) //zero switch is active zero encoder
			zeroEncoder();
		else
			if (intake.getSpeed() >= 0 && !(overrideIntake)) //added intake override for auto
				intake.rampSpeed(0.3);
			
		
		double scaledLift = getEncoderVal()/LIFT_HEIGHT;
		double speed = liftPID.getOutput(scaledLift);
		if (scaledLift>0.2)
			System.out.println("speed= "+speed + " height= "+scaledLift +"setP= "+targetH);
		
		if (!disableSpeedLimit) {
			double speedLimit = Math.pow(0.30,scaledLift);
			drive.setSpeedLimit(speedLimit);
		}else
			drive.setSpeedLimit(1);
		
		rampSpeed(speed);
	}
	
	public double getEncoderVal() {
		double dist = -liftEncoder.getDistance();
		
		if (dist>=-300)
			return dist;
		return 0; 
	}
	
	public void zeroEncoder() {
		liftEncoder.reset();
	}
	
	public boolean isBottom(){
		return !(boolean) liftHallaffect.get();//is lift at bottom
	}
}
