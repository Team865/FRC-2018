package ca.warp7.robot.misc;

import ca.warp7.robot.Robot;
import ca.warp7.robot.subsystems.Limelight;

public class LimelightPhotosensor {
	protected Limelight limelight = Robot.limelight;
	
	private boolean found = false;
	private int pipeline;
	
	public LimelightPhotosensor(int pipeline) {
		this.pipeline = pipeline;
	}
	
	public void update() {
		found = limelight.getPipeline() == pipeline && limelight.foundObject();
	}
	
	public boolean isTriggered() {
		return found;
	}
}
