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
import edu.wpi.first.wpilibj.Notifier;

public class AutonomousBase {
	public static DataPool autoPool = new DataPool("auto");
	private Drive drive = Robot.drive;
	private Navx navx = Robot.navx;
	private Limelight limelight = Robot.limelight;
	private Intake intake = Robot.intake;
	public Lift lift = Robot.lift;
	public static AutoFunctions autoFunc = new AutoFunctions();
	private static CustomFunctions customFunc = new CustomFunctions();
	

	private int step = 0;

	public void autonomousPeriodic(String gameData, int pin) {
		if (pin == 0) { // None
			// System.out.println("pin 0 active :None:");
			if (gameData.equals("RRR")) {
				baseLine();
			} else if (gameData.equals("LLL")) {
				baseLine();
			} else if (gameData.equals("LRL")) {
				baseLine();
			} else if (gameData.equals("RLR")) {
				baseLine();
			}
		} else if (pin == 1) { // Left
			// System.out.println("pin 1 active :Left:");
			if (gameData.equals("RRR")) {
				LeftStart_doubleScaleRight();
			} else if (gameData.equals("LLL")) {
				LeftStart_doubleScaleLeft();
			} else if (gameData.equals("LRL")) {
				LeftStart_doubleScaleRight();
			} else if (gameData.equals("RLR")) {
				LeftStart_doubleScaleLeft();
			}
		} else if (pin == 2) { // Middle
			System.out.println("pin 2 active :Middle:");
			if (gameData.equals("RRR")) {
				MiddleSwitch_Right();			
				} else if (gameData.equals("LLL")) {
				MiddleSwitch_Left();
			} else if (gameData.equals("LRL")) {
				MiddleSwitch_Left();
			} else if (gameData.equals("RLR")) {
				MiddleSwitch_Right();			}
		} else if (pin == 3) { // Right
			System.out.println("pin 3 active :Right:");
			if (gameData.equals("RRR")) {
				
			} else if (gameData.equals("LLL")) {

			} else if (gameData.equals("LRL")) {
				
			} else if (gameData.equals("RLR")) {
			}
		}
	}

	// SINGLE SCALE AUTOS

	private void switchLeft() {
		// TODO Auto-generated method stub
		switch (step) { // turn
		case (0):
			lift.zeroEncoder();
			Timer.delay(0.01);
			intake.setSpeed(0.45); // INTAKE
			lift.disableSpeedLimit = true;
			autoFunc.setSpeedLimit(0.65); // SPEEDLIMIT
			lift.setLoc(0.5);
			step++;
			break;
			
		case (1):
			if (autoFunc.driveDistance(420, 0,15,true))
				step++;
			break;
		case (2):
			if (autoFunc.angleRelTurnLiftUpNoShoot(90,true)) {
				step++;
			
			}
			break;
		case (3):
			if (autoFunc.driveDistance(30, 0,15,true)) {
				step++;
				intake.setSpeed(-0.5);
			}
			break;
		}
		
	}

