package ca.warp7.robot;


//TODO Update Constants!
public class Constants {
	//TODO Update Constants!
	// PWM Pins
	//public static final int[] RIGHT_DRIVE_MOTOR_IDS = { 1, 5 };
	//public static final int[] LEFT_DRIVE_MOTOR_IDS = { 9, 2 };
	public static final int[] RIGHT_DRIVE_MOTOR_IDS = { 1,3 };
	public static final int[] LEFT_DRIVE_MOTOR_IDS = { 2,8 };
	public static final int[] INTAKE_MOTOR_PINS = { 6 };
	public static final int[] ELEVATOR_MOTOR_PINS = { 7 };
	public static final int[] CLIMBER_MOTOR_PINS = { 4, 5 };

	//TODO Update Constants!
	// DIG Pins
	public static final int LEFT_DRIVE_ENCODER_A = 0;
	public static final int LEFT_DRIVE_ENCODER_B = 1;
	public static final int RIGHT_DRIVE_ENCODER_A = 2;
	public static final int RIGHT_DRIVE_ENCODER_B = 3;
	public static final int PHOTOSENSOR_PIN = 9;
	
	//TODO Update Constants!
	// Solenoids (manifold ports)
	public static final int DRIVE_SHIFTER_PORT = 1;
	public static final int INTAKE_PISTONS = 0;
	
	//TODO Update Constants!
	// Compressor
	public static final int COMPRESSOR_PIN = 0;

	//TODO Update Constants!
	// CAN ID's
	/**
	public static final int SHOOTER_SLAVE_ID = 0;
	public stati
	c final int SHOOTER_MASTER_ID = 1;
	**/
	
	//TODO Update Constants!
	// Remote ID's
	public static final int DRIVER_ID = 0;
	public static final int OPERATOR_ID = 1;
	
	//TODO Update Constants!
	// Robot dimensions and stuff
	public static double WHEEL_DIAMETER = 6; // inches
	public static double WHEEL_CIRCUMFERENCE = Math.PI * WHEEL_DIAMETER;
	public static int DRIVE_TICKS_PER_REV = 256; //256
	public static double DRIVE_INCHES_PER_TICK = WHEEL_CIRCUMFERENCE / DRIVE_TICKS_PER_REV;
	public static double pixelOffset = -35;

    public static String ATTR_EX_MODE = "CameraAttributes::Exposure::Mode";
    public static String ATTR_EX_VALUE = "CameraAttributes::Exposure::Value";

    public static String ATTR_BR_MODE = "CameraAttributes::Brightness::Mode";
    public static String ATTR_BR_VALUE = "CameraAttributes::Brightness::Value";
    
    public static double RIGHT_DRIFT_OFFSET = 0.9961154656261869;
    public static double LEFT_DRIFT_OFFSET = 0.9717861474194853;
}
