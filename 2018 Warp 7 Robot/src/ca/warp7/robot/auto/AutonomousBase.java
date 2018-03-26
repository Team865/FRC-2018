package ca.warp7.robot.auto;

import static ca.warp7.robot.Constants.CUBE_DISTANCE_M;

import com.stormbots.MiniPID;

import static ca.warp7.robot.Constants.CUBE_DISTANCE_B;

import ca.warp7.robot.Robot;
import ca.warp7.robot.misc.DataPool;
import ca.warp7.robot.misc.RTS;
import ca.warp7.robot.subsystems.Drive;
import ca.warp7.robot.subsystems.Intake;
import ca.warp7.robot.subsystems.Lift;
import ca.warp7.robot.subsystems.Limelight;
import ca.warp7.robot.subsystems.Navx;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonomousBase {
	public static DataPool autoPool = new DataPool("auto");
	private Drive drive = Robot.drive;
	private Navx navx = Robot.navx;
	private Limelight limelight = Robot.limelight;
	private Intake intake = Robot.intake;
	private Lift lift = Robot.lift;

	public static AutoFunctions autoFunc = new AutoFunctions();
	private static CustomFunctions customFunc = new CustomFunctions();

	private int step = 0;

	public void autonomousPeriodic(String gameData, int pin) {
		if (pin == 0) { // None
			// System.out.println("pin 0 active :None:");
			if (gameData.equals("RRR"))
				None_RRR();
			else if (gameData.equals("LLL"))
				None_LLL();
			else if (gameData.equals("LRL"))
				None_LRL();
			else if (gameData.equals("RLR"))
				None_RLR();
		} else if (pin == 1) { // Left
			System.out.println("pin 1 active :Left:");
			if (gameData.equals("RRR"))
				Left_RRR();
			else if (gameData.equals("LLL"))
				Left_LLL();
			else if (gameData.equals("LRL"))
				Left_LRL();
			else if (gameData.equals("RLR"))
				Left_RLR();
		} else if (pin == 2) { // Middle
			System.out.println("pin 2 active :Middle:");
			if (gameData.equals("RRR"))
				Middle_RRR();
			else if (gameData.equals("LLL"))
				Middle_LLL();
			else if (gameData.equals("LRL"))
				Middle_LRL();
			else if (gameData.equals("RLR"))
				Middle_RLR();
		} else if (pin == 3) { // Right
			System.out.println("pin 3 active :Right:");
			if (gameData.equals("RRR"))
				Right_RRR();
			else if (gameData.equals("LLL"))
				Right_LLL();
			else if (gameData.equals("LRL"))
				Right_LRL();
			else if (gameData.equals("RLR"))
				Right_RLR();
		}
	}

	private void Left_RLR() {
		// DOUBLE SCALE left working
		switch (step) {
		case (0):			
			lift.zeroEncoder();
			Timer.delay(0.1);
			lift.setLoc(1);
			Timer.delay(0.4);
			intake.setSpeed(0.3);
			lift.disableSpeedLimit = true;
			autoFunc.setSpeedLimit(0.57);//
			step++;
			break;
		case (1):
			if (autoFunc.driveDistanceNoStop(200, 0)) {
				autoFunc.setSpeedLimit(0.85);
				step++;
			}
			break;
		case (2):
			if (autoFunc.driveDistanceNoStop(300, 0)) {
				step++;
			}
			break;

		case (3):
			if (autoFunc.driveDistanceNoStop(144, 150, () -> customFunc.turnDrop(40, 43))) {
				step++;
				intake.setSpeed(1);
				autoFunc.setSpeedLimit(0.45);
			}
			break;
		case (4):
			if (autoFunc.alignIntakeCube(154, 4)) {
				intake.setSpeed(1);
				autoFunc.setSpeedLimit(0.25);
				step++;
			}
			break;
		case (5):
			if (autoFunc.alignIntakeCube(18, 4)) {
				step++;
				autoFunc.setSpeedLimit(0.3);
				lift.setLoc(1);
				intake.setSpeed(1);
			}
			break;
		case (6):
			if (autoFunc.driveDistanceNoStop(-50, 0)) {
				autoFunc.setSpeedLimit(0.6);
				step++;
			}
			break;
		case (7):
			if (autoFunc.angleRelTurnLiftUpNoShoot(-75)) {
				autoFunc.setSpeedLimit(0.5);
				step++;
			}
			break;

		case (8):
			if (autoFunc.driveDistanceNoStop(62, -56)) {
				intake.setSpeed(-0.5);
				Timer.delay(0.2);
				intake.setSpeed(0);
				step++;
			}
			break;
		case(9):
			if (autoFunc.driveDistanceNoStop(-50, 0)) {
				step++;
				lift.setLoc(0);
			}
			break;
	}
		/*
		switch (step) {
		case (0):
			if (autoFunc.driveDistance(585)) {
				lift.setLoc(1);
				step++;
			}
			break;
		case (1):
			if (autoFunc.angleRelTurn(22.5)) {
				autoFunc.setSpeedLimit(0.75);
				step++;
			}
			break;
		case (2):
			if (autoFunc.driveDistance(100)) {
				System.out.println("Exiting drive because im done");
				autoFunc.setSpeedLimit(1);
				intake.setSpeed(-1);
				Timer.delay(0.2);
				intake.setSpeed(0);
				step++;
			}
			break;

		case (3):
			if (autoFunc.angleRelTurn(90)) {
				step++;
			}
			break;
			
		}*/
	}

	private void Left_RRR() {
		// WORKING RIGHT SCALE RIGHT SWITCH DOUBLE CUBE march 25
		switch (step) {
		case (0): {
			lift.zeroEncoder();
			Timer.delay(0.1);
			lift.setLoc(0.3);
			intake.setSpeed(0.3);
			lift.disableSpeedLimit = true;
			step++;
			break;
		}
		case (1):
			if (autoFunc.driveDistanceNoStop(317, 0)) {
				step++;
				autoFunc.setSpeedLimit(0.9);
				lift.setLoc(1); //LIFT
			}
			break;
		case (2):
			if (autoFunc.driveDistanceNoStop(537.5, 87)) {
				autoFunc.setSpeedLimit(0.55);
				step++;
			}
			break;
		case (3):
			// if (autoFunc.driveDistanceNoStop(100, -70)) {

			step++;
			// }
			break;
		case (4):
			if (autoFunc.driveDistanceNoStop(300, -252, () -> customFunc.turnDrop(-120, -125))) {
				autoFunc.setSpeedLimit(0.43);
				intake.setSpeed(1);
				step++;
			}
			break;

		case (5):
			if (autoFunc.alignIntakeCube(120, 4)) {
				Timer.delay(0.25);
				step++;
			}
			break;

		case (6):
			if (autoFunc.driveDistance(-15)) {
				intake.setSpeed(0.3);
				lift.setLoc(0.5);
				Timer.delay(1);
				step++;
			}
			break;

		case (7):
			if (autoFunc.driveDistance(15)) {
				intake.setSpeed(-1);
				Timer.delay(0.2);
				intake.setSpeed(0);
				step++;
			}
			break;
		}
	}

	private void Left_LLL() {
		// WORKING LEFT SCALE LEFT SWITCH DOUBLE CUBE march 24
		switch (step) {
		case (0):			
			lift.zeroEncoder();
			Timer.delay(0.1);
			lift.setLoc(1);
			Timer.delay(0.4);
			intake.setSpeed(0.3);
			lift.disableSpeedLimit = true;
			autoFunc.setSpeedLimit(0.57);//
			step++;
			break;
		case (1):
			if (autoFunc.driveDistanceNoStop(200, 0)) {
				autoFunc.setSpeedLimit(0.85);
				step++;
			}
			break;
		case (2):
			if (autoFunc.driveDistanceNoStop(300, 0)) {
				step++;
			}
			break;

		case (3):
			if (autoFunc.driveDistanceNoStop(144, 150, () -> customFunc.turnDrop(40, 43))) {
				step++;
				intake.setSpeed(1);
				autoFunc.setSpeedLimit(0.45);
			}
			break;
		case (4):
			if (autoFunc.alignIntakeCube(154, 4)) {
				intake.setSpeed(1);
				autoFunc.setSpeedLimit(0.25);
				step++;
			}
			break;
		case (5):
			if (autoFunc.alignIntakeCube(18, 4)) {
				step++;
				autoFunc.setSpeedLimit(0.3);
				lift.setLoc(1);
				intake.setSpeed(1);
			}
			break;
		case (6):
			if (autoFunc.driveDistance(-10)) {
				intake.setSpeed(0.7);
				lift.setLoc(0.5);
				Timer.delay(1.35);
				step++;
			}
			break;

		case (7):
			if (autoFunc.driveDistance(35)) {
				intake.setSpeed(-1);
				Timer.delay(0.2);
				intake.setSpeed(0);
				step++;
			}
			break;
		}
	}

	private void Left_LRL() {

		//double scale test
		switch (step) {
		case (0): {
			lift.zeroEncoder();
			Timer.delay(0.1);
			lift.setLoc(0.3);
			intake.setSpeed(0.45);
			lift.disableSpeedLimit = true;
			step++;
			break;
		}
		case (1):
			if (autoFunc.driveDistanceNoStop(312, 0)) {
				step++;
				autoFunc.setSpeedLimit(0.9);
				lift.setLoc(1); //LIFT
			}
			break;
		case (2):
			if (autoFunc.driveDistanceNoStop(540, 87)) {
				autoFunc.setSpeedLimit(0.55);
				step++;
			}
			break;
		case (3):
			// if (autoFunc.driveDistanceNoStop(100, -70)) {

			step++;
			// }
			break;
		case (4):
			if (autoFunc.driveDistanceNoStop(285, -253, () -> customFunc.turnDrop(-120, -145))) {
				autoFunc.setSpeedLimit(0.6);
				intake.setSpeed(1);
				step++;
			}
			break;


		case (5):
			if (autoFunc.alignIntakeCube(105, 4)) {
				Timer.delay(0.15);
				step++;
			}
			break;

		case (6):
			if (autoFunc.driveDistance(-15)) {
				intake.setSpeed(0.3);
				lift.setLoc(0.6);
				autoFunc.setSpeedLimit(0.85);
				Timer.delay(1);
				step++;
			}
			break;
		case (7):
			if (autoFunc.driveDistanceNoStop(-60, 0)) {
				lift.setLoc(1);
				autoFunc.setSpeedLimit(0.65);
				step++;
			}
			break;
		case (8):
			if (autoFunc.angleRelTurnLiftUpNoShoot(134)) {
				autoFunc.setSpeedLimit(0.7);
				step++;
			}
			break;

		case (9):
			if (autoFunc.driveDistanceNoStop(15, 0)) {
				intake.setSpeed(-0.5);
				Timer.delay(0.1);
				intake.setSpeed(0);
				autoFunc.setSpeedLimit(0.5);
				step++;
			}
			break;
		case(10):
			if (autoFunc.driveDistanceNoStop(-35, 0)) {
				step++;
				lift.setLoc(0);
			}
			break;
	}	}

	private void Right_RLR() {

	}

	private void Right_LRL() {
		// TODO Auto-generated method stub

	}

	private void Right_LLL() {
		// TODO Auto-generated method stub

	}

	private void Right_RRR() {

	}

	// Middle switch left
	private void Middle_LLL() {
		switch (step) {
		case (0):
			lift.setLoc(0.5);
			Timer.delay(1);
			step++;
			break;
		case (1):
			if (autoFunc.driveDistance(295, () -> customFunc.driveTurn(10, -25))) {
				intake.setSpeed(-1);
				Timer.delay(0.2);
				intake.setSpeed(0);
				step++;
			}
			break;
		}
	}

	// Middle switch right
	private void Middle_RRR() {

	}

	// Middle switch right
	private void Middle_RLR() {

	}

	// Middle switch left
	private void Middle_LRL() {
		switch (step) {
		case (0):
			if (autoFunc.angleRelTurn(-22.5)) {
				lift.setLoc(0.4);
				step++;
			}
			break;

		case (1): {
			if (autoFunc.driveDistance(280)) {
				intake.setSpeed(-1);
				Timer.delay(0.2);
				intake.setSpeed(0);
				step++;
			}
		}
		}
	}

	private void None_RLR() { // TESTING

	}

	private void None_LRL() {
		// working on this. double cube opposite sides
		/*
		 * switch (step) { case (0): lift.zeroEncoder(); Timer.delay(0.1);
		 * intake.setSpeed(0.3); lift.disableSpeedLimit = true; step++;
		 * lift.setLoc(0.55); break; case (1): if (autoFunc.driveDistanceNoStop(300, 0))
		 * { autoFunc.setSpeedLimit(0.65); step++; } break;
		 * 
		 * case (2): if (autoFunc.angleRelTurnNoStop(315, () -> customFunc.turnDrop(70,
		 * 140))) { step++; } break;
		 * 
		 * case (3): if (autoFunc.driveDistanceNoStop(200, 130)){
		 * autoFunc.setSpeedLimit(0.65); step++; } break; //case (4): if
		 * (autoFunc.alignIntakeCube(dist, angleThresh))
		 */
	}

	private void None_LLL() {}
	/*
	 * case (3): if (autoFunc.driveDistanceNoStop(300,150)) { step++; } break; /*
	 * case (3): if (autoFunc.alignIntakeCube(175, 4)) {
	 * autoFunc.setSpeedLimit(0.4); step++; } break; case (4): if
	 * (autoFunc.alignIntakeCube(70, 4)) { autoFunc.setSpeedLimit(0.75); step++; }
	 * break; case (5): if (autoFunc.driveDistance(-240)) { intake.setSpeed(0.7);
	 * //lift.setLoc(0.5); Timer.delay(1.35); step++; } break;
	 * 
	 * case (7): if (autoFunc.angleRelTurn(156, () -> customFunc.turnDrop(27, 50)))
	 * { Timer.delay(0.35); autoFunc.setSpeedLimit(0.7); intake.setSpeed(1); step++;
	 * }
	 */

	private void None_RRR() {}
}
