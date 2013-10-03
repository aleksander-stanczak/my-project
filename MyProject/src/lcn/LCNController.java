package lcn;

import geometry.Line3D;
import geometry.Point3D;

import java.util.ArrayList;

import app.Config;

import network.PCHKClient;

import processing.core.PVector;
import recognition.Symbol;

import debug.Debug;

public class LCNController {

	/**
	 * @param args
	 */
	
	ArrayList<LCNObject> objects = new ArrayList<LCNObject>();
	PCHKClient client;
	
	
	public LCNController() {
		
		/*objects.add(new LCNObject("kinect", new PVector(0,0,0)));
		objects.add(new LCNObject("fotel", new PVector(641,-487,1777)));
		objects.add(new LCNObject("pufcio", new PVector(-509,-384,1048)));
		objects.add(new LCNObject("lampa górna", new PVector(-805,968,2845)));*/
		
/*		objects.add(
				new LCNObject("kinect", 1, 0, 8, false, new PVector(0,0,0), null)
					);*/
		
		//lampa nad stolem
		objects.add(
				new LCNObject("salon stol", 22, 0, 2, true, new PVector(2000,500,1500), null)
					);
		
		//lampa boczna
				objects.add(
						new LCNObject("lampa boczna", 21, 0, 2, true, new PVector(-510,215,3340), null)
							);
				
		//lampa gorna
		objects.add(
				new LCNObject("lampa gorna", 151, 0, 1, true, new PVector(1000,1500,3600), null)
					);
		
		//zaslona
				objects.add(
						new LCNObject("zaslona", 153, 0, 8, false, new PVector(-500,225,4770), null)
							);
				
		//kolumna
		objects.add(
				new LCNObject("kolumna", 151, 0, 2, true, new PVector(2600,900,3370), null)
					);
		//panoramiczne
		objects.add(
				new LCNObject("panoramiczne", 153, 0, 5, false, new PVector(-670,-115,1600), null)
					);
		
		System.out.println(objects);
		
		client = new PCHKClient();
	}
	
	public void connectToPCHK(){
		
		(new Thread() {
			  public void run() {
				  
				//client.connect("192.168.1.200", 4114, "lcnpro", "lcnpro");
				client.connect("retmanska46.dyndns.org", 4114, "lcnpro", "lcnpro");
				//client.connect("localhost", 4114, "lcnpro", "lcnpro");
				
			  }
			 }).start();
		
		
	}
	
	public void disconnect(){
		client.disconnect();
	}
	
	public void sendCommand(LCNObject obj){
		
		//client.sendMsg(">M000022.A2DI10000");
		//client.sendMsg(">M000022.A2TA00");
		System.out.println("OBJ"+obj);
		client.sendMsg(obj.flip());
		
	}
	
	public void sendCommand(String cmd){
		
		client.sendMsg(cmd);
		
	}

	public LCNObject findPointedObject(Line3D pointingLine){
		
		PVector p1 = pointingLine.p1;
		PVector p2 = pointingLine.p2;
		
		double tDiff = pointingLine.findT(p2) - pointingLine.findT(p1);
		//System.out.println(tDiff);
		//tDiff = Math.signum(tDiff);
		
		//System.out.println(tDiff);

		//pointingLine.print();
		//System.out.println(pointingLine.findT(new PVector(1,0,0)));
		
		double distance;
		double minDistance = Double.MAX_VALUE;
		int pointedObj = -1;
		
		Debug.debugs.clear();
		for (int i = 0; i < objects.size(); i++) {
			
			// check if object is in front from hand not in back or a side
			if ( pointingLine.findT(objects.get(i).location) < tDiff * pointingLine.findT(p2) ){
				
				distance = pointingLine.distanceToPoint(objects.get(i).location);
				//System.out.println(objects.get(i).name+": "+distance);
				
				if ( distance < minDistance && distance < Config.MAXIMAL_POINTING_DISTANCE_ERROR ){
					minDistance = distance;
					pointedObj = i;
				}
				Debug.debugs.add(objects.get(i).name+": "+String.valueOf(distance));
				//Debug.writeOnScreen(String.valueOf((pointingLine.distanceToPoint(objects.get(0).location))));
			}
		}
		
		if (pointedObj == -1)
			return null;
		else
			return objects.get(pointedObj);
		
	}
	
	public static void main(String[] args) {
		
		LCNController controller = new LCNController();
		controller.client.connect("retmanska46.dyndns.org", 4114, "lcnpro", "lcnpro");
		
		//controller.sendCommand(">M000151.TSK--10000000");
		controller.sendCommand(">M000151.TSK--00100000");
		controller.sendCommand(">M000151.TSK--01000000");
		//controller.sendCommand("#ISyGsIWiPic=?");
		
		//controller.sendCommand(">M000022.A2TA00");
		//controller.connectToPCHK();
		controller.disconnect();

	}

	public void affectRecognizedSymbol(Symbol recognizedSymbol) {
		
		if ( recognizedSymbol.name.equalsIgnoreCase("kolacja") )
			sendCommand(">M000151.TSK--10000000");
		if ( recognizedSymbol.name.equalsIgnoreCase("tv") )
			sendCommand(">M000151.TSK--01000000");
		if ( recognizedSymbol.name.equalsIgnoreCase("relaks") )
			sendCommand(">M000151.TSK--00100000");
		
	}

}
