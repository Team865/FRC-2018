package ca.warp7.robot.auto;

import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ca.warp7.robot.Robot;
import ca.warp7.robot.misc.DataPool;
import ca.warp7.robot.misc.RTS;
import ca.warp7.robot.subsystems.Drive;
import ca.warp7.robot.subsystems.Navx;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonomousBase {
	public static DataPool autoPool = new DataPool("auto");
	
	protected Drive drive = Robot.drive;
	protected Navx navx = Robot.navx;
	
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
	
	private void driveDistance(double totalDist, double distThresh) {
		System.out.println("ok");
		double dist = getOverallDistance();
		while (totalDist>dist) {
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
		while (within(navAngle,requiredAngle,angleThresh)){
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
		double navAngle = navx.getAngle()%360;
		angleTurn(navAngle + angle, angleTolerance, angleThresh);
	}
	
	private boolean turnDirection(double requiredAngle,double navAngle) {
		return (requiredAngle - navAngle) % 360 < 180; 
	}
	
	private boolean within(double angle,double setAngle, double thresh) {
		return (setAngle-thresh)<angle && (setAngle+thresh)>angle;
	}
	
	private double getOverallDistance() {
		return (drive.getLeftDistance()*-1+drive.getRightDistance()*-1)/2;
	}
	
}