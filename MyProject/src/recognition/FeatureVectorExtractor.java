package recognition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;
import app.Config;
import processing.core.PVector;
import util.Direction;

public class FeatureVectorExtractor {
	
	private boolean isTracking = false;
	private boolean isLocked = false;
	private List<ObservationDiscrete<Direction>> featureVector;
	private Timer timer;
	
	
	public List<ObservationDiscrete<Direction>> testVector 
		= new ArrayList<ObservationDiscrete<Direction>>(Arrays.asList(
				Direction.C.observation(),Direction.CW.observation(),Direction.CNW.observation(),Direction.O.observation(),
				Direction.O.observation()
				));
	
	
	
	public boolean isTracking(){
		
		return isTracking;
	}
	
	public boolean isLocked(){
		
		return isLocked;
	}
	
	public void startTracking(){
		
		featureVector = new ArrayList<ObservationDiscrete<Direction>>();
		
		// lock tracking
		timer = new Timer();  //At this line a new Thread will be created
		isLocked = true;
	    timer.schedule(new LockRecognitionTask(), Config.MIN_GESTURE_TIME); //delay in milliseconds
	      
		isTracking = true;
		System.out.println("Started tracking");
	}
	
	public void addFeature(Direction d){
		
		featureVector.add(d.observation());
		//System.out.println("Added vector: "+d);

	}
	
	public void stopTracking(){
		
		removeVectorBegining();
		removeVectorEnd();
		isTracking = false;
		System.out.println("Stopped tracking");
		printFeatureVector();
		//Collections.reverse(featureVector);
		//printFeatureVector();
	}
	
	public List<ObservationDiscrete<Direction>> getFeatureVector(){
		return featureVector;
	}
	
	public boolean checkFeatureVectorFinish(){
		
		boolean endVector = true;
		
		for (int i = featureVector.size()-1; i > featureVector.size()-Config.NO_OF_IDLE_FEATURES_BEFORE_END-1; i--) {
			if (!(featureVector.get(i).value == Direction.O ))
				endVector = false;
		}
		
		return endVector;
	}
	
	private void removeVectorEnd(){
		
		int size = featureVector.size();
		
		for (int i = size-1; i > size-Config.NO_OF_IDLE_FEATURES_BEFORE_END-1; i--) {
			System.out.println(i);
			featureVector.remove(i);
		}
	}
	
	private void removeVectorBegining(){
		
		for (int i = 0; i < Config.NO_OF_DELETED_FEATURES_AT_BEGINING; i++) {
			featureVector.remove(0);
		}
	}
	
	public void printFeatureVector(){
		
		System.out.println(featureVector);
	}
	
	public void printTestVector(){
		
		System.out.println(testVector);
	}
	
	
	// Gesture controll task
	private class LockRecognitionTask extends TimerTask {

        @Override
        public void run() {
        	
        	isLocked = false;
	        //timer.cancel(); //Not necessary because we call System.exit
        }
	}
	
