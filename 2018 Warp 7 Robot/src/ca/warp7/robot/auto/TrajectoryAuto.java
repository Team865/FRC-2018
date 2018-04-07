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
    private double p=0,i,d, kv=1/1.5, ka=1/0.8;//v=143 a=70
	private Drive drive = Robot.drive;
	private Navx navx = Robot.navx;	
	
	public TrajectoryAuto (String whichAuto) { //possibly add a component of the file name for multiple paths per auto
			/*
			left = new File("doubleScaleLeft_left_detailed.csv");		
			leftTrajectory = Pathfinder.readFromCSV(left);
			right = new File("doubleScaleLeft_right_detailed.csv");		
			rightTrajectory = Pathfinder.readFromCSV(right);
			*/
		Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_QUINTIC, Trajectory.Config.SAMPLES_HIGH, 0.02, 0.5, 1.0, 50.0);
        Waypoint[] points = new Waypoint[] {
                new Waypoint(0, 0, 0),
                new Waypoint(3, 0, 0)
        };

        Trajectory trajectory = Pathfinder.generate(points, config);
        Trajectory.Segment seg0 = trajectory.get(0);
        // Wheelbase Width = 0.5m
        TankModifier modifier = new TankModifier(trajectory).modify(0.5);
		
        leftTrajectory = modifier.getLeftTrajectory();
        rightTrajectory = modifier.getRightTrajectory();
			
			notifier = new Notifier(this::executeTrajectory);
			notifier.startPeriodic(seg0.dt);
			drive.resetDistance();
			navx.resetAngle();
	}
	
	private void executeTrajectory(){
		if (ticks<leftTrajectory.length()-1) {
		double driveVLeft=drive.leftVelocity()*-100; //convert from backwards centimeters to forward meters
		double driveVRight=drive.leftVelocity()*-100; //convert from backwards centimeters to forward meters
		Trajectory.Segment segLeft = leftTrajectory.get(ticks);
		Trajectory.Segment segRight = rightTrajectory.get(ticks);
		double errorVLeft=segLeft.velocity-driveVLeft;
		double errorVRight=segRight.velocity-driveVRight;
		double errorDLeft=segLeft.position-drive.getLeftDistance();
		double errorDRight=segRight.velocity-drive.getRightDistance();
		double ffLeftAcc=segLeft.acceleration/ka;
		double ffRightAcc=segRight.acceleration/ka;
		double ffLeftVel=segLeft.velocity/kv;
		double ffRightVel=segRight.velocity/kv;
		double leftNewPower=ffLeftVel+ffLeftAcc+errorDLeft*p;
		double rightNewPower=ffRightVel+ffRightAcc+errorDRight*p;
		drive.tankDrive(leftNewPower, rightNewPower);
		System.out.printf("%f,%f,%f,%f,%f,%f,%f,%f\n", 
		        segLeft.dt, segLeft.x, segLeft.y, segLeft.position, segLeft.velocity, 
		            segLeft.acceleration, segLeft.jerk, segLeft.heading);
		//System.out.println("leftP= "+leftNewPower+" rightP= "+ rightNewPower + "rightV= "+driveVRight + " rightVSP= "+segRight.velocity);
		SmartDashboard.putNumber("leftV",driveVLeft);
		SmartDashboard.putNumber("rightV",driveVRight);
		SmartDashboard.putNumber("leftV Setpoint",segLeft.velocity);
		SmartDashboard.putNumber("rightV Setpoint",segRight.velocity);
		//SmartDashboard.putNumber("current leftA",seg);
		//SmartDashboard.putNumber("current rightA",);
		SmartDashboard.putNumber("leftA Setpoint",segLeft.acceleration);
		SmartDashboard.putNumber("rightA Setpoint",segRight.acceleration);
		SmartDashboard.putNumber("left distance",drive.getLeftDistance());
		SmartDashboard.putNumber("right distance",drive.getLeftDistance());
		SmartDashboard.putNumber("left dist SP",segLeft.position);
		SmartDashboard.putNumber("right dist SP",segRight.position);
		ticks++;
		}
	}
	
	public boolean actionReady(){
		return false;
	}
		
	
 }