	private void LeftStart_doubleScaleRight() {
		switch (step) {
		case (0): {
			lift.zeroEncoder();
			Timer.delay(0.01);
			intake.setSpeed(0.45); // INTAKE
			lift.disableSpeedLimit = true;
			autoFunc.setSpeedLimit(1); // SPEEDLIMIT
			step++;
			break;
		}
		case (1):
			if (autoFunc.driveDistanceNoStop(350, 0,true)) { // DRIVE
				step++;
				autoFunc.setSpeedLimit(0.7); // SPEEDLIMIT
				lift.setLoc(0.55); // LIFT
			}
			break;
		case (2):
			if (autoFunc.driveDistanceNoStop(510, 90,true)) { // DRIVE, TURN
				autoFunc.setSpeedLimit(0.6); // DONT TOUCH THIS SPEEDLIMIT, IT EFFEFCTS THE RADIUS OF THE CURVE
				lift.setLoc(1); // LIFT
				step++;
			}
			break;
		case (3): //!!!add 40 CM TO THIS (DONE ALREADY) (so change the 291 to 331) !!!
			if (autoFunc.driveDistanceNoStop(335, -254,false, () -> customFunc.turnDrop(-130, -140))) { // TURN, SHOOT, DROP
				autoFunc.setSpeedLimit(0.4); // SPEEDLIMIT
				lift.overrideIntake=true;
				intake.setSpeed(1); // INTAKE
				Timer.delay(0.01);
				step++;
			}
			break;
		case (4): //!!!remove 40 CM from this (DONE ALREADY) (so change the 158 to 118) !!!
			if (autoFunc.alignIntakeCube(185, 4, true)) { // DRIVE, ALIGN
				Timer.delay(0.01); // DELAY WHILE INTAKE RUNS TO FINISH INTAKING
				step++;
			}
			break;

		case (5):
			if (autoFunc.driveDistance(-15, 0,15,true)) { // DRIVE BACK
				intake.setSpeed(0.3); // INTAKE
				lift.setLoc(0.25); // LIFT
				autoFunc.setSpeedLimit(0.5); // SPEEDLIMIT
				Timer.delay(1); // DELAY TO LET THE LIFT GO UP
				step++;
			}
			break;
		case (6):
			if (autoFunc.driveDistanceNoStop(-75, 0,true)) { // DRIVE BACK
				autoFunc.setSpeedLimit(0.45); // SPEEDLIMIT
				step++;
			}
			break;
		case (7):
			if (autoFunc.angleRelTurnLiftUpNoShoot(118,true)) { // TURN RIGHT WITHOUT DRIVING
				autoFunc.setSpeedLimit(0.58); // SET SPEED LIMIT
				//intake.setSpeed(-1); // OUTTAKE
				step++;
				lift.overrideIntake=false;
			}
			break;
		// TODO Possibly add a drive backwards if we overhang the scale

		} // end switch(step)
	} // end method
	//copy and paste case statement 
	/*
case ():
	if () {
		step++;
	}
break;
*/
	private void LeftStart_doubleScaleLeft() {
		switch (step) {
		case (0): // SETUP-RIGHT SIDE OF THE ROBOT SHOULD HAVE ONE FOOT OF CLEARANCE FROM SWITCH
			lift.disableSpeedLimit = true; // DISABLE SPEEDLIMIT LIFT OVERRIDE
			lift.setLoc(1); // LIFT
			autoFunc.setSpeedLimit(1); // SPEEDLIMIT
			step++;
			break;
		case (1):
			if (autoFunc.driveDistanceNoStop(330, 0, true)) { // drive until intake overhangs the line, measured at 630cm
				autoFunc.setSpeedLimit(0.4);
				step++;
			}
			break;
		case (2):
			if (autoFunc.driveDistance(327.5, 15, 15,true)) { // drive until intake overhangs the line, measured at 630cm
				step++;
				autoFunc.setSpeedLimit(0.65);
			}
			break;
		case (3): 
			intake.setSpeed(-1);
			Timer.delay(0.2);
			intake.setSpeed(0);
			lift.setLoc(0);
			Timer.delay(0.4);
			step++;
		case (4):
			if (autoFunc.angleRelTurnLiftUpNoShoot(148.5,true)){
				step++;
			}
			break;
		
		case (5):
			if (autoFunc.driveDistanceNoStop(90,0,true)) {
				autoFunc.setSpeedLimit(0.27);
				lift.overrideIntake=true;
				intake.setSpeed(1);
				step++;
			}
			break; 
		case (6):
			if (autoFunc.alignIntakeCube(70, 4, true)){
				step++;
				lift.setLoc(1);
				Timer.delay(0.4);
				autoFunc.setSpeedLimit(0.32);
				lift.overrideIntake=false;
			}
			break;
			//DOUBLE SCALE TESTING STARTS BELOW HERE
			//UPDATE THE CASE WHEN READY TO TEST NEXT PART
		
		case (7):
			if (autoFunc.driveDistanceNoStop(-135, -5, true)) { // DRIVE BACKWARDS
				autoFunc.setSpeedLimit(0.5); // SPEEDLIMIT
				step++;
			}
			break;
		case (8):
			if (autoFunc.angleRelTurnLiftUpNoShoot(-105,true)) { // TURN FIX SPDLIMITS
				autoFunc.setSpeedLimit(0.5); // SPEEDLIMIT
				intake.setSpeed(-1);
				step++;
			}
			break;
			
		case (9):
			if (autoFunc.angleRelTurnLiftUpRunnable(105,true, () -> customFunc.outtakeAngle_outtakeSpeed_dropLift(1,-1, 65))) { 
				autoFunc.setSpeedLimit(0.5);
				lift.overrideIntake=true;
				step++;
			}
			break;
			
		} // end switch statement
	}

	
	//SWITCH AUTOS ONLY BELOW HERE
	
