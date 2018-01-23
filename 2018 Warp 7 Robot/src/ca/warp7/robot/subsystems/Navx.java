package ca.warp7.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;

public class Navx {
	private AHRS ahrs;
	
	//variables to calculate distace and velocity
	private double time = Timer.getMatchTime();
	private double velX = 0.0;
	private double velY = 0.0;
	private double distX = 0.0;
	private double distY = 0.0;
	
	public Navx(){
		ahrs = new AHRS(SPI.Port.kMXP);
		
		if(!ahrs.isConnected()) {
			System.out.println("Navx is not Connected");
		}
		else if (ahrs.isCalibrating()) {
			System.out.println("Calibrating Navx");
		}
	}
	
	public double getAccelX() {
		return ahrs.getRawAccelX();
	}
	
	public double getAccelY() {
		return ahrs.getRawAccelY();
	}
	
	public double getDistX() {
		double temp = Timer.getMatchTime() - time;
		time = temp + time;
		
		
		
		return 1.0;
	}
}