	public static Direction findDirection(PVector diffVector){
		
		if ( diffVector.x < Config.MIN_DIRECTION_DISTANCE && diffVector.x > -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y < Config.MIN_DIRECTION_DISTANCE && diffVector.y > -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z < Config.MIN_DIRECTION_DISTANCE && diffVector.z > -Config.MIN_DIRECTION_DISTANCE)
			return Direction.O;
		else if ( diffVector.x > Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y < Config.MIN_DIRECTION_DISTANCE && diffVector.y > -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z < Config.MIN_DIRECTION_DISTANCE && diffVector.z > -Config.MIN_DIRECTION_DISTANCE)
			return Direction.E;
		else if ( diffVector.x > Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y > Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z < Config.MIN_DIRECTION_DISTANCE && diffVector.z > -Config.MIN_DIRECTION_DISTANCE)
			return Direction.NE;
		else if ( diffVector.x < Config.MIN_DIRECTION_DISTANCE && diffVector.x > -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y > Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z < Config.MIN_DIRECTION_DISTANCE && diffVector.z > -Config.MIN_DIRECTION_DISTANCE)
			return Direction.N;
		else if ( diffVector.x < -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y > Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z < Config.MIN_DIRECTION_DISTANCE && diffVector.z > -Config.MIN_DIRECTION_DISTANCE)
			return Direction.NW;
		else if ( diffVector.x < -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y < Config.MIN_DIRECTION_DISTANCE && diffVector.y > -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z < Config.MIN_DIRECTION_DISTANCE && diffVector.z > -Config.MIN_DIRECTION_DISTANCE)
			return Direction.W;
		else if ( diffVector.x < -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y < -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z < Config.MIN_DIRECTION_DISTANCE && diffVector.z > -Config.MIN_DIRECTION_DISTANCE)
			return Direction.SW;
		else if ( diffVector.x < Config.MIN_DIRECTION_DISTANCE && diffVector.x > -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y < -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z < Config.MIN_DIRECTION_DISTANCE && diffVector.z > -Config.MIN_DIRECTION_DISTANCE)
			return Direction.S;
		else if ( diffVector.x > Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y < -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z < Config.MIN_DIRECTION_DISTANCE && diffVector.z > -Config.MIN_DIRECTION_DISTANCE)
			return Direction.SE;
		
		else if ( diffVector.x > Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y < Config.MIN_DIRECTION_DISTANCE && diffVector.y > -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z < -Config.MIN_DIRECTION_DISTANCE)
			return Direction.CE;
		else if ( diffVector.x > Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y > Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z < -Config.MIN_DIRECTION_DISTANCE)
			return Direction.CNE;
		else if ( diffVector.x < Config.MIN_DIRECTION_DISTANCE && diffVector.x > -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y > Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z < -Config.MIN_DIRECTION_DISTANCE)
			return Direction.CN;
		else if ( diffVector.x < -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y > Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z < -Config.MIN_DIRECTION_DISTANCE)
			return Direction.CNW;
		else if ( diffVector.x < -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y < Config.MIN_DIRECTION_DISTANCE && diffVector.y > -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z < -Config.MIN_DIRECTION_DISTANCE)
			return Direction.CW;
		else if ( diffVector.x < -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y < -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z < -Config.MIN_DIRECTION_DISTANCE)
			return Direction.CSW;
		else if ( diffVector.x < Config.MIN_DIRECTION_DISTANCE && diffVector.x > -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y < -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z < -Config.MIN_DIRECTION_DISTANCE)
			return Direction.CS;
		else if ( diffVector.x > Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y < -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z < -Config.MIN_DIRECTION_DISTANCE)
			return Direction.CSE;
		
		else if ( diffVector.x > Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y < Config.MIN_DIRECTION_DISTANCE && diffVector.y > -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z > Config.MIN_DIRECTION_DISTANCE)
			return Direction.FE;
		else if ( diffVector.x > Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y > Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z > Config.MIN_DIRECTION_DISTANCE)
			return Direction.FNE;
		else if ( diffVector.x < Config.MIN_DIRECTION_DISTANCE && diffVector.x > -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y > Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z > Config.MIN_DIRECTION_DISTANCE)
			return Direction.FN;
		else if ( diffVector.x < -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y > Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z > Config.MIN_DIRECTION_DISTANCE)
			return Direction.FNW;
		else if ( diffVector.x < -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y < Config.MIN_DIRECTION_DISTANCE && diffVector.y > -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z > Config.MIN_DIRECTION_DISTANCE)
			return Direction.FW;
		else if ( diffVector.x < -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y < -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z > Config.MIN_DIRECTION_DISTANCE)
			return Direction.FSW;
		else if ( diffVector.x < Config.MIN_DIRECTION_DISTANCE && diffVector.x > -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y < -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z > Config.MIN_DIRECTION_DISTANCE)
			return Direction.FS;
		else if ( diffVector.x > Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y < -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z > Config.MIN_DIRECTION_DISTANCE)
			return Direction.FSE;
		
		else if ( diffVector.x < Config.MIN_DIRECTION_DISTANCE && diffVector.x > -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y < Config.MIN_DIRECTION_DISTANCE && diffVector.y > -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z > Config.MIN_DIRECTION_DISTANCE)
			return Direction.F;
		else if ( diffVector.x < Config.MIN_DIRECTION_DISTANCE && diffVector.x > -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.y < Config.MIN_DIRECTION_DISTANCE && diffVector.y > -Config.MIN_DIRECTION_DISTANCE
				&& diffVector.z < -Config.MIN_DIRECTION_DISTANCE)
			return Direction.C;
		else
			return null;
	}
	
	public static void main(String[] args) {
		
		System.out.println(FeatureVectorExtractor.findDirection(new PVector(-700,-600,500)));
		
		
		FeatureVectorExtractor e = new  FeatureVectorExtractor();
		e.printTestVector();
		System.out.println(e.checkFeatureVectorFinish());
		e.removeVectorEnd();
		e.printTestVector();
	}

}
