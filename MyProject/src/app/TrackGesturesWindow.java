package app;

import geometry.Line3D;
import geometry.Point3D;

import java.awt.Container;
import java.awt.Frame;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lcn.LCNController;
import lcn.LCNObject;

import org.omg.CosNaming.IstringHelper;

import debug.Debug;

import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;
import processing.opengl.*;

import SimpleOpenNI.SimpleOpenNI;
import SimpleOpenNI.Vector3D;
import processing.core.PApplet;
import processing.core.PMatrix3D;
import processing.core.PVector;
import recognition.FeatureVectorExtractor;
import recognition.GestureRecognizer;
import recognition.Symbol;
import util.Direction;

public class TrackGesturesWindow extends PApplet {
	
	/**
	 * 
	 */
	
	// Constants
	
	final static int TRACKING_DELAY = Config.VECTOR_EXTRACTION_DELAY;
	final static int TRACKING_PERIOD = Config.VECTOR_EXTRACTION_SPEED;
	
	//
	
	private static final long serialVersionUID = -4286660469648015609L;
	
	final static int IDLE     = -1;
	
	final static int CALIB_START     = 0;
	final static int CALIB_NULLPOINT = 1;
	final static int CALIB_X_POINT   = 2;
	final static int CALIB_Z_POINT   = 3;
	final static int CALIB_DONE      = 4;
	
	final static int LEARN_GESTURE     = 5;

	SimpleOpenNI  context;
	boolean       screenFlag = true;
	int           calibMode = IDLE;//CALIB_START;
	boolean       autoCalib=true;


	PVector   nullPoint3d = new PVector();
	PVector   xDirPoint3d = new PVector();
	PVector   zDirPoint3d = new PVector();
	PVector   tempVec1 = new PVector();
	PVector   tempVec2 = new PVector();
	PVector   tempVec3 = new PVector();

	PMatrix3D   userCoordsysMat = new PMatrix3D();
	
	PVector previousHand = new PVector();
	PVector projHand = new PVector();
	
	PVector shoulder = new PVector();
	PVector projShoulder = new PVector();
	
	PVector ball = new PVector();
	PVector projBall = new PVector();
	
	int resX = 640;
	int resY = 480;
	
	// test values
	PVector testPoint = new PVector(resX/2,resY/2,-50);
	
	String typing = "";
	String saved = "";
	
	
	Timer timer;
	private boolean taskScheduled = false;
	
	FeatureVectorExtractor extractor;
	GestureRecognizer recognizer;
	LCNController controller;
	
	// recognized gestures
	Symbol recognizedSymbol;
	boolean displayRecognized = false;
	
	// pointing
	boolean isPointing = false;
	private boolean displayPointed = false;
	LCNObject pointedObj;
	LCNObject previouslyPointedObj;
	LCNObject affectedObj;
	
	
	//training
	List<ObservationDiscrete<Direction>> addedSequence = new ArrayList<ObservationDiscrete<Direction>>();
	Symbol addedSymbol;
	private List<List<ObservationDiscrete<Direction>>> sequences;
	int states = 2;
	

	public void setup()
	{  
		
	  size(1280, 480,P3D);
	  frame = findFrame();
	  frame.setLocation(50, 50);
	  
	  smooth();
	  
	  
	
	  //context = new SimpleOpenNI(this);
	  context = new SimpleOpenNI(this,SimpleOpenNI.RUN_MODE_MULTI_THREADED);
	
	  context.setMirror(true);
	
	  // enable depthMap generation 
	  if (context.enableDepth() == false)
	  {
	    println("Can't open the depthMap, maybe the camera is not connected!"); 
	    exit();
	    return;
	  }
	
	  if (context.enableRGB() == false)
	  {
	    println("Can't open the rgbMap, maybe the camera is not connected or there is no rgbSensor!"); 
	    exit();
	    return;
	  }
	  
	// enable skeleton generation for all joints
	  context.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
	
	  // align depth data to image data
	  context.alternativeViewPointDepthToImage();
	
	  // Create the font
	  textFont(createFont("Georgia", 18));
	  
	  extractor = new FeatureVectorExtractor();
	  recognizer =  new GestureRecognizer();
	  controller = new LCNController();
	  
	  controller.connectToPCHK();
	  
	  timer = new Timer();  //At this line a new Thread will be created
      timer.scheduleAtFixedRate(new GesturePositionTask(), TRACKING_DELAY, TRACKING_PERIOD); //delay in milliseconds
      
      
	}
	
