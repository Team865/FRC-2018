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

public class TrajectoryAuto {
	private int ticks=0;
	private File left,right;
    public Notifier notifier;
    private Trajectory leftTrajectory,rightTrajectory;
    private double p=0,i,d,v,a, kv=1.8, ka=2;//v=143 a=70
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
                new Waypoint(2, 0, 0)
        };

        Trajectory trajectory = Pathfinder.generate(points, config);

        // Wheelbase Width = 0.5m
        TankModifier modifier = new TankModifier(trajectory).modify(0.5);
		
        leftTrajectory = modifier.getLeftTrajectory();
        rightTrajectory = modifier.getRightTrajectory();
			
			notifier = new Notifier(this::executeTrajectory);
			notifier.startPeriodic(0.05);
			drive.resetDistance();
			navx.resetAngle();
	}
	
	private void executeTrajectory(){
		if (ticks<leftTrajectory.length()-1) {
		double driveVLeft=drive.leftVelocity()*-0.01;
		double driveVRight=drive.leftVelocity()*-0.01;
		System.out.println(ticks);
		Trajectory.Segment segLeft = leftTrajectory.get(ticks);
		Trajectory.Segment segRight = rightTrajectory.get(ticks);
		double errorVLeft=segLeft.velocity-driveVLeft;
		double errorVRight=segRight.velocity-driveVRight;
		double ffLeftAcc=segLeft.acceleration/ka;
		double ffRightAcc=segRight.acceleration/ka;
		double ffLeftVel=segLeft.velocity/kv;
		double ffRightVel=segRight.velocity/kv;
		double leftNewPower=ffLeftVel+ffLeftAcc+errorVLeft*p;
		double rightNewPower=ffRightVel+ffRightAcc+errorVRight*p;
		System.out.println("leftP= "+leftNewPower+" rightP= "+ rightNewPower + "rightV= "+driveVRight + " rightVSP= "+segRight.velocity);
		drive.tankDrive(leftNewPower, rightNewPower);
		
		ticks++;
		}
	}
	
	public boolean actionReady(){
		return false;
	}
		
	
 }