	// Middle switch right - scores 2 cubes then lines up exchange
	// uses driveDistanceNoStop
	// angleRelTurnNoShoot, alignIntakeCube
	// all speed limits are manually set. lift-speedLimit is disabled at all times

	// WRITE TUNING UPDATES AT THE COMP HERE.
	private void MiddleSwitch_Right() {
		switch (step) {
		case (0):
			lift.disableSpeedLimit = true;
			autoFunc.setSpeedLimit(1); // SPEEDLIMIT
			lift.setLoc(0.5); // LIFT
			step++;
			break;

		case (1):
			if (autoFunc.driveDistanceNoStop(150, 40,false)) { // DRIVE, TURN RIGHT
				step++;
			}
			break;
		case (2):
			if (autoFunc.driveDistanceNoStop(120, -60,false)) { // DRIVE, TURN LEFT
				Timer.delay(0.15);
				intake.setSpeed(-0.4); // OUTTAKE
				Timer.delay(0.25);
				intake.setSpeed(1);
				lift.setLoc(0); // DROP LIFT
				autoFunc.setSpeedLimit(0.6);
				step++;
			}
			break;
		case (3):
			if (autoFunc.driveDistanceNoStop(-85, -7,false)) { // DRIVE BACK
				intake.setSpeed(1); // INTAKE
				step++;
			}
			break;

		case (4):
			if (autoFunc.angleRelTurnLiftUpNoShoot(-43.5,false)) { // TURN
				step++;
				Timer.delay(0.05);
				intake.setSpeed(1); // INTAKE
				autoFunc.setSpeedLimit(0.5);

			}
			break;
		case (5):
			if (autoFunc.alignIntakeCube(135, 4, false)) {// DRIVE, ALIGN
				step++;
				Timer.delay(0.1);
				intake.setSpeed(1); // INTAKE
				autoFunc.setSpeedLimit(0.3); // SPEEDLIMIT
			}
			break;
		case (6):
			if (autoFunc.alignIntakeCube(20, 4, false)) {
				step++;
				autoFunc.setSpeedLimit(0.6); // SPEEDLIMIT
				intake.setSpeed(0.5); // INTAKE
				lift.setLoc(0.5); // LIFT
			}
			break;
		case (7):
			if (autoFunc.driveDistanceNoStop(-107, 250,false)) { // DRIVE BACK, TURN
				autoFunc.setSpeedLimit(0.65); // SPEEDLIMIT
				step++;
			}
			break;
		case (8):
			if (autoFunc.driveDistanceNoStop(108, 65,false)) { // TURN, DRIVE
				Timer.delay(0.1);
				intake.setSpeed(-0.4); // OUTTAKE
				Timer.delay(0.25);
				intake.setSpeed(0);
				lift.setLoc(0);
				step++;
			}
			break;
		case (9):
			if (autoFunc.driveDistanceNoStop(-84, -7,false)) {
				intake.setSpeed(0.5);
				step++;
			}
			break;
		case (10):
			if (autoFunc.angleRelTurnLiftUpNoShoot(-42,false)) {
				step++;
				Timer.delay(0.05);
				intake.setSpeed(1);
			}
			break;
		case (11):
			if (autoFunc.alignIntakeCube(144, 4, false)) {
				step++;
				Timer.delay(0.05);
				intake.setSpeed(1);
				autoFunc.setSpeedLimit(1);
			}
			break;

		case (12):
			if (autoFunc.driveDistanceNoStop(10, -100,false)) {
				intake.setSpeed(0.6);
				step++;
			}
			break;

		case (13):
			if (autoFunc.driveDistanceNoStop(-50, -100,false)) {
				intake.setSpeed(0.3);
				step++;
			}
			break;

		case (14):
			if (autoFunc.driveDistanceNoStop(100, -89,false)) {
				step++;
			}
			break;

		}
	}

