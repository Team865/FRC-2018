package ca.warp7.robot.auto;
import java.io.File;

import ca.warp7.robot.Robot;
import ca.warp7.robot.subsystems.Drive;
import ca.warp7.robot.subsystems.Intake;
import ca.warp7.robot.subsystems.Navx;
import edu.wpi.first.wpilibj.Notifier;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
public class TrajectoryAuto {
	private int ticks=0;
	private File left,right;
    public Notifier notifier;
    private Trajectory leftTrajectory,rightTrajectory;
	private Drive drive = Robot.drive;
	private Navx navx = Robot.navx;	
	public boolean notifierRunning=false;
	public static final double p=0,i=0,d=0,kv=1/2,ka=1/3;//zero temporary;

	
	public TrajectoryAuto (String whichAuto) { //possibly add a component of the file name for multiple paths per auto
			/*
			left = new File("doubleScaleLeft_left_detailed.csv");		
			leftTrajectory = Pathfinder.readFromCSV(left);
			right = new File("doubleScaleLeft_right_detailed.csv");		
			rightTrajectory = Pathfinder.readFromCSV(right);
			*/

		
		Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_QUINTIC, Trajectory.Config.SAMPLES_FAST, 0.01, 1.4, 0.9, 50.0);
        Waypoint[] points = new Waypoint[] {
                new Waypoint(0, 0, 0),
                new Waypoint(2, 0, 0)
        };

        Trajectory trajectory = Pathfinder.generate(points, config);
        Trajectory.Segment seg0 = trajectory.get(0);
        // Wheelbase Width = 0.5m
        TankModifier modifier = new TankModifier(trajectory).modify(0.5);
		
        leftTrajectory = modifier.getLeftTrajectory();
        rightTrajectory = modifier.getRightTrajectory();
        notifierRunning=true;
		drive.resetDistance();
		navx.resetAngle();
		notifier = new Notifier(this::executeTrajectory);
		notifier.startPeriodic(seg0.dt);
			
	}
	
	private void executeTrajectory(){
		System.out.println("notifier tick "+ticks);
		System.out.println("trajectory length= "+leftTrajectory.length());
		if (ticks<leftTrajectory.length()) {
		double driveVLeft=drive.leftVelocity()/100; 
		double driveVRight=drive.rightVelocity()/100; 
		Trajectory.Segment segLeft = leftTrajectory.get(ticks);
		Trajectory.Segment segRight = rightTrajectory.get(ticks);
		double errorVLeft=segLeft.velocity-driveVLeft;
		double errorVRight=segRight.velocity-driveVRight;
		double errorDLeft=segLeft.position-drive.getLeftDistance()/100;
		double errorDRight=segRight.velocity-drive.getRightDistance()/100;
		double ffLeftAcc=segLeft.acceleration*1/3;
		double ffRightAcc=segRight.acceleration*1/3;
		double ffLeftVel=segLeft.velocity*1/2;//kv
		double ffRightVel=segRight.velocity*1/2;//kv
		double leftNewPower=ffLeftVel+ffLeftAcc+errorDLeft*p;
		double rightNewPower=ffRightVel+ffRightAcc+errorDRight*p;
		drive.tankDrive(leftNewPower, rightNewPower);
		//System.out.printf("%f,%f,%f,%f,%f,%f,%f,%f\n", 
		        //segLeft.dt, segLeft.x, segLeft.y, segLeft.position, segLeft.velocity, 
		         //   segLeft.acceleration, segLeft.jerk, segLeft.heading);
		//System.out.println("leftP= "+leftNewPower+" rightP= "+ rightNewPower + "rightV= "+driveVRight + " rightVSP= "+segRight.velocity);
		SmartDashboard.putNumber("kv",kv);
		SmartDashboard.putNumber("ffLeftVel",ffLeftVel);
		SmartDashboard.putNumber("ffLeftAcc",ffLeftAcc);
		SmartDashboard.putNumber("left motor power",leftNewPower);
		SmartDashboard.putNumber("right motor power",rightNewPower);
		SmartDashboard.putNumber("leftV Setpoint",segLeft.velocity);
		SmartDashboard.putNumber("rightV Setpoint",segRight.velocity);
		SmartDashboard.putNumber("leftV calculated",driveVLeft);
		SmartDashboard.putNumber("rightV calculated",driveVRight);
		//SmartDashboard.putNumber("current leftA",seg);
		//SmartDashboard.putNumber("current rightA",);
		SmartDashboard.putNumber("leftA Setpoint",segLeft.acceleration);
		SmartDashboard.putNumber("rightA Setpoint",segRight.acceleration);
		SmartDashboard.putNumber("left distance",drive.getLeftDistance());
		SmartDashboard.putNumber("right distance",drive.getRightDistance());
		SmartDashboard.putNumber("left dist SP",segLeft.position);
		SmartDashboard.putNumber("right dist SP",segRight.position);
		SmartDashboard.putNumber("LDistError",drive.getLeftDistance()/100-segLeft.position);
		SmartDashboard.putNumber("RDistError",drive.getRightDistance()/100-segRight.position);
		ticks++;
		}
		else {
			drive.tankDrive(0, 0);
			notifierRunning=false;
			notifier.stop();
			}
	}
	
	public boolean actionReady(){
		return false;
	}
		
	
 }