	public Frame findFrame() {		
	      Container f = this.getParent();
	      while (!(f instanceof Frame) && f!=null)
	            f = f.getParent();
	      return (Frame) f;
	}

	public void draw()
	{  
		
		
		// update the cam
		context.update();

		if (screenFlag)
			image(context.rgbImage(), 0, 0);
		else
			image(context.depthImage(), 0, 0);
		
		// draw text background
		pushStyle();
		noStroke();
		fill(0,200,0,100);
		rect(0,0,640,40);
		// technical screen
		fill(120,120,120,255);
		rect(640,0,640,480);
		// technical screen text rectangle
		fill(0,100,100,100);
		rect(640,0,640,40);
		popStyle();

		// draw the skeleton if it's available
		int[] userList = context.getUsers();
		for(int i=0;i<userList.length;i++)
		{
			if(context.isTrackingSkeleton(userList[i])){
				// draw skeleton
				drawSkeleton(userList[i]);
				// draw ball
				drawBall(userList[i]);
				// draw pointing line
				drawPointingLine(userList[i]);
			}
				
		}
		
		drawRecognizedSymbol();
		drawTest();
		
		// common options
		if ( extractor.isTracking() ){
			text("Tracking...", resX+5, resY-36);
			//drawBall();
		}
		
		// mode options
	  switch(calibMode)
	  {
	  	case IDLE:
			text("Welcome in SmartHouse", 5, 30);
			text("IDLE", 645, 30);
			break;
	  	case LEARN_GESTURE:
			text("Type new gesture name and press enter:\n"+typing, 5, 30);
			text("LEARN_GESTURE", 645, 30);
			break;
		case CALIB_START:
			text("Training new gesture, press 'a' to approve sequence, 'f' for finish", 5, 30);
			text(addedSequence.toString(), 645, 30);
			break;
		case CALIB_NULLPOINT:
			text("Set the nullpoint with the left mousebutton", 5, 30);
			break;
		case CALIB_X_POINT:
			text("Set the x-axis with the left mousebutton", 5, 30);
			break;
		case CALIB_Z_POINT:
			text("Set the z-axis with the left mousebutton", 5, 30);
			break;
		case CALIB_DONE:
			text("New nullpoint is defined!", 5, 30);
			break;
	  }
	  
	  

	  // draw 
	  drawCalibPoint();

	  // draw the user defined coordinate system
	  //  with the size of  500mm
	  if (context.hasUserCoordsys())
	  {
	    PVector temp = new PVector();
	    PVector nullPoint = new PVector();

	    pushStyle();

	    strokeWeight(3);
	    noFill();        

	    context.convertRealWorldToProjective(new PVector(0, 0, 0), tempVec1);  
	    stroke(255, 255, 255, 150);
	    ellipse(tempVec1.x, tempVec1.y, 10, 10); 

	    context.convertRealWorldToProjective(new PVector(500, 0, 0), tempVec2);        
	    stroke(255, 0, 0, 150);
	    line(tempVec1.x, tempVec1.y, 
	    tempVec2.x, tempVec2.y); 

	    context.convertRealWorldToProjective(new PVector(0, 500, 0), tempVec2);        
	    stroke(0, 255, 0, 150);
	    line(tempVec1.x, tempVec1.y, 
	    tempVec2.x, tempVec2.y); 

	    context.convertRealWorldToProjective(new PVector(0, 0, 500), tempVec2);        
	    stroke(0, 0, 255, 150);
	    line(tempVec1.x, tempVec1.y, 
	    tempVec2.x, tempVec2.y); 

	    popStyle();
	  }
	  
	}
	