	// Middle switch left
	// 2 CUBES AND THEN PREPARE TO EXCHANGE
	// NOT TESTED ON COMP BOT
	// driveDistanceNoStop, angleRelTurnLiftUpNoShoot, alignIntakeCube
	// all speed limits are manually set. lift-speedLimit is disabled at all times

	// WRITE TUNING UPDATES AT THE COMP HERE.

	// ADJUSTED AFTER MATCH 1

	private void MiddleSwitch_Left() {
		switch (step) {
		case (0):
			lift.disableSpeedLimit = true;
			lift.setLoc(0.5);
			step++;
			break;

		case (1):
			if (autoFunc.driveDistanceNoStop(150, -52,false)) {
				step++;
			}
			break;
		case (2):
			if (autoFunc.driveDistanceNoStop(135, 80,false)) {
				Timer.delay(0.15);
				intake.setSpeed(-0.4);
				Timer.delay(0.25);
				intake.setSpeed(1);
				lift.setLoc(0);
				autoFunc.setSpeedLimit(0.87);
				step++;
			}
			break;
		case (3):
			if (autoFunc.driveDistanceNoStop(-90, 7,false)) {
				lift.overrideIntake=true;
				step++;
				Timer.delay(0.05);
				intake.setSpeed(1);
			}
			break;
		case (4):
			if (autoFunc.angleRelTurnLiftUpNoShoot(45,false)) {
				step++;
				autoFunc.setSpeedLimit(0.7);
			}
			break;
		case (5):
			if (autoFunc.alignIntakeCube(160, 4, false)) {
				step++;
				intake.setSpeed(1);
				autoFunc.setSpeedLimit(0.35);
			}
			break;
		case (6):
			if (autoFunc.alignIntakeCube(20, 4, false)) {
				intake.setSpeed(1);
				step++;
			}
			break;
		case (7):
			step++;
			autoFunc.setSpeedLimit(0.75);
			intake.setSpeed(0.8);
			lift.setLoc(0.5);
			break;
		case (8):
			if (autoFunc.driveDistanceNoStop(-100, -200,false)) {
				step++;
				autoFunc.setSpeedLimit(0.8);
			}
			break;
		case (9):
			if (autoFunc.angleRelTurnLiftUpNoShoot(-40,false)) {
				step++;
			}
			break;
		case (10):// -60
			if (autoFunc.driveDistanceNoStop(150, 0,false)) {
				Timer.delay(0.3);
				intake.setSpeed(-0.3);
				Timer.delay(0.25);
				intake.setSpeed(1);
				lift.setLoc(0);
				autoFunc.setSpeedLimit(0.75);
				step++;
			}
			break;
		case (11):
			if (autoFunc.driveDistanceNoStop(-80, 7,false)) {
				intake.setSpeed(1);
				step++;
			}
			break;
		case (12):
			if (autoFunc.angleRelTurnLiftUpNoShoot(42,false)) {
				step++;
				Timer.delay(0.05);
				intake.setSpeed(1);
				autoFunc.setSpeedLimit(0.65);
			}
			break;
		case (13):
			if (autoFunc.alignIntakeCube(174, 4, false)) {
				step++;
				Timer.delay(0.05);
				intake.setSpeed(1);
				autoFunc.setSpeedLimit(0.35);
			}
			break;
		case (14):
			if (autoFunc.alignIntakeCube(20, 4, false)) {
				intake.setSpeed(0.8);
				step++;
				autoFunc.setSpeedLimit(0.8);
			}
			break;
		case (15):
			if (autoFunc.driveDistanceNoStop(-50, 100,false)) {
				intake.setSpeed(0.3);
				step++;
			}
			break;
		case (16):
			if (autoFunc.driveDistanceNoStop(100, 89,false)) {
				step++;
				lift.overrideIntake=false;
			}
			break;
		}
	}
	
	// baseline auto
		private void baseLine() {
			switch (step) { // turn
			case (0):
				if (autoFunc.driveDistance(325, 0,15,true))
					step++;
				break;
			}
		}
		
