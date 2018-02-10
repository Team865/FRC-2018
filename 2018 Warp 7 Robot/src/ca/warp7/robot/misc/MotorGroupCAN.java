package ca.warp7.robot.misc;

import java.lang.reflect.InvocationTargetException;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.SpeedController;


public class MotorGroupCAN {
	
	private VictorSPX[] motors;
	private boolean isInverted;

	public MotorGroupCAN(int[] pins) {
		//assert type.isAssignableFrom(SpeedController.class); // if this fails u borked
		motors = new VictorSPX[pins.length];
		for (int i = 0; i < pins.length; i++) {
				motors[i] = new VictorSPX(i);
		}
	}

	public void set(double speed) {
		for (VictorSPX motor : motors) {
			motor.set(ControlMode.PercentOutput, speed);
		}
	}
	
	public double get() {
		return motors[0].getMotorOutputPercent();
	}

	public void setInverted(boolean isInverted) {
		this.isInverted = isInverted;
		for (VictorSPX motor : motors) {
			motor.setInverted(isInverted);
		}
	}
	
	// THIS IS DANGEROUS.
	/*
	 * public void setInverted(int index, boolean isInverted) {
	 * motors[index].setInverted(isInverted); }
	 */

	public boolean getInverted() {
		return isInverted;
	}

	public void disable() {
		for (SpeedController motor : motors) {
			motor.set(ControlMode.Disabled, 0);
		}
	}

	public void stopMotor() {
		//for (SpeedController motor : motors) {
			//motor.stopMotor();
		//}
	}
}