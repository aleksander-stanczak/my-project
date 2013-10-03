package app;

import java.util.TimerTask;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PVector;
import recognition.FeatureVectorExtractor;
import util.Direction;

public class SmartHouseWindow extends PApplet {
	
	/* --------------------------------------------------------------------------
	 * SimpleOpenNI User Test
	 * --------------------------------------------------------------------------
	 * Processing Wrapper for the OpenNI/Kinect library
	 * http://code.google.com/p/simple-openni
	 * --------------------------------------------------------------------------
	 * prog:  Max Rheiner / Interaction Design / zhdk / http://iad.zhdk.ch/
	 * date:  02/16/2011 (m/d/y)
	 * ----------------------------------------------------------------------------
	 */


	/**
	 * 
	 */
	private static final long serialVersionUID = -1122277720500215689L;
	SimpleOpenNI  context;
	boolean       autoCalib=true;

	public void setup()
	{
	  context = new SimpleOpenNI(this);
	   
	  // enable depthMap generation 
	  if(context.enableDepth() == false)
	  {
	     println("Can't open the depthMap, maybe the camera is not connected!"); 
	     exit();
	     return;
	  }
	  
	  // enable skeleton generation for all joints
	  context.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
	 
	  background(200,0,0);

	  stroke(0,0,255);
	  strokeWeight(3);
	  smooth();
	  
	  size(context.depthWidth(), context.depthHeight()); 
	}

	public void draw()
	{
	  // update the cam
	  context.update();
	  
	  // draw depthImageMap
	  image(context.depthImage(),0,0);
	  
	  // draw the skeleton if it's available
	  int[] userList = context.getUsers();
	  for(int i=0;i<userList.length;i++)
	  {
		  //System.out.println(context.isTrackingSkeleton(userList[i]));
	    if(context.isTrackingSkeleton(userList[i]))
	      drawSkeleton(userList[i]);
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

	// -----------------------------------------------------------------
	// SimpleOpenNI events

	public void onNewUser(int userId)
	{
	  System.out.println("onNewUser - userId: " + userId);
	  println("  start pose detection");
	  
	  if(autoCalib)
	    context.requestCalibrationSkeleton(userId,true);
	  else    
	    context.startPoseDetection("Psi",userId);
	}

	public void onLostUser(int userId)
	{
	  println("onLostUser - userId: " + userId);
	}

	public void onExitUser(int userId)
	{
	  println("onExitUser - userId: " + userId);
	}

	public void onReEnterUser(int userId)
	{
	  println("onReEnterUser - userId: " + userId);
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
	}

	
	
	// Gesture controll task

	class GesturePositionTask extends TimerTask {
	
	    @Override
	    public void run() {
	    	
	    	PVector[] realWorldMap = context.depthMapRealWorld();
		    //int index = mouseX + mouseY * context.depthWidth();  
	        //System.out.println("Point3d: " + realWorldMap[index].x + "," + realWorldMap[index].y + "," + realWorldMap[index].z);
	            
	    	if(true){
		        // draw the hand if it's available
		  		int[] userList = context.getUsers();
		  		for(int i=0;i<userList.length;i++)
		  		{
		  		  if(context.isTrackingSkeleton(userList[i]))
		  		     drawElement(userList[i]);
		  		}
	    	}
	        //timer.cancel(); //Not necessary because we call System.exit
	        //System.exit(0); //Stops the AWT thread (and everything else)
	    }
	    
	  void drawElement(int userId){
		  
		  
		// write 3D position of a joint
		  /*PVector jointPos = new PVector();
		  context.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_RIGHT_HAND,jointPos);
		  Direction dir = FeatureVectorExtractor.findDirection(PVector.sub(jointPos, previousPos));
		  previousPos = jointPos;
		  System.out.println("Left hand position: " + jointPos.x + "," + jointPos.y + "," + jointPos.z);
		  System.out.println(dir);*/
		  
		// convert real world point to projective s pace
		 /* PVector jointPos_Proj = new PVector(); 
		  context.convertRealWorldToProjective(jointPos,jointPos_Proj);
		  System.out.println("Right hand position: " + jointPos_Proj.x + "," + jointPos_Proj.y + "," + jointPos_Proj.z);*/
	  }
}

}