		private void stuffs() {
			autoFunc.setSpeedLimit(0.7);
			switch (step) { // turn
			case (0):
				if (autoFunc.driveDistance(400, 0,15,true))
					step++;
				break;
			}
		}
}
		/*
		private void RightStart_singleScaleRight() {
			switch (step) {
			case (0): // SETUP-RIGHT SIDE OF THE ROBOT SHOULD HAVE ONE FOOT OF CLEARANCE FROM SWITCH
				lift.disableSpeedLimit = true; // DISABLE SPEEDLIMIT LIFT OVERRIDE
				lift.setLoc(0.85); // LIFT
				autoFunc.setSpeedLimit(1); // SPEEDLIMIT
				step++;
				break;
			case (1):
				if (autoFunc.driveDistanceNoStop(330, -3)) { // drive until intake overhangs the line, measured at 630cm
					autoFunc.setSpeedLimit(0.4);
					step++;
				}
				break;
			case (2):
				if (autoFunc.driveDistance(300, 0, 15,false)) { // drive until intake overhangs the line, measured at 630cm
					step++;
					autoFunc.setSpeedLimit(0.65);
				}
				break;
			case (3): 
			if (autoFunc.angleRelTurnLiftUpRunnable(-145, () -> customFunc.outtakeAngle_outtakeSpeed_dropLift(-30,-1, -30))) { 
					autoFunc.setSpeedLimit(0.5);
					lift.overrideIntake=true;
					step++;
				}
				break;
			case (4):
				if (autoFunc.driveDistanceNoStop(120,0)) {
					autoFunc.setSpeedLimit(0.3);
					intake.setSpeed(1);
					step++;
				}
				break; 
			case (5):
				if (autoFunc.alignIntakeCube(50, 4)){
					step++;
					//lift.setLoc(1);
					Timer.delay(0.4);
					lift.overrideIntake=false;
				}
			break;
			
		}*/
	//}
