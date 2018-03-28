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
				}+
				break;
			case (9):
				if (autoFunc.driveDistanceNoStop(-50, 0)) {
					step++;
					lift.setLoc(0);
				}
				break;
		}
	}

	private void Left_RRR() {
		// WORKING RIGHT SCALE RIGHT SWITCH DOUBLE CUBE march 25
		switch (step) {
			case (0): {
				lift.zeroEncoder();
				Timer.delay(0.1);
				lift.setLoc(0.4);
				intake.setSpeed(0.45);
				lift.disableSpeedLimit = true;
				step++;
				break;
			}
			case (1):
				if (autoFunc.driveDistanceNoStop(305, 0)) {
					step++;
					autoFunc.setSpeedLimit(0.9);
					lift.setLoc(1); // LIFT
				}
				break;
			case (2):
				if (autoFunc.driveDistanceNoStop(518, 87)) {
					autoFunc.setSpeedLimit(0.58);
					step++;
				}
				break;
			case (3):
				// if (autoFunc.driveDistanceNoStop(100, -70)) {
	
				step++;
				// }
				break;
			case (4):
				if (autoFunc.driveDistanceNoStop(291, -253, () -> customFunc.turnDrop(-120, -145))) {
					autoFunc.setSpeedLimit(0.5);
					Timer.delay(0.05);
					intake.setSpeed(1);
					Timer.delay(0.05);
	
					step++;
				}
				break;
	
			case (5):
				if (autoFunc.alignIntakeCube(108, 4)) {
					Timer.delay(0.1);
					step++;
				}
				break;
	
			case (6):
				if (autoFunc.driveDistance(-15)) {
					intake.setSpeed(0.3);
					lift.setLoc(1);
					autoFunc.setSpeedLimit(0.85);
					Timer.delay(0.7);
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
		// TODO shoot second cube a little later, almost hit the wood and missed switch.
		// lift it higher
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
			// drive backwards TODO so we dont hit intake on switch after auto ends
		}
	}

	private void Left_LRL() {

		// double scale test right side
		switch (step) {
			case (0): {
				lift.zeroEncoder();
				Timer.delay(0.1);
				lift.setLoc(0.4);
				intake.setSpeed(0.45);
				lift.disableSpeedLimit = true;
				step++;
				break;
			}
			case (1):
				if (autoFunc.driveDistanceNoStop(305, 0)) {
					step++;
					autoFunc.setSpeedLimit(0.9);
					lift.setLoc(1); // LIFT
				}
				break;
			case (2):
				if (autoFunc.driveDistanceNoStop(518, 87)) {
					autoFunc.setSpeedLimit(0.58);
					step++;
				}
				break;
			case (3):
				// if (autoFunc.driveDistanceNoStop(100, -70)) {
	
				step++;
				// }
				break;
			case (4):
				if (autoFunc.driveDistanceNoStop(291, -253, () -> customFunc.turnDrop(-120, -145))) {
					autoFunc.setSpeedLimit(0.5);
					Timer.delay(0.05);
					intake.setSpeed(1);
					Timer.delay(0.05);
	
					step++;
				}
				break;
	
			case (5):
				if (autoFunc.alignIntakeCube(108, 4)) {
					Timer.delay(0.1);
					step++;
				}
				break;
	
			case (6):
				if (autoFunc.driveDistance(-15)) {
					intake.setSpeed(0.3);
					lift.setLoc(1);
					autoFunc.setSpeedLimit(0.85);
					Timer.delay(0.7);
					step++;
				}
				break;
			case (7):
				if (autoFunc.driveDistanceNoStop(-60, 0)) {
					lift.setLoc(1);
					autoFunc.setSpeedLimit(0.5);
					step++;
				}
				break;
			case (8):
				if (autoFunc.angleRelTurnLiftUpNoShoot(123)) {
					autoFunc.setSpeedLimit(0.58);
					intake.setSpeed(-1);
					step++;
				}
				break;
		}
	}

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

	// Middle switch left (march 26: idk if this works. -kaelan)
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
		switch (step) {
			case (0):
				lift.disableSpeedLimit = true;
				lift.setLoc(0.5);
				step++;
				break;
		
			case (1):
				if (autoFunc.driveDistanceNoStop(150, 40)) {
					step++;
				}
				break;
			case (2):
				if (autoFunc.driveDistanceNoStop(110, -60)) {
					Timer.delay(0.15);
					intake.setSpeed(-0.6);
					Timer.delay(0.25);
					intake.setSpeed(0);
					lift.setLoc(0);
					step++;
				}
				break;
			case (3):
				if (autoFunc.driveDistanceNoStop(-85,-7)) {
					intake.setSpeed(0.5);
					step++;
				}
				break;
			case (4):
				if (autoFunc.angleRelTurnLiftUpNoShoot(-43.5)) {
					step++;
					Timer.delay(0.05);
					intake.setSpeed(1);
				}
				break;
			case (5):
				if (autoFunc.alignIntakeCube(145,4)) {
					step++;
					Timer.delay(0.1);
					intake.setSpeed(1);
					autoFunc.setSpeedLimit(0.3);
				}
				break;
			case (6):
				//if (autoFunc.alignIntakeCube(20,4)) {
					step++;
					autoFunc.setSpeedLimit(0.75);
					intake.setSpeed(0.5);
					lift.setLoc(0.5);
				//}
				break;
			case (7):
				if (autoFunc.driveDistanceNoStop(-100,250)) {
					step++;
					autoFunc.setSpeedLimit(0.9);
				}
				break;
			case (8):
				if (autoFunc.driveDistanceNoStop(104, 46)) {
					Timer.delay(0.1);
					intake.setSpeed(-0.4);
					Timer.delay(0.25);
					intake.setSpeed(0);
					lift.setLoc(0);
					step++;
				}
				break;
			case (9):
				if (autoFunc.driveDistanceNoStop(-84,-7)) {
					intake.setSpeed(0.5);
					step++;
				}
				break;
			case (10):
				if (autoFunc.angleRelTurnLiftUpNoShoot(-42)) {
					step++;
					Timer.delay(0.05);
					intake.setSpeed(1);
				}
				break;
			case (11):
				if (autoFunc.alignIntakeCube(144,4)) {
					step++;
					Timer.delay(0.05);
					intake.setSpeed(1);
					autoFunc.setSpeedLimit(1);
				}
				break;
				
			case (12):
				if (autoFunc.driveDistanceNoStop(10,-100)) {
					intake.setSpeed(0.6);
					step++;
				}
				break;
				
			case (13):
				if (autoFunc.driveDistanceNoStop(-50,-100)) {
					intake.setSpeed(0.3);
					step++;
				}
				break;
			
			case (14):
				if (autoFunc.driveDistanceNoStop(100,-89)) {
					step++;
				}
				break;
		}
	}

	// Middle switch left
	private void Middle_LRL() {
		switch (step) {
			case (0):
				lift.disableSpeedLimit = true;
				lift.setLoc(0.5);
				step++;
				break;
	
			case (1):
				if (autoFunc.driveDistanceNoStop(150, -40)) {
					step++;
				}
				break;
			case (2):
				if (autoFunc.driveDistanceNoStop(155, 60)) {
					Timer.delay(0.15);
					intake.setSpeed(-0.6);
					Timer.delay(0.25);
					intake.setSpeed(0);
					lift.setLoc(0);
					step++;
				}
				break;
			case (3):
				if (autoFunc.driveDistanceNoStop(-85,7)) {
					intake.setSpeed(0.5);
					step++;
				}
				break;
			case (4):
				if (autoFunc.angleRelTurnLiftUpNoShoot(43.5)) {
					step++;
					Timer.delay(0.05);
					intake.setSpeed(1);
				}
				break;
			case (5):
				if (autoFunc.alignIntakeCube(145,4)) {
					step++;
					Timer.delay(0.1);
					intake.setSpeed(1);
					autoFunc.setSpeedLimit(0.3);
				}
				break;
			case (6):
				//if (autoFunc.alignIntakeCube(20,4)) {
					step++;
					autoFunc.setSpeedLimit(0.75);
					intake.setSpeed(0.5);
					lift.setLoc(0.5);
				//}
				break;
			case (7):
				if (autoFunc.driveDistanceNoStop(-100,-250)) {
					step++;
					autoFunc.setSpeedLimit(0.9);
				}
				break;
			case (8):
				if (autoFunc.driveDistanceNoStop(100, -46)) {
					Timer.delay(0.1);
					intake.setSpeed(-0.4);
					Timer.delay(0.25);
					intake.setSpeed(0);
					lift.setLoc(0);
					step++;
				}
				break;
			case (9):
				if (autoFunc.driveDistanceNoStop(-80,7)) {
					intake.setSpeed(0.5);
					step++;
				}
				break;
			case (10):
				if (autoFunc.angleRelTurnLiftUpNoShoot(42)) {
					step++;
					Timer.delay(0.05);
					intake.setSpeed(1);
				}
				break;
			case (11):
				if (autoFunc.alignIntakeCube(144,4)) {
					step++;
					Timer.delay(0.05);
					intake.setSpeed(1);
					autoFunc.setSpeedLimit(1);
				}
				break;
				
			case (12):
				if (autoFunc.driveDistanceNoStop(10,100)) {
					intake.setSpeed(0.6);
					step++;
				}
				break;
				
			case (13):
				if (autoFunc.driveDistanceNoStop(-50,100)) {
					intake.setSpeed(0.3);
					step++;
				}
				break;
			
			case (14):
				if (autoFunc.driveDistanceNoStop(100,89)) {
					step++;
				}
				break;
				
			/*case (14):
				if (autoFunc.driveDistanceNoStop(100,0)) {
					step++;
					intake.setSpeed(0.6);
				}
				break;*/
	
		}
		
	}

	private void None_RLR() { // TESTING

	}

	private void None_LRL() {
	
	}

	private void None_LLL() {
		switch (step) {
			case (0):
				lift.zeroEncoder();
				lift.disableSpeedLimit = true;
				step++;
				break;
			
			case (1):
				if (autoFunc.driveDistance(300)) {
					step++;
				}
				break;
		}
	}

	private void None_RRR() {
	}
}
