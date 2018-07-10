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

public class AutonomousBaseBackup {
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
//PINS: 
	//ALL THE WAY TO THE RIGHT = NONE = BASELINE
	//ONE CLICK AWAY = LEFT = DOUBLE SCALE
	//TWO CLICKS AWAY = MIDDLE = 2SWITCH 1EXCHANGE
	//THREE CLICKS AWAY = RIGHT = LITERALLY NO AUTO

//METHODS:
	//baseLine	: Baseline
	//switchLeft	: straight line only
	//RADIUS_LeftStart_doubleScaleRight double scale right, uses, new curves and radius curve. consistent on prac bot	
	//LeftStart_singleScaleRight()	: DEPRECATED single scale, uses mostly new curves and one old curve. perfectly consistent
	//LeftStart_singleScaleLeft()	: single scale, uses all new curves	: perfectly consistent
	//LeftStart_doubleScaleRight()	: DEPRECATED double scale, mostly new curves one old curve. second cube inconsistent due to curve
	//LeftStart_doubleScaleLeft()	: double scale, all new curves. quite consistent, last cube missed once
	//stuffs()	: literally just drives 4 meters

		if (pin == 0) { // None
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
				V2_LeftStart_doubleScaleLeft();
			} else if (gameData.equals("LRL")) {
				LeftStart_doubleScaleRight();
			} else if (gameData.equals("RLR")) {
				V2_LeftStart_doubleScaleLeft();
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
			//System.out.println("pin 3 active :Right:");
			if (gameData.equals("RRR")) {
			} else if (gameData.equals("LLL")) {
			} else if (gameData.equals("LRL")) {
			} else if (gameData.equals("RLR")) {
			}
		}
		