	private void drawPointingLine(int userId) {
		
		findPointedObject(userId);
		
		if ( isPointing ){
			
			context.convertRealWorldToProjective(shoulder, projShoulder);
			context.convertRealWorldToProjective(previousHand, projHand);
/*			System.out.println(projShoulder);
			System.out.println(projHand);*/
			
			pushStyle();
			//pushMatrix();
			strokeWeight(2);
			stroke(0, 255, 0);
			//line(projShoulder.x+resX, projShoulder.y, 30, projHand.x+resX, projHand.y, 30);
			line(projShoulder.x+resX, projShoulder.y, projShoulder.z, projHand.x+resX, projHand.y, projShoulder.z);
			
			if ( Config.DEBUG_MODE ){
				for (int i = 0; i < Debug.debugs.size(); i++) {
					text(Debug.debugs.get(i).toString(), resX+100, 300+i*30);
				}
				text(Debug.txt, 100, 350);
			}
			
			//popMatrix();
			popStyle();
		}
		
	}
	
	private void drawTest(){
		
		if ( !isPointing )
			pointedObj = null;
		
		if( pointedObj != null ){
			pushStyle();
			textFont(createFont("Georgia", 26));
			text(pointedObj.toString(), resX+50, 250);
			popStyle();
		}
		
		/*// test ball
		pushStyle();
		pushMatrix();
		noStroke();
		fill(0, 255, 0);
		lights();
		translate(testPoint.x, testPoint.y , testPoint.z++);
		//System.out.println(testPoint);
		sphere(20);
		popMatrix();
		popStyle();*/
	}

	private void drawBall(int userId) {
		
		
		// convert real world point to projective s pace
   	  /*PVector handProj = new PVector(); 
   	  context.convertRealWorldToProjective(previousHand,handProj);
   	  System.out.println("Right hand position: " + handProj.x + "," + handProj.y + "," + handProj.z);*/
		
		//for(int i=0;i<userList.length;i++)
		//{
		//	if(context.isTrackingSkeleton(userList[i]))
				context.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_RIGHT_HAND,ball);
		//}
		//System.out.println("Real hand position: " + ball.x + "," + ball.y);
		// convert real world point to projective s pace
		context.convertRealWorldToProjective(ball,projBall);
		//System.out.println("Right hand position: " + projBall.x + "," + projBall.y);	
		
