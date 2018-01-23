package ca.warp7.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;


public class Navx {
	private AHRS ahrs;
	
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
	
}