//}
//autos from windsor, commented out.
//trajectory test also commented out
/*
	// DOUBLE SCALE LEFT
	// tuned for comp bot (poorly)
	// uses driveDistanceNoStop, driveDistanceNoStop(runnable turnDrop),
	// angleRelTurnLiftUpNoShoot, alignIntakeCube
	// all speed limits are manually set. lift-speedLimit is disabled at all times

	// WRITE TUNING UPDATES AT THE COMP HERE.
	private void Left_RLR() {
		switch (step) {
		case (0):
			lift.zeroEncoder();
			Timer.delay(0.1);
			lift.setLoc(1); // LIFT
			Timer.delay(1.7); // DELAY TO LET THE LIFT GO UP (IMPORTANT)
			intake.setSpeed(0.3); // INTAKE
			lift.disableSpeedLimit = true;
			autoFunc.setSpeedLimit(0.57); // SPEEDLIMIT
			step++;
			break;
		case (1):
			if (autoFunc.driveDistanceNoStop(200, -13.5)) { // DRIVE
				autoFunc.setSpeedLimit(0.85); // SPEEDLIMIT
				step++;
			}
			break;
		case (2):
			if (autoFunc.driveDistanceNoStop(267, 4.3)) { // DRIVE
				step++;
			}
			break;

		case (3):// was 190
			if (autoFunc.driveDistanceNoStop(196, 123, () -> customFunc.turnDrop(55, 52))) { // TURN,SHOOT,DROP
				step++;
				intake.setSpeed(1); // INTAKE
				autoFunc.setSpeedLimit(0.5);// SPEEDLIMIT
			}
			break;
		case (4):
			if (autoFunc.alignIntakeCube(160, 4)) { // DRIVE,ALIGNINTAKE
				intake.setSpeed(1); // INTAKE
				autoFunc.setSpeedLimit(0.37); // SPEEDLIMIT
				step++;
			}
			break;
		case (5):
			if (autoFunc.alignIntakeCube(40, 4)) {// DRIVE SUPER SLOW
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
			if (autoFunc.angleRelTurnLiftUpNoShoot(-98)) { // TURN
				autoFunc.setSpeedLimit(0.5); // SPEEDLIMIT
				step++;
			}
			break;

		case (8):
			if (autoFunc.driveDistanceNoStop(62, -35)) { // DRIVE,TURN
				intake.setSpeed(-0.42); // OUTTAKE
				Timer.delay(0.4);
				intake.setSpeed(0);
				autoFunc.setSpeedLimit(0.4);
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
	// alignIntakeCube (((DOESNT USE AngleRelTurnLiftUpNoShoot
	// all speed limits are manually set. lift-speedLimit is disabled at all times

	// WRITE TUNING UPDATES AT THE COMP HERE.
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
				autoFunc.setSpeedLimit(0.67); // SPEEDLIMIT
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
			if (autoFunc.driveDistance(-15, 0)) { // DRIVE BACK, LIFT
				intake.setSpeed(0.3); // SLOW DOWN INTAKE
				lift.setLoc(1); // LIFT
				autoFunc.setSpeedLimit(0.85); // SPEEDLIMIT
				Timer.delay(0.7); // DELAY TO LET THE LIFT GO UP (IMPORTANT)
				step++;
			}
			break;

		case (6):
			if (autoFunc.driveDistance(15, 0)) { // DRIVE FORWARD, OUTTAKE
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
	// alignIntakeCube DOESNT USE ANGLERELTURNLIFTUPNOSHOOT
	// all speed limits are manually set. lift-speedLimit is disabled at all times

	// WRITE TUNING UPDATES AT THE COMP HERE.
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
				intake.setSpeed(1); // INTAKE
				autoFunc.setSpeedLimit(0.45); // SPEEDLIMIT
			}
			break;
		case (4):
			if (autoFunc.alignIntakeCube(154, 4)) {// DRIVE, ALIGN
				intake.setSpeed(1); // INTAKE
				autoFunc.setSpeedLimit(0.25); // SPEEDLIMIT
				step++;
			}
			break;
		case (5):
			if (autoFunc.alignIntakeCube(18, 4)) {// DRIVE, ALIGN
				autoFunc.setSpeedLimit(0.3); // SPEEDLIMIT
				lift.setLoc(0.6); // LIFT
				intake.setSpeed(1); // INTAKE
				step++;
			}
			break;
		case (6):
			if (autoFunc.driveDistance(-10, 0)) {// DRIVE BACK
				intake.setSpeed(0.7); // INTAKE
				lift.setLoc(0.5); // LIFT
				Timer.delay(1.35); // DELAY TO LET THE LIFT GO UP (IMPORTANT)
				step++;
			}
			break;

		case (7):
			if (autoFunc.driveDistance(35, 0)) {// DRIVE
				intake.setSpeed(-1); // OUTTAKE
				Timer.delay(0.2);
				intake.setSpeed(0);
				step++;
			}
			break;
		// drive backwards TODO so we dont hit intake on switch after auto ends

		}// end switch(step)
	}// end method

	// DOUBLE SCALE RIGHT
	// tuned for practice bot, not tuned for comp bot
	// uses driveDistanceNoStop, driveDistanceNoStop(runnable turnDrop),
	// angleRelTurnLiftUpNoShoot, alignIntakeCube
	// all speed limits are manually set. lift-speedLimit is disabled at all times

	// WRITE TUNING UPDATES AT THE COMP HERE.
	private void Left_LRL() {

		switch (step) {
		case (0): {
			lift.zeroEncoder();
			Timer.delay(0.1);
			lift.setLoc(0.4); // LIFT
			intake.setSpeed(0.45); // INTAKE
			lift.disableSpeedLimit = true;
			autoFunc.setSpeedLimit(1); // SPEEDLIMIT
			step++;
			break;
		}
		case (1):
			if (autoFunc.driveDistanceNoStop(275, 0)) { // DRIVE
				step++;
				autoFunc.setSpeedLimit(0.9); // SPEEDLIMIT
				lift.setLoc(1); // LIFT
			}
			break;
		case (2):
			if (autoFunc.driveDistanceNoStop(570, 90)) { // DRIVE, TURN
				autoFunc.setSpeedLimit(0.5); // SPEEDLIMIT
				step++;
			}
			break;
		case (3):
			if (autoFunc.driveDistanceNoStop(291, -255, () -> customFunc.turnDrop(-122, -145))) { // TURN, SHOOT, DROP
				autoFunc.setSpeedLimit(0.5); // SPEEDLIMIT
				Timer.delay(0.05);
				intake.setSpeed(1); // INTAKE
				Timer.delay(0.05);
				step++;
			}
			break;

		case (4):
			if (autoFunc.alignIntakeCube(150, 4)) { // DRIVE, ALIGN
				Timer.delay(0.1); // DELAY WHILE INTAKE RUNS TO FINISH INTAKING
				step++;
			}
			break;

		case (5):
			if (autoFunc.driveDistance(-15, 0)) { // DRIVE BACK
				intake.setSpeed(0.3); // INTAKE
				lift.setLoc(1); // LIFT
				autoFunc.setSpeedLimit(0.85); // SPEEDLIMIT
				Timer.delay(0.7); // DELAY TO LET THE LIFT GO UP
				step++;
			}
			break;
		case (6):
			if (autoFunc.driveDistanceNoStop(-60, 0)) { // DRIVE BACK
				autoFunc.setSpeedLimit(0.5); // SPEEDLIMIT
				step++;
			}
			break;
		case (7):
			if (autoFunc.angleRelTurnLiftUpNoShoot(123)) { // TURN RIGHT WITHOUT DRIVING
				autoFunc.setSpeedLimit(0.58); // SET SPEED LIMIT
				intake.setSpeed(-1); // OUTTAKE
				step++;
			}
			break;
		// TODO Possibly add a drive backwards if we overhang the scale

		} // end switch(step)
	} // end method

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
		Middle_LRL();
	}

	// Middle switch right
	private void Middle_RRR() {
		Middle_RLR();
	}

	// Middle switch right - scores 2 cubes then lines up exchange
	// uses driveDistanceNoStop
	// angleRelTurnLiftUpNoShoot, alignIntakeCube
	// all speed limits are manually set. lift-speedLimit is disabled at all times

	// WRITE TUNING UPDATES AT THE COMP HERE.
	private void Middle_RLR() {
		switch (step) {
		case (0):
			lift.disableSpeedLimit = true;
			autoFunc.setSpeedLimit(1); // SPEEDLIMIT
			lift.setLoc(0.5); // LIFT
			step++;
			break;

		case (1):
			if (autoFunc.driveDistanceNoStop(150, 40)) { // DRIVE, TURN RIGHT
				step++;
			}
			break;
		case (2):
			if (autoFunc.driveDistanceNoStop(110, -60)) { // DRIVE, TURN LEFT
				Timer.delay(0.15);
				intake.setSpeed(-0.4); // OUTTAKE
				Timer.delay(0.25);
				intake.setSpeed(1);
				lift.setLoc(0); // DROP LIFT
				autoFunc.setSpeedLimit(0.6);
				step++;
			}
			break;
		case (3):
			if (autoFunc.driveDistanceNoStop(-85, -7)) { // DRIVE BACK
				intake.setSpeed(1); // INTAKE
				step++;
			}
			break;

		case (4):
			if (autoFunc.angleRelTurnLiftUpNoShoot(-43.5)) { // TURN
				step++;
				Timer.delay(0.05);
				intake.setSpeed(1); // INTAKE
				autoFunc.setSpeedLimit(0.5);

			}
			break;
		case (5):
			if (autoFunc.alignIntakeCube(132, 4)) {// DRIVE, ALIGN
				step++;
				Timer.delay(0.1);
				intake.setSpeed(1); // INTAKE
				autoFunc.setSpeedLimit(0.3); // SPEEDLIMIT
			}
			break;
		case (6):
			if (autoFunc.alignIntakeCube(20, 4)) {
				step++;
				autoFunc.setSpeedLimit(0.6); // SPEEDLIMIT
				intake.setSpeed(0.5); // INTAKE
				lift.setLoc(0.5); // LIFT
			}
			break;
		case (7):
			if (autoFunc.driveDistanceNoStop(-107, 250)) { // DRIVE BACK, TURN
				autoFunc.setSpeedLimit(0.65); // SPEEDLIMIT
				step++;
			}
			break;
		case (8):
			if (autoFunc.driveDistanceNoStop(108, 65)) { // TURN, DRIVE
				Timer.delay(0.1);
				intake.setSpeed(-0.4); // OUTTAKE
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
	// uses driveDistanceNoStop
	// angleRelTurnLiftUpNoShoot, alignIntakeCube
	// all speed limits are manually set. lift-speedLimit is disabled at all times

	// WRITE TUNING UPDATES AT THE COMP HERE.

	// ADJUSTED AFTER MATCH 1

	private void Middle_LRL() {
		switch (step) {
		case (0):
			lift.disableSpeedLimit = true;
			lift.setLoc(0.5);
			step++;
			break;

		case (1):
			if (autoFunc.driveDistanceNoStop(150, -52)) {
				step++;
			}
			break;
		case (2):
			if (autoFunc.driveDistanceNoStop(125, 80)) {
				Timer.delay(0.15);
				intake.setSpeed(-0.4);
				Timer.delay(0.25);
				intake.setSpeed(1);
				lift.setLoc(0);
				autoFunc.setSpeedLimit(0.87);
				step++;
			}
			break;
		case (3):
			if (autoFunc.driveDistanceNoStop(-90, 7)) {
				step++;
				Timer.delay(0.05);
				intake.setSpeed(1);
			}
			break;

		case (4):
			if (autoFunc.angleRelTurnLiftUpNoShoot(45)) {
				step++;
				autoFunc.setSpeedLimit(0.7);
			}
			break;
		case (5):
			if (autoFunc.alignIntakeCube(160, 4)) {
				step++;
				intake.setSpeed(1);
				autoFunc.setSpeedLimit(0.35);
			}
			break;
		case (6):
			if (autoFunc.alignIntakeCube(20, 4)) {
				intake.setSpeed(1);
				step++;
			}
			break;
		case (7):
			step++;
			autoFunc.setSpeedLimit(0.75);
			intake.setSpeed(0.8);
			lift.setLoc(0.5);
			break;
		case (8):
			if (autoFunc.driveDistanceNoStop(-100, -200)) {
				step++;
				autoFunc.setSpeedLimit(0.8);
			}
			break;

		case (9):
			if (autoFunc.angleRelTurnLiftUpNoShoot(-40)) {
				step++;
			}
			break;
		case (10):// -60
			if (autoFunc.driveDistanceNoStop(150, 0)) {
				Timer.delay(0.3);
				intake.setSpeed(-0.3);
				Timer.delay(0.25);
				intake.setSpeed(1);
				lift.setLoc(0);
				autoFunc.setSpeedLimit(0.75);
				step++;
			}
			break;
		case (11):
			if (autoFunc.driveDistanceNoStop(-80, 7)) {
				intake.setSpeed(1);
				step++;
			}
			break;
		case (12):
			if (autoFunc.angleRelTurnLiftUpNoShoot(42)) {
				step++;
				Timer.delay(0.05);
				intake.setSpeed(1);
				autoFunc.setSpeedLimit(0.65);
			}
			break;
		case (13):
			if (autoFunc.alignIntakeCube(174, 4)) {
				step++;
				Timer.delay(0.05);
				intake.setSpeed(1);
				autoFunc.setSpeedLimit(0.35);
			}
			break;

		case (14):
			if (autoFunc.alignIntakeCube(20, 4)) {
				intake.setSpeed(0.8);
				step++;
				autoFunc.setSpeedLimit(0.8);
			}
			break;

		case (15):
			if (autoFunc.driveDistanceNoStop(-50, 100)) {
				intake.setSpeed(0.3);
				step++;
			}
			break;

		case (16):
			if (autoFunc.driveDistanceNoStop(100, 89)) {
				step++;
			}
			break;

		}

	}

	// left scale redirect
	private void None_RLR() {
		None_LLL();
	}
	
	// trajectory test
	private void None_LLL() {
		switch (step) {
		case (0):
			trajA = new TrajectoryAuto("doubleScaleLeft");
			step++;
			break;
		}
		/*
		case(1):
			if (trajA.actionReady())
				lift.setLoc(0.5);
		break;
		}
		
			
	}

	// right
	private void None_LRL() { // TUNING STEP 2: DRIVE AND TURN 150 degrees with lift at 0.4 and 0.8
		switch (step) { // setup
		case (0):
			step++;
			break;
		}
	}

	// right
	private void None_RRR() {
		None_LRL();
	}
}
*/