		if ( extractor.isTracking() ){
		
			pushStyle();
			pushMatrix();
			noStroke();
			fill(255, 100, 100);
			lights();
			translate(resX+projBall.x, projBall.y , 30);
			sphere(20);
			popMatrix();
			popStyle();
			
		} else{
			pushStyle();
			pushMatrix();
			noStroke();
			lights();
			translate(resX+projBall.x, projBall.y , 30);
			sphere(20);
			popMatrix();
			popStyle();
		}
		
	}
	
	private void drawRecognizedSymbol(){
		if ( displayRecognized ){
			pushStyle();
			textFont(createFont("Georgia", 28));
			text(recognizedSymbol.name, resX+100 , 100);
			popStyle();
		}
		if ( displayPointed ){
			pushStyle();
			textFont(createFont("Georgia", 28));
			if ( affectedObj != null )
				text(affectedObj.toString(), resX+100 , 120);
			popStyle();
		}
	}

		// draw the skeleton with the selected joints
		public void drawSkeleton(int userId)
		{
		  // to get the 3d joint data
		  /*
		  PVector jointPos = new PVector();
		  context.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_NECK,jointPos);
		  println(jointPos);
		  */
			
			strokeWeight(3);
			stroke(255, 0, 0);
		  
		  context.drawLimb(userId, SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK);

		  context.drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_LEFT_SHOULDER);
		  context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_LEFT_ELBOW);
		  context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND);

		  context.drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_RIGHT_SHOULDER);
		  context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_ELBOW);
		  context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW, SimpleOpenNI.SKEL_RIGHT_HAND);

		  context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_TORSO);
		  context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_TORSO);

		  context.drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_HIP);
		  context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_HIP, SimpleOpenNI.SKEL_LEFT_KNEE);
		  context.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_KNEE, SimpleOpenNI.SKEL_LEFT_FOOT);

		  context.drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_RIGHT_HIP);
		  context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_HIP, SimpleOpenNI.SKEL_RIGHT_KNEE);
		  context.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_KNEE, SimpleOpenNI.SKEL_RIGHT_FOOT);  
		}

	public void drawCalibPoint()
	{
	  pushStyle();

	  strokeWeight(3);
	  noFill();

	  switch(calibMode)
	  {
	  case CALIB_START:    
	    break;
	  case CALIB_NULLPOINT:
	    context.convertRealWorldToProjective(nullPoint3d, tempVec1);

	    stroke(255, 255, 255, 150);
	    ellipse(tempVec1.x, tempVec1.y, 10, 10);  
	    break;
	  case CALIB_X_POINT:
	    // draw the null point
	    context.convertRealWorldToProjective(nullPoint3d, tempVec1);
	    context.convertRealWorldToProjective(xDirPoint3d, tempVec2);

	    stroke(255, 255, 255, 150);
	    ellipse(tempVec1.x, tempVec1.y, 10, 10);  

	    stroke(255, 0, 0, 150);
	    ellipse(tempVec2.x, tempVec2.y, 10, 10);  
	    line(tempVec1.x, tempVec1.y, tempVec2.x, tempVec2.y);

	    break;
	  case CALIB_Z_POINT:

	    context.convertRealWorldToProjective(nullPoint3d, tempVec1);
	    context.convertRealWorldToProjective(xDirPoint3d, tempVec2);
	    context.convertRealWorldToProjective(zDirPoint3d, tempVec3);

	    stroke(255, 255, 255, 150);
	    ellipse(tempVec1.x, tempVec1.y, 10, 10);  

	    stroke(255, 0, 0, 150);
	    ellipse(tempVec2.x, tempVec2.y, 10, 10);  
	    line(tempVec1.x, tempVec1.y, tempVec2.x, tempVec2.y);

	    stroke(0, 0, 255, 150);
	    ellipse(tempVec3.x, tempVec3.y, 10, 10);  
	    line(tempVec1.x, tempVec1.y, tempVec3.x, tempVec3.y);

	    break;
	  case CALIB_DONE:


	    break;
	  }

	  popStyle();
	}

	public void keyPressed()
	{

		// lock keyboard for entering new gesture name
		if ( calibMode == LEARN_GESTURE ){
			if ( key == '\n' ){
				saved = typing;
				typing = "";
				calibMode = CALIB_START;
			}
			else
				typing = typing + key;
		}

		else{

			switch(key)
			{
			case '1': 
				screenFlag = !screenFlag; 
				break;
			case 'c': 
				calibMode = LEARN_GESTURE;
				sequences = new ArrayList<List<ObservationDiscrete<Direction>>>();
				break;
			case 'f': 
				calibMode = IDLE;
				recognizer.newGesture(states, sequences, saved);
				break;
			case 'i': 
				calibMode = IDLE; 
				break;
			case 'a': 
				if ( calibMode == CALIB_START ){
					sequences.add(addedSequence);
					addedSequence = new ArrayList<ObservationDiscrete<Direction>>();
				}
				break;
			case ' ': 
				calibMode++;
				if (calibMode > CALIB_DONE)
				{
					calibMode = CALIB_START; 
					context.resetUserCoordsys();
				}
				else if (calibMode == CALIB_DONE)
				{  
					// set the calibration
					context.setUserCoordsys(nullPoint3d.x, nullPoint3d.y, nullPoint3d.z, 
							xDirPoint3d.x, xDirPoint3d.y, xDirPoint3d.z, 
							zDirPoint3d.x, zDirPoint3d.y, zDirPoint3d.z);

					println("Set the user define coordinatesystem");
					println("nullPoint3d: " + nullPoint3d);
					println("xDirPoint3d: " + xDirPoint3d);
					println("zDirPoint3d: " + zDirPoint3d);

					/*
	      // test
	      context.getUserCoordsysTransMat(userCoordsysMat);
	      PVector temp = new PVector();

	      userCoordsysMat.mult(new PVector(0, 0, 0), temp);         
	      println("PVector(0,0,0): " + temp);

	      userCoordsysMat.mult(new PVector(500, 0, 0), temp);        
	      println("PVector(500,0,0): " + temp);

	      userCoordsysMat.mult(new PVector(0, 500, 0), temp);        
	      println("PVector(0,500,0): " + temp);

	      userCoordsysMat.mult(new PVector(0, 0, 500), temp);
	      println("PVector(0,0,500): " + temp);
					 */
				}

				break;
			}
		}
	}  

	public void mousePressed() 
	{
	  if (mouseButton == LEFT)
	  {
	    PVector[] realWorldMap = context.depthMapRealWorld();
	    int index = mouseX + mouseY * context.depthWidth();

	    switch(calibMode)
	    {
	    case CALIB_NULLPOINT:
	      nullPoint3d.set(realWorldMap[index]);
	      break;
	    case CALIB_X_POINT:
	      xDirPoint3d.set(realWorldMap[index]);
	      break;
	    case CALIB_Z_POINT:
	      zDirPoint3d.set(realWorldMap[index]);
	      break;
	    }
	  }
	  else
	  {
	    PVector[] realWorldMap = context.depthMapRealWorld();
	    int index = mouseX + mouseY * context.depthWidth();  
	    

	    println("Point3d: " + realWorldMap[index].x + "," + realWorldMap[index].y + "," + realWorldMap[index].z);
	  }
	}

	public void mouseDragged() 
	{
	  if (mouseButton == LEFT)
	  {
	    PVector[] realWorldMap = context.depthMapRealWorld();
	    int index = mouseX + mouseY * context.depthWidth();

	    switch(calibMode)
	    {
	    case CALIB_NULLPOINT:
	      nullPoint3d.set(realWorldMap[index]);
	      break;
	    case CALIB_X_POINT:
	      xDirPoint3d.set(realWorldMap[index]);
	      break;
	    case CALIB_Z_POINT:
	      zDirPoint3d.set(realWorldMap[index]);
	      break;
	    }
	  }

	}
	
	// -----------------------------------------------------------------
		// SimpleOpenNI events

		public void onNewUser(int userId)
		{
		  println("onNewUser - userId: " + userId);
		  println("  start pose detection");
		  
		  if(autoCalib)
		    context.requestCalibrationSkeleton(userId,true);
		  else    
		    context.startPoseDetection("Psi",userId);
		}

		public void onLostUser(int userId)
		{
		  println("onLostUser - userId: " + userId);
		  beepTwice();
		}

		public void onExitUser(int userId)
		{
		  println("onExitUser - userId: " + userId);
		}

		public void onReEnterUser(int userId)
		{
		  println("onReEnterUser - userId: " + userId);
		  Toolkit.getDefaultToolkit().beep();
		}

		public void onStartCalibration(int userId)
		{
		  println("onStartCalibration - userId: " + userId);
		}

		public void onEndCalibration(int userId, boolean successfull)
		{
		  println("onEndCalibration - userId: " + userId + ", successfull: " + successfull);
		  
		  if (successfull) 
		  { 
		    println("  User calibrated !!!");
		    context.startTrackingSkeleton(userId);
		    Toolkit.getDefaultToolkit().beep();
		  } 
		  else 
		  { 
		    println("  Failed to calibrate user !!!");
		    println("  Start pose detection");
		    context.startPoseDetection("Psi",userId);
		  }
		}

		public void onStartPose(String pose,int userId)
		{
		  println("onStartPose - userId: " + userId + ", pose: " + pose);
		  println(" stop pose detection");
		  
		  context.stopPoseDetection(userId); 
		  context.requestCalibrationSkeleton(userId, true);
		 
		}

		public void onEndPose(String pose,int userId)
		{
		  println("onEndPose - userId: " + userId + ", pose: " + pose);
		  beepTwice();
		}
		
			
		
		// Gesture controll task
		
		class GesturePositionTask extends TimerTask {

	        @Override
	        public void run() {
	        	
	        	PVector[] realWorldMap = context.depthMapRealWorld();
	    	    //int index = mouseX + mouseY * context.depthWidth();  
	            //System.out.println("Point3d: " + realWorldMap[index].x + "," + realWorldMap[index].y + "," + realWorldMap[index].z);
		            
	        	if(checkIfTrackingReady()){
			        // draw the hand if it's available
			  		int[] userList = context.getUsers();
			  		for(int i=0;i<userList.length;i++)
			  		{
			  		  if(context.isTrackingSkeleton(userList[i]))
			  		     drawElement(userList[i]);
			  		}
	        	}
		        //timer.cancel(); //Not necessary because we call System.exit

	        	
	        }
	        
	      void drawElement(int userId){
	    	  

	    	  // write 3D position of a joint
	    	  PVector currentHand = new PVector();
	    	  context.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_RIGHT_HAND,currentHand);
	    	  Direction d = FeatureVectorExtractor.findDirection(PVector.sub(currentHand, previousHand));
	    	  previousHand = currentHand;

	    	  // print hand position
	    	  //System.out.println("Left hand position: " + currentHand.x + "," + currentHand.y + "," + currentHand.z);
	    	  //System.out.println(d);

	    	  // extract feature vector

	    	  //System.out.println(isInitiated(userId)+"  "+extractor.isTracking()+"  "+extractor.isLocked());

	    	  // check if is not tracking and initiation conditions are met
	    	  // start tracking
	    	  if ( !extractor.isTracking() && isInitiated(userId) )
	    		  if ( isInitiated(userId) ){
	    			  extractor.startTracking();
	    			  //Toolkit.getDefaultToolkit().beep();
	    		  }

	    	  // add feature
	    	  if ( extractor.isTracking() ){
	    		  extractor.addFeature(d);
	    	  }

	    	  // finish tracking
	    	  if ( extractor.isTracking() && !extractor.isLocked())
	    		  if ( /*isFinished(userId)*/ extractor.checkFeatureVectorFinish() ){
	    			  extractor.stopTracking();
	    			  if ( calibMode == IDLE ){
		    			  recognizedSymbol = recognizer.findMatchingSymbol(extractor.getFeatureVector());
		    			  timer.schedule(new DrawRecognizedTask(), Config.DISPLAY_RECOGNIZED); //delay in milliseconds
		    			  displayRecognized = true;
		    			  controller.affectRecognizedSymbol(recognizedSymbol);
	    			  }
	    			  else if (calibMode == CALIB_START){
	    				  addedSequence = extractor.getFeatureVector();
	    			  }
	    				  
	    		  }

	      }
	    }
		
		// checks if kinect is ready to track body parts to extract move vectors
		public boolean checkIfTrackingReady(){		
			
			//if ( calibMode == CALIB_DONE )
				return true;
			//else
				//return false;
		}

		public void print(PVector vector){
			
			System.out.println("Vector: [" + vector.x + "," + vector.y + "," + vector.z+"]");
			
		}
		
		// returns true if gesture sequence is initiated 
		public boolean isInitiated(int userId){
			
			PVector leftHand = new PVector();
			PVector rightHand = new PVector();
			PVector head = new PVector();
	    	context.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_LEFT_HAND,leftHand);
	    	context.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_RIGHT_HAND,rightHand);
	    	context.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_NECK,head);
			
			if ( Math.sqrt(Math.pow(leftHand.x - rightHand.x,2) + Math.pow(leftHand.y - rightHand.y,2)) <= Config.INITIATION_JOINT_DISTANCE
					&& Math.sqrt(Math.pow(leftHand.x - head.x,2) + Math.pow(leftHand.y - head.y,2)) <= Config.INITIATION_JOINT_DISTANCE
					&& Math.sqrt(Math.pow(head.x - rightHand.x,2) + Math.pow(head.y- rightHand.y,2)) <= Config.INITIATION_JOINT_DISTANCE)
				return true;
			else
				return false;
		}

		
		class DrawRecognizedTask extends TimerTask {
	        @Override
	        public void run() {        	
	        	displayRecognized = false;
	        }
		}
		
		
		
		private void findPointedObject(int userId){
			
			PVector elbow = new PVector();
	    	context.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_RIGHT_ELBOW,elbow);
	    	PVector hand = new PVector();
	    	context.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_RIGHT_HAND,hand);
	    	context.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_RIGHT_SHOULDER,shoulder);
	    	
	    	PVector upperArm = PVector.sub(elbow,shoulder);
	    	PVector foreArm = PVector.sub(hand,elbow);
	    	
	    	upperArm.normalize();
	    	foreArm.normalize();
	    	
	    	isPointing = PVector.dot(upperArm, foreArm) > Config.POINTING_THRESHOLD;
	    	
	    	if ( isPointing ){
	    		//PVector pointingVec = PVector.sub(hand,shoulder);
	    		Line3D pointingDir = new Line3D(hand, shoulder);    		
	    		pointedObj = controller.findPointedObject(pointingDir);
	    		previouslyPointedObj = pointedObj;
	    		// if checking  pointingtaskobject isn't running - run it
	    		if ( !taskScheduled && pointedObj != null ){
	    			timer.schedule(new FindPointedObjectTask(), Config.CHECK_IF_POINTED); //delay in milliseconds
	    			taskScheduled = true;
	    		}
	    	}
			
		}
		
		private void affectPointedObject(LCNObject obj) {
			
			// check if it isn't object affected before
			if (obj != affectedObj){
			
				if ( obj != null ){
					affectedObj = obj;
					displayPointed = true;
					timer.schedule(new DrawPointedTask(), Config.DISPLAY_POINTED); //delay in milliseconds
					controller.sendCommand(obj);
					timer.schedule(new LockPointedObjectTask(), Config.LOCK_POINTING_AGAIN);
				}
			}
		}
		
		class FindPointedObjectTask extends TimerTask {
	        @Override
	        public void run() {        	
	        	
		        	// check if you are pointing  the same object for some period of time
		        	if ( pointedObj == previouslyPointedObj && isPointing ){
		        		affectPointedObject(pointedObj);
		        	}
	        		
		        	taskScheduled = false;
	        	}
	        
		}
		
		class DrawPointedTask extends TimerTask {
	        @Override
	        public void run() {        	
	        	displayPointed = false;
	        }
		}
		
		class LockPointedObjectTask extends TimerTask {
	        @Override
	        public void run() {        	
	        	affectedObj = null;
	        }
		}

		@Override
		public void destroy() {
			
			controller.disconnect();
			
			super.destroy();
		}
		
		void beepTwice(){
			Toolkit.getDefaultToolkit().beep();
			timer.schedule(new SecondBeepTask(), 100); //delay in milliseconds
		}
		
		class SecondBeepTask extends TimerTask {
	        @Override
	        public void run() {        	
	        	Toolkit.getDefaultToolkit().beep();
	        }
		}
		
}
