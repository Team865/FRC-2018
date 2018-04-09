package ca.warp7.robot.auto;

import java.io.File;

import ca.warp7.robot.Robot;
import ca.warp7.robot.subsystems.Drive;
import ca.warp7.robot.subsystems.Intake;
import ca.warp7.robot.subsystems.Navx;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class TrajectoryAuto {
	private int ticks = 0;
	private File left, right;
	public Notifier notifier;
	private Trajectory leftTrajectory, rightTrajectory;
	private Drive drive = Robot.drive;
	private Navx navx = Robot.navx;
	public boolean notifierRunning = false;
	public static final double p = 0, i = 0, d = 0, kv = 1 / 5, ka = 1 / 4;// zero temporary;
	double timeInterval,oldDL=0,oldDR=0,newDL=0,newDR=0,errorDL=0,errorDR=0,oldVL=0,oldVR=0,newVL=0,newVR=0,errorVL=0,errorVR=0,oldAL=0,oldAR=0,newAL=0,newAR=0,errorAL=0,errorAR=0;
	double angle_difference = 0;
	double currentAngle=0;

	public TrajectoryAuto(String whichAuto) { // possibly add a component of the file name for multiple paths per auto
		/*
		 * left = new File("doubleScaleLeft_left_detailed.csv"); leftTrajectory =
		 * Pathfinder.readFromCSV(left); right = new
		 * File("doubleScaleLeft_right_detailed.csv"); rightTrajectory =
		 * Pathfinder.readFromCSV(right);
		 */

		Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_QUINTIC,
				Trajectory.Config.SAMPLES_FAST, 0.01, 4, 2, 50.0);
		Waypoint[] points = new Waypoint[] { new Waypoint(1.1, 7, 0), new Waypoint(2.3, 3.4, Pathfinder.r2d(-90))};

		Trajectory trajectory = Pathfinder.generate(points, config);
		Trajectory.Segment seg0 = trajectory.get(0);
		// Wheelbase Width = 0.5m
		TankModifier modifier = new TankModifier(trajectory).modify(0.65);

		leftTrajectory = modifier.getLeftTrajectory();
		rightTrajectory = modifier.getRightTrajectory();
		notifierRunning = true;
		drive.resetDistance();
		navx.resetAngle();
		notifier = new Notifier(this::executeTrajectory);
		timeInterval=seg0.dt;
		notifier.startPeriodic(timeInterval);

	}

	private void executeTrajectory() {
		// debug print
		System.out.println("notifier tick " + ticks);
		System.out.println("trajectory length= " + leftTrajectory.length());

		// run notifier if within length
		if (ticks < leftTrajectory.length()) {
			
			//genereate trajectory left and right segments for current tick
			Trajectory.Segment segLeft = leftTrajectory.get(ticks);
			Trajectory.Segment segRight = rightTrajectory.get(ticks);
			
			//calculate distance,velocity,acceleration old
			oldDR=newDR;
			oldDL=newDL; //update dist
			oldVR=newVR;//update vel
			oldVL=newVL;
			oldAR=newAR;//update acc
			oldAL=newAL;
			
			//calculate distance,velocity,acceleration new
			newDR=drive.getRightDistance()/100;
			newDL=drive.getLeftDistance()/100;
			newVR=(newDR-oldDR)/timeInterval;
			newVL=(newDR-oldDR)/timeInterval;
			newAR=(newVR-oldVR)/timeInterval;
			newAL=(newVL-oldVL)/timeInterval;

			//calculate distance,velocity,acceleration error
			errorVL=newVL-segLeft.velocity;
			errorVR=newVR-segRight.velocity;
			errorDL = segLeft.position - newDL;
			errorDR = segRight.position - newDR;
			
			
			//calculate feedforward values
			double kv1=1/4, ka1=1/4;
			double ffLeftAcc = ((segLeft.acceleration) * 1/5.5);
			double ffRightAcc = ((segRight.acceleration) * 1/5.5);
			double ffLeftVel = ((segLeft.velocity) * 1/4);
			double ffRightVel = ((segRight.velocity) * 1/4);
			
			//calculate left and right motor power - final step
			double leftNewPower = ffLeftVel + ffLeftAcc + errorDL * 5;
			double rightNewPower = ffRightVel + ffRightAcc + errorDR * 5;
			
			/*
			//calculate turn adjust
			if (navx.getAngle()<0)
				currentAngle=-navx.getAngle();
			else if (navx.getAngle()==0)
				currentAngle=360-navx.getAngle();
			else
				currentAngle=360-navx.getAngle();
			if (currentAngle>Pathfinder.r2d(segLeft.heading))
				angle_difference=
				angle_difference = ((Pathfinder.r2d(segLeft.heading))- currentAngle);
				*/
			double segAngle=(Pathfinder.r2d(segLeft.heading));
			double navAngle = navx.getAngle();
			
			double angleDifference=segAngle-navAngle;
			
			
			double kG = 0.8 * (-1.0/80.0);//80
			double turn = kG * angle_difference;
			
			//drive robot
			drive.tankDrive(leftNewPower+turn, rightNewPower-turn);
			
			//print to dashboard/shuffleboard
			SmartDashboard.putNumber("ffLeftVel", ffLeftVel);
			SmartDashboard.putNumber("ffLeftAcc", ffLeftAcc);
			
			SmartDashboard.putNumber("left motor power", leftNewPower);
			SmartDashboard.putNumber("right motor power", rightNewPower);
			
			SmartDashboard.putNumber("leftV Setpoint", segLeft.velocity);
			SmartDashboard.putNumber("rightV Setpoint", segRight.velocity);
			
			SmartDashboard.putNumber("leftV calculated", newVL);
			SmartDashboard.putNumber("rightV calculated", newVR);
			
			SmartDashboard.putNumber("leftV error", errorVL);

			
			SmartDashboard.putNumber("leftA Setpoint", segLeft.acceleration);
			SmartDashboard.putNumber("rightA Setpoint", segRight.acceleration);
			
			SmartDashboard.putNumber("leftA calculated", newAL);
			
			SmartDashboard.putNumber("left distance", newDL);
			SmartDashboard.putNumber("right distance", newDR);
			
			SmartDashboard.putNumber("left dist SP", segLeft.position);
			SmartDashboard.putNumber("right dist SP", segRight.position);
			SmartDashboard.putNumber("LDistError", errorDL);
			SmartDashboard.putNumber("RDistError", errorDR);
			ticks++;
		} else {
			drive.tankDrive(0, 0);
			notifierRunning = false;
			notifier.stop();
		}
	}

	public boolean actionReady() {
		return false;
	}

}
