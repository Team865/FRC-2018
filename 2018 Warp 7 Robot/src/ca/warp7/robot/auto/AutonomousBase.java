package ca.warp7.robot.auto;

import static ca.warp7.robot.Constants.CUBE_DISTANCE_M;

import com.stormbots.MiniPID;

import static ca.warp7.robot.Constants.CUBE_DISTANCE_B;

import ca.warp7.robot.Robot;
import ca.warp7.robot.misc.DataPool;
import ca.warp7.robot.misc.RTS;
import ca.warp7.robot.subsystems.Drive;
import ca.warp7.robot.subsystems.Intake;
import ca.warp7.robot.subsystems.Lift;
import ca.warp7.robot.subsystems.Limelight;
import ca.warp7.robot.subsystems.Navx;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonomousBase {
	public static DataPool autoPool = new DataPool("auto");
	
	private Drive drive = Robot.drive;
	private Navx navx = Robot.navx;
	private Limelight limelight = Robot.limelight;
	private Intake intake = Robot.intake;
	private Lift lift = Robot.lift;
	
	private MiniPID turnPID;
	private MiniPID distancePID;
	
	public AutonomousBase(){
		turnPID = new MiniPID(1,0,0);
		turnPID.setOutputLimits(1);
		
		distancePID = new MiniPID(1,0,0);
		distancePID.setOutputLimits(1);
	}
	
	
	private static final double speed = 0.35;
	public void autonomousInit(String gameData, int pin) {
		RTS liftRTS = new RTS("liftRTS", 8);
		Runnable liftPer = () -> lift.periodic();
		liftRTS.addTask(liftPer);
		liftRTS.start();
		//driveDistance(520-49-10,360,100,10);
		
		System.out.println(gameData);
		if (pin == 0) { //None
			System.out.println("pin 0 active :None:");
			if (gameData.equals("RRR"))
				None_RRR();
			else if (gameData.equals("LLL"))
				None_LLL();
			else if (gameData.equals("LRL"))
				None_LRL();
			else if (gameData.equals("RLR"))
				None_RLR();
		}
		else if (pin == 1) { //Left
			System.out.println("pin 1 active :Left:");
			if (gameData.equals("RRR"))
				Left_RRR();
			else if (gameData.equals("LLL"))
				Left_LLL();
			else if (gameData.equals("LRL"))
				Left_LRL();
			else if (gameData.equals("RLR"))
				Left_RLR();
		}
		else if (pin == 2) { //Middle
			System.out.println("pin 2 active :Middle:");
			if (gameData.equals("RRR"))
				Middle_RRR();
			else if (gameData.equals("LLL"))
				Middle_LLL();
			else if (gameData.equals("LRL"))
				Middle_LRL();
			else if (gameData.equals("RLR"))
				Middle_RLR();
		}
		else if (pin == 3) { //Right
			System.out.println("pin 3 active :Right:");
			if (gameData.equals("RRR"))
				Right_RRR();
			else if (gameData.equals("LLL"))
				Right_LLL();
			else if (gameData.equals("LRL"))
				Right_LRL();
			else if (gameData.equals("RLR"))
				Right_RLR();
		}
		
		liftRTS.stop();
		lift.setSpeed(0);
		
	}
	
	private void Left_RLR() {
		navx.resetAngle();
		drive.resetDistance();
		driveDistance(299.65*2.54-50-75,5);
		lift.setLoc(0.8);
		angleRelTurn(50,10);
		Timer.delay(2);
		intake.setSpeed(-1);
		Timer.delay(0.4);
		intake.setSpeed(0);
		angleRelTurn(100,5);
		lift.setLoc(0.1);
		Timer.delay(1.5);
		lift.setLoc(0);
		
		drive.resetDistance();
		driveDistance(100,2);
		intake.setSpeed(0.8);
		drive.resetDistance();
		alignIntakeCube(5,3,5);
		Timer.delay(0.4);
		intake.setSpeed(0);
	}

	private void Left_RRR() {
		driveDistance(520-49-10,10);
	}
	
	private void Left_LLL() {
		navx.resetAngle();
		drive.resetDistance();
		driveDistance(299.65*2.54-50-75,5);
		lift.setLoc(0.8);
		angleRelTurn(50,10);
		Timer.delay(2);
		intake.setSpeed(-1);
		Timer.delay(0.4);
		intake.setSpeed(0);
		angleRelTurn(100,5);
		lift.setLoc(0.1);
		Timer.delay(1.5);
		lift.setLoc(0);
		
		drive.resetDistance();
		driveDistance(100,2);
		intake.setSpeed(0.8);
		drive.resetDistance();
		alignIntakeCube(5,3,5);
		Timer.delay(0.4);
		intake.setSpeed(0);
		lift.setLoc(0.4);
		Timer.delay(2);
		intake.setSpeed(-0.8);
		Timer.delay(0.4);
		intake.setSpeed(0);
		
	}
	
	/*
	driveDistance(120*2.54+420-110+40-40,50,10);
	turnRelRight(45,45/2,15);
	lift.setLoc(0.9);
	drive.resetDistance();
	driveDistance(10,10,2);
	Timer.delay(7.5);
	intake.setSpeed(1);
	Timer.delay(0.4);
	intake.setSpeed(0);
	turnRelRight(90+45,45/2,10);
	lift.setLoc(0.1);
	drive.resetDistance();
	driveDistance(100,50,10);
	*/
	
	private void Left_LRL() {
		navx.resetAngle();
		drive.resetDistance();
		driveDistance(260,10);
		angleRelTurn(50,8);
		lift.setLoc(0.4);
		Timer.delay(1);
		drive.resetDistance();
		driveDistance(40,15);
		Timer.delay(0.5);
		intake.setSpeed(-0.5);
		Timer.delay(0.4);
		intake.setSpeed(0);
	}

	/*
	turnRelRight(90,75,5);
	driveDistance(520-49-10,360,100,10);
	turnRelRight(90,70,5);
	Timer.delay(0.5);
	drive.resetDistance();
	driveDistance(450,360,50,10);
	angleTurn(0,675,5);
	lift.setLoc(0.9);
	drive.resetDistance();
	driveDistance(25,360,10,2);
	Timer.delay(3.5);
	intake.setSpeed(1);
	Timer.delay(0.4);
	intake.setSpeed(0);
	*/
	
	private void Right_RLR() {
		navx.resetAngle();
		drive.resetDistance();
		driveDistance(210/2,2);
		angleRelTurn(-25,5);
		lift.setLoc(0.4);
		drive.resetDistance();
		driveDistance(100,2);
		Timer.delay(0.5);
		intake.setSpeed(1);
		Timer.delay(0.4);
		intake.setSpeed(0);
	}

	private void Right_LRL() {
		// TODO Auto-generated method stub
		
	}

	private void Right_LLL() {
		// TODO Auto-generated method stub
		
	}

	private void Right_RRR() {
		navx.resetAngle();
		drive.resetDistance();
		driveDistance(210/2,2);
		angleRelTurn(-25,5);
		lift.setLoc(0.4);
		drive.resetDistance();
		driveDistance(100,2);
		Timer.delay(0.5);
		intake.setSpeed(1);
		Timer.delay(0.4);
		intake.setSpeed(0);
	}
	
	//Middle switch left
	private void Middle_LLL() {
		navx.resetAngle();
		drive.resetDistance();
		angleRelTurn(-25,5);
		lift.setLoc(0.4);
		driveDistance(220,2);
		Timer.delay(0.5);
		intake.setSpeed(-1);
		Timer.delay(0.4);
		intake.setSpeed(0);
	}
	
	//Middle switch right
	private void Middle_RRR() {
		navx.resetAngle();
		drive.resetDistance();
		angleRelTurn(25,5);
		lift.setLoc(0.4);
		driveDistance(220,2);
		Timer.delay(0.5);
		intake.setSpeed(-1);
		Timer.delay(0.4);
		intake.setSpeed(0);
	
	}
	
	//Middle switch right
	private void Middle_RLR() {
		navx.resetAngle();
		drive.resetDistance();
		angleRelTurn(25,5);
		lift.setLoc(0.4);
		driveDistance(240,2);
		Timer.delay(0.5);
		intake.setSpeed(1);
		Timer.delay(0.4);
		intake.setSpeed(0);
		Timer.delay(3);
		
	}

	//Middle switch left
	private void Middle_LRL() {
		navx.resetAngle();
		drive.resetDistance();
		angleRelTurn(-25,5);
		lift.setLoc(0.4);
		driveDistance(240,2);
		Timer.delay(0.5);
		intake.setSpeed(1);
		Timer.delay(0.4);
		intake.setSpeed(0);
		Timer.delay(3);
		//drive.resetDistance();
		//driveDistance(-50,5,10,2);
		//lift.setLoc(0);
		
	}

	private void None_RLR() {
		// TODO Auto-generated method stub
		
	}

	private void None_LRL() {
		// TODO Auto-generated method stub
		
	}

	private void None_LLL() {
		// TODO Auto-generated method stub
		
	}

	private void None_RRR() {
		// TODO Auto-generated method stub
		
	}

	private void testVis() {
		navx.resetAngle();
		drive.resetDistance();
		intake.setSpeed(-1);
		alignIntakeCube(100,4,20);
		Timer.delay(1);
		intake.setSpeed(0);
	}
	
	private void alignIntakeCube(double distThresh, double angleThresh, double distanceTolerance){
		double setDistance = distancePredictor(limelight.getArea());
		double dist = getOverallDistance();
		distancePID.setSetpoint(setDistance);
		while (!intake.hasCube() && !within(dist,setDistance,distanceTolerance)) {
			turnPID.setSetpoint((navx.getAngle()%360)+limelight.getXOffset());
			double turnSpeed = turnPID.getOutput();
			double driveSpeed = distancePID.getOutput(dist);
			
			if (turnSpeed >= 0 )//turn right
				drive.tankDrive(speed*driveSpeed,speed*driveSpeed-turnSpeed);
			else //turn left
				drive.tankDrive(speed*driveSpeed-turnSpeed,speed*driveSpeed);
			
			dist = getOverallDistance();
		}
		drive.tankDrive(0,0);
	}
	
	private double distancePredictor(double area){
		return CUBE_DISTANCE_B - CUBE_DISTANCE_M * area;
	}
	
	private void driveDistance(double setDistance, double distanceTolerance) {
		double dist = getOverallDistance();
		double curAngle = navx.getAngle()%360;
		turnPID.setSetpoint(curAngle);
		distancePID.setSetpoint(setDistance);
		while (!within(dist,setDistance,distanceTolerance)) {
			double turnSpeed = turnPID.getOutput(navx.getAngle()%360);
			double driveSpeed = distancePID.getOutput(dist);
			
			if (turnSpeed >= 0 )//turn right
				drive.tankDrive(speed*driveSpeed,speed*driveSpeed-turnSpeed);
			else //turn left
				drive.tankDrive(speed*driveSpeed-turnSpeed,speed*driveSpeed);
			
			dist = getOverallDistance();
		}
		drive.tankDrive(0,0);
	}
	
	private void angleRelTurn(double angle, double angleTolerance) {
		double curAngle = navx.getAngle();
		turnPID.setSetpoint(angle);
		while (!within(curAngle,angle,angleTolerance)){
			double turnSpeed = turnPID.getOutput(curAngle);
			if (angle >= 0)
				drive.tankDrive(speed*turnSpeed,turnSpeed*speed*-1);
			else
				drive.tankDrive(turnSpeed*speed*-1,turnSpeed*speed);
			
			curAngle = navx.getAngle()%360;
		}
		drive.tankDrive(0,0);
	}
	
	private boolean turnDirection(double requiredAngle,double angle) {
		return (requiredAngle - angle) % 360 < 180; 
	}
	
	private boolean within(double angle,double setAngle, double thresh) {
		return (setAngle-thresh)<angle && (setAngle+thresh)>angle;
	}
	
	//TODO check encoder direction
	private double getOverallDistance() {
		return (drive.getLeftDistance() + drive.getRightDistance())/2;
	}
	
}