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

	// DOUBLE SCALE LEFT
	// tuned for practice bot, not tuned for comp bot
	// uses driveDistanceNoStop, driveDistanceNoStop(runnable turnDrop),
	// angleRelTurnLiftUpNoShoot, alignIntakeCube
	// all speed limits are manually set. lift-speedLimit is disabled at all times
	private void Left_RLR() {
		switch (step) {
		case (0):
			lift.zeroEncoder();
			Timer.delay(0.1);
			lift.setLoc(1); // LIFT
			Timer.delay(0.4); // DELAY TO LET THE LIFT GO UP (IMPORTANT)
			intake.setSpeed(0.3); // INTAKE
			lift.disableSpeedLimit = true;
			autoFunc.setSpeedLimit(0.57); // SPEEDLIMIT
			step++;
			break;
		case (1):
			if (autoFunc.driveDistanceNoStop(200, 0)) { // DRIVE
				autoFunc.setSpeedLimit(0.85); // SPEEDLIMIT
				step++;
			}
			break;
		case (2):
			if (autoFunc.driveDistanceNoStop(300, 0)) { // DRIVE
				step++;
			}
			break;

		case (3):
			if (autoFunc.driveDistanceNoStop(144, 150, () -> customFunc.turnDrop(40, 43))) { // TURN,SHOOT,DROP
				step++;
				intake.setSpeed(1); // INTAKE
				autoFunc.setSpeedLimit(0.45);// SPEEDLIMIT
			}
			break;
		case (4):
			if (autoFunc.alignIntakeCube(154, 4)) { // DRIVE,ALIGNINTAKE
				intake.setSpeed(1); // INTAKE
				autoFunc.setSpeedLimit(0.25); // SPEEDLIMIT
				step++;
			}
			break;
		case (5):
			if (autoFunc.alignIntakeCube(18, 4)) {// DRIVE SUPER SLOW
				step++;
				autoFunc.setSpeedLimit(0.3); // SPEEDLIMIT
				lift.setLoc(1); // LIFT
				intake.setSpeed(1); // INTAKE
			}
			break;
		case (6):
			if (autoFunc.driveDistanceNoStop(-50, 0)) { // DRIVE BACKWARDS
				autoFunc.setSpeedLimit(0.6); // SPEEDLIMIT
				step++;
			}
			break;
		case (7):
			if (autoFunc.angleRelTurnLiftUpNoShoot(-75)) { // TURN
				autoFunc.setSpeedLimit(0.5); // SPEEDLIMIT
				step++;
			}
			break;

		case (8):
			if (autoFunc.driveDistanceNoStop(62, -56)) { // DRIVE,TURN
				intake.setSpeed(-0.5); // OUTTAKE
				Timer.delay(0.2);
				intake.setSpeed(0);
				step++;
			}
			;
			break;
		case (9):
			if (autoFunc.driveDistanceNoStop(-50, 0)) { // DRIVE BACK
				step++;
				lift.setLoc(0); // DROP LIFT
			}
			break;
		} // end switch
	} // end method

	// RIGHT SCALE RIGHT SWITCH DOUBLE CUBE
	// tuned for practice bot, not tuned for comp bot
	// uses driveDistanceNoStop, driveDistanceNoStop(runnable turnDrop),
	// angleRelTurnLiftUpNoShoot, alignIntakeCube
	// all speed limits are manually set. lift-speedLimit is disabled at all times
	private void Left_RRR() {
		switch (step) {
		case (0): {
			lift.zeroEncoder();
			Timer.delay(0.1);
			lift.setLoc(0.4);// LIFT
			intake.setSpeed(0.45); // INTAKE
			lift.disableSpeedLimit = true;
			autoFunc.setSpeedLimit(1); // SPEEDLIMIT
			step++;
			break;
		}
		case (1):
			if (autoFunc.driveDistanceNoStop(305, 0)) { // DRIVE
				autoFunc.setSpeedLimit(0.9); // SPEEDLIMIT
				lift.setLoc(1); // LIFT
				step++;
			}
			break;
		case (2):
			if (autoFunc.driveDistanceNoStop(518, 87)) { // DRIVE,TURN
				autoFunc.setSpeedLimit(0.58); // SPEEDLIMIT
				step++;
			}
			break;
		case (3):
			if (autoFunc.driveDistanceNoStop(291, -253, () -> customFunc.turnDrop(-120, -145))) { // DRIVE,TURN,SHOOT
				autoFunc.setSpeedLimit(0.5); // SPEEDLIMIT
				Timer.delay(0.05);
				intake.setSpeed(1); // INTAKE
				Timer.delay(0.05);
				step++;
			}
			break;

		case (4):
			if (autoFunc.alignIntakeCube(108, 4)) { // DRIVE,ALIGN
				Timer.delay(0.1);
				step++;
			}
			break;

		case (5):
			if (autoFunc.driveDistance(-15,0)) { // DRIVE BACK, LIFT
				intake.setSpeed(0.3); // SLOW DOWN INTAKE
				lift.setLoc(1); // LIFT
				autoFunc.setSpeedLimit(0.85); // SPEEDLIMIT
				Timer.delay(0.7); // DELAY TO LET THE LIFT GO UP (IMPORTANT)
				step++;
			}
			break;

		case (6):
			if (autoFunc.driveDistance(15,0)) { // DRIVE FORWARD, OUTTAKE
				intake.setSpeed(-1); // OUTTAKE
				Timer.delay(0.2);
				intake.setSpeed(0);
				step++;
			}
			break;
		} // end switch
	} // end method

	// LEFT SCALE LEFT SWITCH DOUBLE CUBE
	// tuned for practice bot, not tuned for comp bot
	// uses driveDistanceNoStop, driveDistanceNoStop(runnable turnDrop),
	// angleRelTurnLiftUpNoShoot, alignIntakeCube
	// all speed limits are manually set. lift-speedLimit is disabled at all times
	private void Left_LLL() {
		switch (step) {
		case (0):
			lift.zeroEncoder();
			Timer.delay(0.1);
			lift.setLoc(1); // LIFT
			Timer.delay(0.4); // DELAY TO LET THE LIFT GO UP (IMPORTANT)
			intake.setSpeed(0.3); // INTAKE
			lift.disableSpeedLimit = true;
			autoFunc.setSpeedLimit(0.57); // SPEEDLIMIT
			step++;
			break;
		case (1):
			if (autoFunc.driveDistanceNoStop(200, 0)) { // DRIVE
				autoFunc.setSpeedLimit(0.85); // SPEEDLIMIT
				step++;
			}
			break;
		case (2):
			if (autoFunc.driveDistanceNoStop(300, 0)) {// DRIVE
				step++;
			}
			break;
		case (3):
			if (autoFunc.driveDistanceNoStop(144, 150, () -> customFunc.turnDrop(40, 43))) { // DRIVE,TURN,SHOOT
				step++;
				intake.setSpeed(1); //INTAKE
				autoFunc.setSpeedLimit(0.45); //SPEEDLIMIT
			}
			break;
		case (4):
			if (autoFunc.alignIntakeCube(154, 4)) {// DRIVE, ALIGN
				intake.setSpeed(1); //INTAKE
				autoFunc.setSpeedLimit(0.25); //SPEEDLIMIT
				step++;
			}
			break;
		case (5):
			if (autoFunc.alignIntakeCube(18, 4)) {// DRIVE, ALIGN
				autoFunc.setSpeedLimit(0.3); //SPEEDLIMIT
				lift.setLoc(1); //LIFT
				intake.setSpeed(1); //INTAKE
				step++;
			}
			break;
		case (6):
			if (autoFunc.driveDistance(-10,0)) {//DRIVE BACK
				intake.setSpeed(0.7); //INTAKE
				lift.setLoc(0.5); //LIFT
				Timer.delay(1.35); //DELAY TO LET THE LIFT GO UP (IMPORTANT)
				step++;
			}
			break;

		case (7):
			if (autoFunc.driveDistance(35,0)) {// DRIVE
				intake.setSpeed(-1); //OUTTAKE
				Timer.delay(0.2);
				intake.setSpeed(0);
				step++;
			}
			break;
		// drive backwards TODO so we dont hit intake on switch after auto ends
		
		}//end switch(step)
	}//end method

	// DOUBLE SCALE RIGHT
	// tuned for practice bot, not tuned for comp bot
	// uses driveDistanceNoStop, driveDistanceNoStop(runnable turnDrop),
	// angleRelTurnLiftUpNoShoot, alignIntakeCube
	// all speed limits are manually set. lift-speedLimit is disabled at all times
	private void Left_LRL() {

		switch (step) {
		case (0): {
			lift.zeroEncoder();
			Timer.delay(0.1);
			lift.setLoc(0.4); //LIFT
			intake.setSpeed(0.45); //INTAKE
			lift.disableSpeedLimit = true;
			autoFunc.setSpeedLimit(1); //SPEEDLIMIT
			step++;
			break;
		}
		case (1):
			if (autoFunc.driveDistanceNoStop(305, 0)) { //DRIVE
				step++;
				autoFunc.setSpeedLimit(0.9); //SPEEDLIMIT
				lift.setLoc(1); // LIFT
			}
			break;
		case (2):
			if (autoFunc.driveDistanceNoStop(518, 87)) { //DRIVE, TURN
				autoFunc.setSpeedLimit(0.58); //SPEEDLIMIT
				step++;
			}
			break;
		case (3):
			if (autoFunc.driveDistanceNoStop(291, -253, () -> customFunc.turnDrop(-120, -145))) { //TURN, SHOOT, DROP
				autoFunc.setSpeedLimit(0.5); //SPEEDLIMIT
				Timer.delay(0.05);
				intake.setSpeed(1); //INTAKE
				Timer.delay(0.05);
				step++;
			}
			break;

		case (4):
			if (autoFunc.alignIntakeCube(108, 4)) { //DRIVE, ALIGN
				Timer.delay(0.1); //DELAY WHILE INTAKE RUNS TO FINISH INTAKING
				step++;
			}
			break;

		case (5):
			if (autoFunc.driveDistance(-15,0)) { //DRIVE BACK
				intake.setSpeed(0.3); //INTAKE
				lift.setLoc(1); //LIFT
				autoFunc.setSpeedLimit(0.85); //SPEEDLIMIT
				Timer.delay(0.7); //DELAY TO LET THE LIFT GO UP
				step++;
			}
			break;
		case (6):
			if (autoFunc.driveDistanceNoStop(-60, 0)) { //DRIVE BACK
				autoFunc.setSpeedLimit(0.5);  //SPEEDLIMIT
				step++;
			}
			break;
		case (7):
			if (autoFunc.angleRelTurnLiftUpNoShoot(123)) { //TURN RIGHT WITHOUT DRIVING
				autoFunc.setSpeedLimit(0.58); //SET SPEED LIMIT
				intake.setSpeed(-1); //OUTTAKE
				step++;
			}
			break;
		//TODO Possibly add a drive backwards if we overhang the scale
			
		} //end switch(step)
	} //end method

	private void Right_RLR() {

	}

	private void Right_LRL() {

	}

	private void Right_LLL() {

	}

	private void Right_RRR() {

	}

	// CURRENTLY UNUSED
	private void Middle_LLL() {

	}

	// Middle switch right
	private void Middle_RRR() {

	}

	// Middle switch right - scores 2 cubes then lines up exchange
	// uses driveDistanceNoStop, driveDistanceNoStop(runnable turnDrop),
	// angleRelTurnLiftUpNoShoot, alignIntakeCube
	// all speed limits are manually set. lift-speedLimit is disabled at all times
	private void Middle_RLR() {
		switch (step) {
		case (0):
			lift.disableSpeedLimit = true; 
			autoFunc.setSpeedLimit(0.3); //SPEEDLIMIT
			lift.setLoc(0.5); //LIFT
			step++;
			break;

		case (1):
			if (autoFunc.driveDistanceNoStop(150, 40)) { //DRIVE, TURN RIGHT
				step++;
			}
			break;
		case (2):
			if (autoFunc.driveDistanceNoStop(110, -60)) { //DRIVE, TURN LEFT
				Timer.delay(0.15);
				intake.setSpeed(-0.6); //OUTTAKE
				Timer.delay(0.25);
				intake.setSpeed(0);
				lift.setLoc(0); //DROP LIFT
				step++;
			}
			break;
		case (3):
			if (autoFunc.driveDistanceNoStop(-85, -7)) { //DRIVE BACK
				intake.setSpeed(0.5); //INTAKE
				step++;
			}
			break;
		case (4):
			if (autoFunc.angleRelTurnLiftUpNoShoot(-43.5)) { //TURN
				step++;
				Timer.delay(0.05);
				intake.setSpeed(1); //INTAKE
			}
			break;
		case (5):
			if (autoFunc.alignIntakeCube(145, 4)) {//DRIVE, ALIGN
				step++;
				Timer.delay(0.1);
				intake.setSpeed(1); //INTAKE
				autoFunc.setSpeedLimit(0.3); //SPEEDLIMIT
			}
			break;
		case (6):
			// if (autoFunc.alignIntakeCube(20,4)) {
			step++;
			autoFunc.setSpeedLimit(0.75); //SPEEDLIMIT
			intake.setSpeed(0.5); //INTAKE
			lift.setLoc(0.5); //LIFT
			// }
			break;
		case (7):
			if (autoFunc.driveDistanceNoStop(-100, 250)) { //DRIVE BACK, TURN
				autoFunc.setSpeedLimit(0.9); //SPEEDLIMIT
				step++;
			}
			break;
		case (8):
			if (autoFunc.driveDistanceNoStop(104, 46)) { //TURN, DRIVE
				Timer.delay(0.1);
				intake.setSpeed(-0.4); //OUTTAKE
				Timer.delay(0.25);
				intake.setSpeed(0);
				lift.setLoc(0);
				step++;
			}
			break;
		case (9):
			if (autoFunc.driveDistanceNoStop(-84, -7)) {
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
			if (autoFunc.alignIntakeCube(144, 4)) {
				step++;
				Timer.delay(0.05);
				intake.setSpeed(1);
				autoFunc.setSpeedLimit(1);
			}
			break;

		case (12):
			if (autoFunc.driveDistanceNoStop(10, -100)) {
				intake.setSpeed(0.6);
				step++;
			}
			break;

		case (13):
			if (autoFunc.driveDistanceNoStop(-50, -100)) {
				intake.setSpeed(0.3);
				step++;
			}
			break;

		case (14):
			if (autoFunc.driveDistanceNoStop(100, -89)) {
				step++;
			}
			break;
		}
	}

	// Middle switch left
	// 2 CUBES AND THEN PREPARE TO EXCHANGE
	// NOT TESTED ON COMP BOT
	// uses driveDistanceNoStop, driveDistanceNoStop(runnable turnDrop),
	// angleRelTurnLiftUpNoShoot, alignIntakeCube
	// all speed limits are manually set. lift-speedLimit is disabled at all times
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
			if (autoFunc.driveDistanceNoStop(-85, 7)) {
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
			if (autoFunc.alignIntakeCube(145, 4)) {
				step++;
				Timer.delay(0.1);
				intake.setSpeed(1);
				autoFunc.setSpeedLimit(0.3);
			}
			break;
		case (6):
			step++;
			autoFunc.setSpeedLimit(0.75);
			intake.setSpeed(0.5);
			lift.setLoc(0.5);
			break;
		case (7):
			if (autoFunc.driveDistanceNoStop(-100, -250)) {
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
			if (autoFunc.driveDistanceNoStop(-80, 7)) {
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
			if (autoFunc.alignIntakeCube(144, 4)) {
				step++;
				Timer.delay(0.05);
				intake.setSpeed(1);
				autoFunc.setSpeedLimit(1);
			}
			break;

		case (12):
			if (autoFunc.driveDistanceNoStop(10, 100)) {
				intake.setSpeed(0.6);
				step++;
			}
			break;

		case (13):
			if (autoFunc.driveDistanceNoStop(-50, 100)) {
				intake.setSpeed(0.3);
				step++;
			}
			break;

		case (14):
			if (autoFunc.driveDistanceNoStop(100, 89)) {
				step++;
			}
			break;
		}

	}
	
	//NONE METHODS: CURRENTLY ONLY FOR TESTING
	private void None_RLR() {

	}

	private void None_LRL() {

	}

	private void None_LLL() {

	}

	private void None_RRR() {
	}
}
