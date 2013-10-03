package app;


public class Config {
	
	
	
	/**
	 * Feature vector extraction
	 */
	public static final int VECTOR_EXTRACTION_SPEED = 400; // [ms]
	public static final int VECTOR_EXTRACTION_DELAY = 5*1000; // [ms]
	
	public static final int MIN_DIRECTION_DISTANCE = 120; // [px?]
	
	
	public static final int MIN_GESTURE_TIME = 2*1000; // [ms]
	public static final int MAX_GESTURE_TIME = 20*1000; // [ms]
	
	public static final int NO_OF_IDLE_FEATURES_BEFORE_END = 1; // [no]
	public static final int NO_OF_DELETED_FEATURES_AT_BEGINING = 1;
	
	/**
	 * Gesture recognition
	 */
	public static final int INITIATION_JOINT_DISTANCE = 400; // [px]
	public static final double MIN_SYMBOL_PROB = 0;
	public static final double MIN_FEATURE_PROB = 0.001;
	
	public static final int DISPLAY_RECOGNIZED = 4*1000; // [ms]
	
	public static final double MINIMAL_OPDF_VALUE = 0.003;
	
	/**
	 * Pointing objects
	 */
	public static final double POINTING_THRESHOLD = 0.91;
	public static final double MAXIMAL_POINTING_DISTANCE_ERROR = 600;
	public static final int DISPLAY_POINTED = 8*100; // [ms]
	public static final int CHECK_IF_POINTED = 8*100; // [ms]
	public static final int LOCK_POINTING_AGAIN = 2*1000;
	
	/**
	 * Debug
	 */
	public static final boolean DEBUG_MODE = true;
	
	
	

}
