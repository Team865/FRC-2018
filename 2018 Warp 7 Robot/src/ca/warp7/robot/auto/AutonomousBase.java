package ca.warp7.robot.auto;

import static ca.warp7.robot.Constants.CUBE_DISTANCE_M;
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
	
	public AutonomousBase(){
		
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
		driveDistance(299.65*2.54-50-75,5,50,5);
		lift.setLoc(0.8);
		turnRelRight(50,45/2-5,10);
		Timer.delay(2);
		intake.setSpeed(-1);
		Timer.delay(0.4);
		intake.setSpeed(0);
		turnRelRight(100,45/2,5);
		lift.setLoc(0.1);
		Timer.delay(1.5);
		lift.setLoc(0);
		
		drive.resetDistance();
		driveDistance(100,5,40,2);
		intake.setSpeed(0.8);
		drive.resetDistance();
		alignIntakeCube(5,3,5);
		Timer.delay(0.4);
		intake.setSpeed(0);
	}

	private void Left_RRR() {
		driveDistance(520-49-10,360,100,10);
	}
	
	private void Left_LLL() {
		navx.resetAngle();
		drive.resetDistance();
		driveDistance(299.65*2.54-50-75,5,50,5);
		lift.setLoc(0.8);
		turnRelRight(50,45/2-5,10);
		Timer.delay(2);
		intake.setSpeed(-1);
		Timer.delay(0.4);
		intake.setSpeed(0);
		turnRelRight(100,45/2,5);
		lift.setLoc(0.1);
		Timer.delay(1.5);
		lift.setLoc(0);
		
		drive.resetDistance();
		driveDistance(100,5,40,2);
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
		driveDistance(260,5,50,10);
		turnRelRight(50,10,8);
		lift.setLoc(0.4);
		Timer.delay(1);
		drive.resetDistance();
		driveDistance(40,5,10,15);
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
		driveDistance(210/2,5,40,2);
		turnRelLeft(-25,10,5);
		lift.setLoc(0.4);
		drive.resetDistance();
		driveDistance(100,5,40,2);
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
		driveDistance(210/2,5,40,2);
		turnRelLeft(-25,10,5);
		lift.setLoc(0.4);
		drive.resetDistance();
		driveDistance(100,5,40,2);
		Timer.delay(0.5);
		intake.setSpeed(1);
		Timer.delay(0.4);
		intake.setSpeed(0);
	}
	
	//Middle switch left
	private void Middle_LLL() {
		navx.resetAngle();
		drive.resetDistance();
		turnRelLeft(-25,5,5);
		lift.setLoc(0.4);
		driveDistance(220,5,40,2);
		Timer.delay(0.5);
		intake.setSpeed(-1);
		Timer.delay(0.4);
		intake.setSpeed(0);
	}
	
	//Middle switch right
	private void Middle_RRR() {
		navx.resetAngle();
		drive.resetDistance();
		turnRelRight(25,5,5);
		lift.setLoc(0.4);
		driveDistance(220,5,40,2);
		Timer.delay(0.5);
		intake.setSpeed(-1);
		Timer.delay(0.4);
		intake.setSpeed(0);
	
	}
	
	//Middle switch right
	private void Middle_RLR() {
		navx.resetAngle();
		drive.resetDistance();
		turnRelRight(25,15,5);
		lift.setLoc(0.4);
		driveDistance(240,5,40,2);
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
		turnRelLeft(-25,10,5);
		lift.setLoc(0.4);
		driveDistance(240,5,40,2);
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
		double totalDist = distancePredictor(limelight.getArea());
		double dist = getOverallDistance();
		while (!intake.hasCube() && !within(dist,totalDist,distanceTolerance)) {
			double driveSpeed = 1+(totalDist-dist-distThresh)/distThresh;
			if (driveSpeed > 1)
				driveSpeed = 1;
			
			double cubeAngleOffset = limelight.getXOffset();
			double turnSpeed = 1-Math.abs(cubeAngleOffset/angleThresh);
			if (turnSpeed < 0)
				turnSpeed = 0;
				
			if (cubeAngleOffset >= 0)//turn right
				drive.tankDrive(speed*driveSpeed,speed*driveSpeed*turnSpeed);
			else //turn left
				drive.tankDrive(speed*driveSpeed*turnSpeed,speed*driveSpeed);
			
			dist = getOverallDistance();
			
			//dist = getOverallDistance()/2;
			//double b = limelight.getArea();
			//if (b != 0)
			//	totalDist = distancePredictor(b)/2;
		}
	}
	
	private double distancePredictor(double area){
		return CUBE_DISTANCE_B - CUBE_DISTANCE_M * area;
	}
	
	private void driveDistance(double totalDist, double angleThresh, double distThresh, double distanceTolerance) {
		double dist = getOverallDistance();
		double requiredAngle = navx.getAngle();
		while (!within(dist,totalDist,distanceTolerance)) {
			double navAngle = navx.getAngle();
			double turnSpeed = (1+(requiredAngle-navAngle-angleThresh)/angleThresh);
			if (turnSpeed > 1)
				turnSpeed = 1;
			
			if (turnSpeed < -1)
				turnSpeed = -1;
			
			double driveSpeed = 1+(totalDist-dist-distThresh)/distThresh;
			if (driveSpeed > 1)
				driveSpeed = 1;
			
			if (driveSpeed < -1)
				driveSpeed = -1;
			
			if (!within(navAngle,requiredAngle,2))
				if (turnSpeed >= 0 )//turn right
					drive.tankDrive(speed*driveSpeed,speed*driveSpeed*turnSpeed);
				else //turn left
					drive.tankDrive(speed*driveSpeed*turnSpeed,speed*driveSpeed);
			else
				drive.tankDrive(speed*driveSpeed,speed*driveSpeed);
			
			dist = getOverallDistance();
			//System.out.println(turnSpeed);
		}
		drive.tankDrive(0,0);
	}
	
	private void angleTurn(double requiredAngle, double angleThresh, double angleTolerance) {
		double navAngle = navx.getAngle()%360;
		while (!within(navAngle,requiredAngle,angleTolerance)){
			double turnSpeed = 1+(requiredAngle-navAngle-angleThresh)/angleThresh;
			if (turnSpeed > 1)
				turnSpeed = 1;
			navAngle = navx.getAngle()%360;
			//System.out.println(turnSpeed);
			//System.out.println(turnSpeed*speed);
			if (turnDirection(requiredAngle,navAngle))
				drive.tankDrive(speed*turnSpeed,turnSpeed*speed*-1);
			else
				drive.tankDrive(turnSpeed*speed*-1,turnSpeed*speed);
				
		}
		drive.tankDrive(0,0);
	}
	
	private void turnRelRight(double angle, double angleThresh, double angleTolerance) {
		double wantedAngle = navx.getAngle()+angle;
		double navAngle = navx.getAngle();
		while (!within(navAngle,wantedAngle,angleTolerance)){
			double turnSpeed = 1+(wantedAngle-navAngle-angleThresh)/angleThresh;
			if (turnSpeed > 1)
				turnSpeed = 1;
			navAngle = navx.getAngle();
			//System.out.println(turnSpeed);
			//System.out.println(turnSpeed*speed);
			if (wantedAngle >= 0)
				drive.tankDrive(speed*turnSpeed,turnSpeed*speed*-1);
			else
				drive.tankDrive(turnSpeed*speed*-1,turnSpeed*speed);
				
		}
		drive.tankDrive(0,0);
	}
	
	private void turnRelLeft(double angle, double angleThresh, double angleTolerance) {
		double wantedAngle = navx.getAngle()+angle;
		double navAngle = navx.getAngle();
		while (!within(navAngle,wantedAngle,angleTolerance)){
			double turnSpeed = 1+(wantedAngle-navAngle-angleThresh)/angleThresh;
			if (turnSpeed > 1)
				turnSpeed = 1;
			navAngle = navx.getAngle();
			//System.out.println(turnSpeed);
			//System.out.println(turnSpeed*speed);
			if (wantedAngle >= 0)
				drive.tankDrive(turnSpeed*speed*-1,turnSpeed*speed);
			else
				drive.tankDrive(speed*turnSpeed,turnSpeed*speed*-1);
		}	
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