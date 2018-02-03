package ca.warp7.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;

import ca.warp7.robot.misc.RTS;
import edu.wpi.first.wpilibj.SPI;

public class Navx {
	private AHRS ahrs;
	private RTS updater;
	
	private float last_velocity[] = new float[2];
    private float displacement[] = new float[2];
	
	public Navx(){
		ahrs = new AHRS(SPI.Port.kMXP);
		
		if(!ahrs.isConnected()) {
			System.out.println("Navx is not Connected");
		}
		else if (ahrs.isCalibrating()) {
			System.out.println("Calibrating Navx");
		}
	}
	
	private void updateDisplacement() {
        if (ahrs.isMoving()) {
            float accel_g[] = new float[2];
            float accel_m_s2[] = new float[2];
            float curr_velocity_m_s[] = new float[2];
            float sample_time = (1.0f / updater.getHz());
            //accel_g[0] = ahrs.getRawAccelX();
            //accel_g[1] = ahrs.getRawAccelY();
            accel_g[0] = ahrs.getWorldLinearAccelX();
            accel_g[1] = ahrs.getWorldLinearAccelY();
            for (int i = 0; i < 2; i++) {
                accel_m_s2[i] = accel_g[i] * 9.80665f;
                curr_velocity_m_s[i] = last_velocity[i] + (accel_m_s2[i] * sample_time);
                displacement[i] += last_velocity[i] + (0.5f * accel_m_s2[i] * sample_time * sample_time);
                last_velocity[i] = curr_velocity_m_s[i];
            }
        } else {
            last_velocity[0] = 0.0f;
            last_velocity[1] = 0.0f;
        }
     }
	
	public void startUpdateDisplacement(int refesh) {
		updater = new RTS("DisplacementUpdater",refesh);
		Runnable methodCall = () -> updateDisplacement();
		updater.addTask(methodCall);
		updater.start();
	}
	
	public void stopUpdateDisplacement(){
		updater.stop();
	}
	
	public float getAccelX() {
		return ahrs.getRawAccelX();
	}
	
	public float getAccelY() {
		return ahrs.getRawAccelY();
	}
	
	public float getDispX() {		
		return displacement[0];
	}
	
	public float getDispY(){
		return displacement[1];
	}
	
	public float getDispZ() {
        return 0;
   }
	
	public float getVelX() {
        return last_velocity[0];
    }

    public float getVelY() {
        return last_velocity[1];
    }
    
    public float getVelZ() {
        return 0;
    }
	
	public boolean isMoving(){
		return ahrs.isMoving();
	}
	
	public RTS getDisplacementUpdater(){
		return updater;
	}
}
