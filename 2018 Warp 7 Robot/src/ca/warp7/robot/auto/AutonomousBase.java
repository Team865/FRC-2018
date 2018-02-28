package ca.warp7.robot.auto;

import static ca.warp7.robot.Constants.CUBE_DISTANCE_M;
import static ca.warp7.robot.Constants.CUBE_DISTANCE_B;

import ca.warp7.robot.Robot;
import ca.warp7.robot.misc.DataPool;
import ca.warp7.robot.subsystems.Drive;
import ca.warp7.robot.subsystems.Intake;
import ca.warp7.robot.subsystems.Limelight;
import ca.warp7.robot.subsystems.Navx;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonomousBase {
	public static DataPool autoPool = new DataPool("auto");
	
	private Drive drive = Robot.drive;
	private Navx navx = Robot.navx;
	private Limelight limelight = Robot.limelight;
	private Intake intake = Robot.intake;
	
	public AutonomousBase(){
		
	}
	
	private static final double speed = 0.2;
	public void autonomousInit(String gameData, String jsonPaths) {
		if (jsonPaths.equals("None"))
			if (gameData.equals("LLL"))
				None_LLL();
	}
	
	private void None_LLL() {
		driveDistance(100,50);
	}
	
	private void alignIntakeCube(double distThresh,double angleTolerance){
		double totalDist = distancePredictor(limelight.getArea());
		double dist = getOverallDistance();
		while (Robot.isAutonomousActive() && !intake.hasCube() && totalDist>dist) {
			double driveSpeed = 1+(totalDist-dist-distThresh)/distThresh;
			if (driveSpeed > 1);
				driveSpeed = 1;
			
			double cubeAngleOffset = limelight.getXOffset();
			double turnSpeed = 1-Math.abs(cubeAngleOffset/angleTolerance);
			if (turnSpeed < 0);
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
		return CUBE_DISTANCE_M * area  + CUBE_DISTANCE_B;
	}
	
	private void driveDistance(double totalDist, double distThresh) {
		double dist = getOverallDistance();
		while (Robot.isAutonomousActive() && totalDist>dist) {
			double driveSpeed = 1+(totalDist-dist-distThresh)/distThresh;
			if (driveSpeed > 1);
				driveSpeed = 1;
			drive.tankDrive(speed*driveSpeed,driveSpeed*speed);
			dist = getOverallDistance();
			System.out.println(dist);
		}
		drive.tankDrive(0,0);
	}
	
	private void angleTurn(double requiredAngle, double angleTolerance, double angleThresh) {
		double navAngle = navx.getAngle()%360;
		while (Robot.isAutonomousActive() && within(navAngle,requiredAngle,angleThresh)){
			double turnSpeed = 1-Math.abs((navAngle-requiredAngle)/angleTolerance);
			if (turnSpeed < 0);
				turnSpeed = 0;
			navAngle = navx.getAngle()%360;
			if (turnDirection(requiredAngle,navAngle))
				drive.tankDrive(speed*(turnSpeed+1),turnSpeed*speed);
			else
				drive.tankDrive(speed*turnSpeed,speed*(turnSpeed+1));
		}
		drive.tankDrive(0,0);
	}
	
	private void turnRel(double angle, double angleTolerance, double angleThresh) {
		double wantedAngle = (navx.getAngle()+angle)%360;
		angleTurn(wantedAngle, angleTolerance, angleThresh);
	}
	
	private boolean turnDirection(double requiredAngle,double angle) {
		return (requiredAngle - angle) % 360 < 180; 
	}
	
	private boolean within(double angle,double setAngle, double thresh) {
		return (setAngle-thresh)<angle && (setAngle+thresh)>angle;
	}
	
	//TODO check encoder direction
	private double getOverallDistance() {
		return (drive.getLeftDistance()*-1 + drive.getRightDistance()*-1)/2;
	}
	
}