		//stuffs();
	
	}

	/*STUFF TO COPY AND PASTE TO MAKE AUTO METHODS QUICKLY
	  
	case ():
		if () {
			step++;
		}
	break;

	COPY AND PASTE METHOD

	private void (){
		switch(step)
		case ():
		if () {
			step++;
		}
	break;
	}

	*/
	
	private void radiusCurveTest() {
		switch (step) {
		case (0):
			intake.setSpeed(0.5);
			break;
		}// end switch
	}// end method
	
	//DOUBLE SCALE RIGHT
		private void RADIUS_LeftStart_doubleScaleRight() {
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
				if (autoFunc.driveDistanceNoStop(346, 0,true)) { // DRIVE
					step++;
					autoFunc.setSpeedLimit(0.7); // SPEEDLIMIT
					lift.setLoc(0.5); // LIFT
				}
				break;
			case (2):
				if (autoFunc.driveDistanceNoStop(457, 87,true)) { // DRIVE, TURN
					autoFunc.setSpeedLimit(0.43); // DONT TOUCH THIS SPEEDLIMIT, IT EFFEFCTS THE RADIUS OF THE CURVE
					lift.setLoc(1); // LIFT
					step++;
				}
				break;
			case (3):
				if (autoFunc.driveDistanceNoStop(150, 0,true)) { 
					autoFunc.setSpeedLimit(0.55); // DONT TOUCH THIS SPEEDLIMIT, IT EFFEFCTS THE RADIUS OF THE CURVE
					step++;
				}
				break;
			case (4):
				if (autoFunc.radiusCurve(0,1.1,96,'L',false)) {
					System.out.println("donezo");
					autoFunc.setSpeedLimit(0.4);
					step++;
				}
				break;
			case(5):
				if (autoFunc.driveDistance(24,0,10,true)) {
					step++;
					lift.overrideIntake=true;
					intake.setSpeed(-0.6);
					lift.setLoc(0);
					autoFunc.setSpeedLimit(0.24);
				}
			break;
			
			case (6):
				if (autoFunc.driveDistanceNoStop(-47,0,true)) {
					step++;
					autoFunc.setSpeedLimit(0.7);
					intake.setSpeed(0.3);
				}
				break;
			case (7):
				if (autoFunc.angleRelTurnLiftUpNoShoot(-129,true)) { // TURN RIGHT WITHOUT DRIVING
					step++;
					intake.setSpeed(1);
					autoFunc.setSpeedLimit(0.5);;

				}
				break;
			case (8): //!!!remove 40 CM from this (DONE ALREADY) (so change the 158 to 118) !!!
				if (autoFunc.driveDistance(90, 0, 15, true)) { // DRIVE, ALIGN
					intake.setSpeed(0.6);
					lift.setLoc(1);
					step++;
					autoFunc.setSpeedLimit(0.9); //Out of all the speedlimits in this auto, really do not touch this one at comp. very well might change the shot angle a little
				}
				break;

			case (9):
				if (autoFunc.driveDistanceNoStop(-50, -40,true)) { // DRIVE BACK
					intake.setSpeed(0.3); // INTAKE
					autoFunc.setSpeedLimit(0.8); // SPEEDLIMIT
					Timer.delay(0.4); // DELAY TO LET THE LIFT GO UP
					step++;
				}
				break;
			case (10):
				if (autoFunc.angleRelTurnLiftUpNoShoot(120,true)) { // TURN RIGHT WITHOUT DRIVING
					autoFunc.setSpeedLimit(0.58); // SET SPEED LIMIT
					step++;
					lift.overrideIntake=false;
				}
				break;
			case (11): //!!!remove 40 CM from this (DONE ALREADY) (so change the 158 to 118) !!!
				if (autoFunc.driveDistance(55, 0, 15, true)) { // DRIVE, ALIGN
					intake.setSpeed(-0.6);
					step++;
					lift.setLoc(0);
				}
				break;
			case(12):
				if (autoFunc.driveDistance(-25,0,15, true)) {
					intake.setSpeed(0.3);
					lift.setLoc(0);
					lift.overrideIntake=false;
				}
			// TODO Possibly add a drive backwards if we overhang the scale

			} // end switch(step)
		} // end method
		//DOUBLE SCALE LEFT - SECOND VERSION WRITTEN BY KAELAN BEFORE CHAMPS TO WORK ON 2.5 or 3 CUBES
		private void V2_LeftStart_doubleScaleLeft() {
			switch (step) {
			case (0): // SETUP-RIGHT SIDE OF THE ROBOT SHOULD HAVE ONE FOOT OF CLEARANCE FROM SWITCH
				lift.disableSpeedLimit = true; // DISABLE SPEEDLIMIT LIFT OVERRIDE
				lift.setLoc(1); // LIFT
				autoFunc.setSpeedLimit(1); // SPEEDLIMIT
				step++;
				break;
			case (1):
				if (autoFunc.driveDistanceNoStop(330, 0, true)) { // drive until intake overhangs the line, measured at 630cm
					autoFunc.setSpeedLimit(0.5);
					step++;
				}
				break;
			case (2):
				if (autoFunc.driveDistance(327.5, 22, 15,true)) { //angle changed from 15 to 18
					step++;
					autoFunc.setSpeedLimit(0.7);
				}
				break;
			case (3): 
				lift.overrideIntake=true;
				intake.setSpeed(-0.6);
				lift.setLoc(0);
				Timer.delay(0.4);
				step++;
			case (4):
				if (autoFunc.angleRelTurnLiftUpNoShoot(141,true)){
					step++;
					intake.setSpeed(0.3);
				}
				break;
			
			case (5):
				if (autoFunc.driveDistanceNoStop(110,0,true)) {
					autoFunc.setSpeedLimit(0.5);
					intake.setSpeed(1);
					step++;
				}
				break; 
				
			case (6):
				if (autoFunc.alignIntakeCube(42, 4, true)){
					step++;
					lift.setLoc(1);
					autoFunc.setSpeedLimit(0.8);
				}
				break;

			case (7):
				if (autoFunc.driveDistanceNoStop(-70,0, true)) { // DRIVE BACKWARDS
					autoFunc.setSpeedLimit(0.75); // SPEEDLIMIT
					step++;
					intake.setSpeed(0.35);
				}
				break;
			case (8):
				if (autoFunc.angleRelTurnLiftUpNoShoot(-125,true)) { // TURN FIX SPDLIMITS
					autoFunc.setSpeedLimit(0.5); // SPEEDLIMIT
					step++;
				}
				break;
			case (9):
				if (autoFunc.driveDistance(30,0,15, true)) { // DRIVE BACKWARDS
					autoFunc.setSpeedLimit(0.5); // SPEEDLIMIT
					step++;
					intake.setSpeed(-0.6);
					lift.setLoc(0);
				}
				break;
			case (10):
				if (autoFunc.angleRelTurnLiftUpNoShoot(101.5,true)) { 
					autoFunc.setSpeedLimit(0.9);
					intake.setSpeed(0.3);
					step++;
				}
				break;
			case (11):
				if (autoFunc.driveDistanceNoStop(148,0,true)) {
					autoFunc.setSpeedLimit(0.5);
					intake.setSpeed(1);
					step++;
				}
				break; 
			case (12):
				if (autoFunc.alignIntakeCube(42, 4, true)){
					step++;
					autoFunc.setSpeedLimit(0.8);
					lift.setLoc(0.8);
				}
			break;
			case(13):
				if (autoFunc.driveDistanceNoStop(-130,0,true)){
					intake.setSpeed(0.3);
					step++;
				}
			break;
			
			} // end switch statement
		}
	// switch left auto - one cube out of the way of teammates
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
	
	
	//--------------------------------------------------------------WE DONT USE THIS AUTO, WE USE THE RADIUS VERSION
	
	//DOUBLE SCALE RIGHT
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
				lift.setLoc(0.11); // LIFT
			}
			break;
		case (2):
			if (autoFunc.driveDistanceNoStop(250, 90,true)) { // DRIVE, TURN
				autoFunc.setSpeedLimit(0.6); // DONT TOUCH THIS SPEEDLIMIT, IT EFFEFCTS THE RADIUS OF THE CURVE
				//lift.setLoc(1); // LIFT
				step++;
			}
			break;
		/*
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
			*/
		// TODO Possibly add a drive backwards if we overhang the scale
		} // end switch(step)
	} // end method
	
	
	
	//DOUBLE SCALE LEFT
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
	
	//SINGLE SCALE RIGHT
		private void LeftStart_singleScaleRight() {
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
			} // end switch(step)
		} // end method
		
		//SINGLE SCALE LEFT
		private void LeftStart_singleScaleLeft() {
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
				if (autoFunc.driveDistance(300,0,15,true))
					step++;
				break;
			}
		}
}