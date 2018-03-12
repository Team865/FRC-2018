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
	private MiniPID turnSoftPID;
	private MiniPID distancePID;
	
	private AutoFunctions autoFunc = new AutoFunctions();
	
	private int step = 0;
	private double robotAngle = 0;
	
	private static final double speed = 1;
	public void autonomousPeriodic(String gameData, int pin) {		
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
	}
	
	private void Left_RLR() {
		
	}

	private void Left_RRR() {
		
	}
	
	private void Left_LLL() {
		
	}
	
	private void Left_LRL() {
		
	}
	
	private void Right_RLR() {
		
	}

	private void Right_LRL() {
		// TODO Auto-generated method stub
		
	}

	private void Right_LLL() {
		// TODO Auto-generated method stub
		
	}

	private void Right_RRR() {
		
	}
	
	//Middle switch left
	private void Middle_LLL() {
		
	}
	
	//Middle switch right
	private void Middle_RRR() {
			
	}
	
	//Middle switch right
	private void Middle_RLR() {
				
	}

	//Middle switch left
	private void Middle_LRL() {
		
	}

	private void None_RLR() {
		// TODO Auto-generated method stub
		
	}

	private void None_LRL() {
		// TODO Auto-generated method stub
		
	}

	private void None_LLL() {
		// TODO Auto-generated method stub
		switch (step) {
			case (0): {
				autoFunc.setDistanceTarget(1000);
				updateRobotAngle();
				step++;
				break;
			}
			case (1): {
				if(autoFunc.driveDistance(1000, robotAngle)) {
					step++;
				}
				break;
			}
		}
	}

	private void None_RRR() {
		// TODO Auto-generated method stub
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
	
	private double distancePredictor(double area){
		return CUBE_DISTANCE_B - CUBE_DISTANCE_M * area;
	}
	
	private void updateRobotAngle(){
		robotAngle = navx.getAngle()%360;
	}
}