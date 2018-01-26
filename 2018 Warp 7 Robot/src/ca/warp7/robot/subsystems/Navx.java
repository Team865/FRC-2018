package ca.warp7.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;

import ca.warp7.robot.misc.RTS;
import edu.wpi.first.wpilibj.SPI;

public class Navx {
	private AHRS ahrs;
	private RTS updater;
	public Navx(){
		ahrs = new AHRS(SPI.Port.kMXP);
		
		if(!ahrs.isConnected()) {
			System.out.println("Navx is not Connected");
		}
		else if (ahrs.isCalibrating()) {
			System.out.println("Calibrating Navx");
		}
	}
	
	public float getAccelX() {
		return ahrs.getRawAccelX();
	}
	
	public float getAccelY() {
		return ahrs.getRawAccelY();
	}
	
	public float getDistX() {		
		return ahrs.getDisplacementX();
	}
	
	public float getDistY(){
		return ahrs.getDisplacementY();
	}
	
	private boolean isMoving(){
		return ahrs.isMoving();
	}
	
	public void startUpdateDisplacement(int refesh) {
		updater = new RTS(refesh,5);
		Runnable methodCall = () -> ahrs.updateDisplacement(getAccelX(), getAccelY(), refesh, isMoving());
		updater.addTask(methodCall);
		updater.start();
	}
	
	public void stopUpdateDisplacement(){
		updater.stop();
	}
}
