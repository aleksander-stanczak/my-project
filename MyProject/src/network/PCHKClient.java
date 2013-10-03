/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package network;


import java.net.*;
import java.io.*;

import debug.Debug;

/**
 *
 * @author Aleks
 */
public class PCHKClient {

	Socket socket;
	OutputStream os;
	InputStream is;
	PrintWriter out;
	BufferedReader in;
	private Boolean connectionFlag = false;
	ListeningThread listeningThread;
	private boolean isListening;
	
	public static final int UPDATE_STATUS = 0;

public Boolean getConnectionFlag() {
	
		if ( os == null || socket == null )
			connectionFlag = false;
	
		return connectionFlag;
	}
/**
 *
 * @author aleks
 */

public boolean connect(String host, int port , String login, String password) {
    try {

        socket = new Socket(host, port);
        os = socket.getOutputStream();
        out = new PrintWriter(os, true);
        is = socket.getInputStream();
        in = new BufferedReader(
        new InputStreamReader(is));

        BufferedReader stdIn = new BufferedReader(
                new InputStreamReader(System.in));
        String userInput;

        // lcn version
        System.out.println(readRsp());
        
        // login
        System.out.println(readRsp());
        out.println(login);
        System.out.println(login);

        // pass
        System.out.println(readRsp());
        out.println(password);
        System.out.println(password);

        //System.out.println(readRsp());
        if (readRsp().equalsIgnoreCase("OK")){
        	System.out.println("Connected & logged to LCN-PCH");
        	connectionFlag = true;
        	isListening = true;
        	listeningThread = new ListeningThread(this);
        	listeningThread.start();
        }
        else
          //output.append("Could't connect"+"\n");
        	System.out.println("Could't connect");


        return true;
    } catch (Exception e) {
        System.err.println("Client exception: " + e);
        return false;
    }
}
public void disconnect() {
    try {
        System.out.println("Closing socket "+ socket+"...");
        isListening = false;
        socket.close();
        //output.append("Disconnected"+"\n");
        System.out.println("Client disconnected");
        connectionFlag = false;
    }
    catch (Exception e) {
        System.err.println("Client exception: " + e);
    }
  }
public void sendMsg(String msg){
        try {
        System.out.println("Sending message...");
        //System.out.println(socket);
        System.out.println(msg);
        out.println(msg);
        
        //readConfirmation();
        
        //System.out.println("Response "+readRsp());
    } catch (Exception e) {
        System.err.println("Client exception: " + e);
    }
}
private String readConfirmation() {
	
	String resp;
	while(true){
		
		resp = readRsp();
		Debug.out(resp, "resp");
		if (resp.startsWith("M"));
			break;
	}
	
	return resp;
}
public String readRsp(){
        try{
        return in.readLine();
        }
        catch(Exception e){
            System.err.println("Client exception: " + e);
            return "not connected";
        }
}
public void showSocket(){
    System.out.println("my socket: "+ socket);
}
public void listServer(){
    out.println("list");
}

public String listenOnSocket(){
	
	return readConfirmation();
	
}


	public static void main(String[] args) {
		
		
		System.out.println("test");
	}
	
	private class ListeningThread extends Thread{
		
		PCHKClient client;

		public ListeningThread(PCHKClient client) {
			System.out.println("const thread");
			this.client = client;
		}

		@Override
		public void run() {
			
			try {
	            String line = null;
	            System.out.println("Run thread");

	            while((line = in.readLine()) != null && isListening )
	                {
	        		//Debug.out(line, "line");
	        		if (line.startsWith("M"));
	        			processMsg(line);

	                }
	            } catch (Exception e) {
	                // ...
	            }

	        System.out.println("End");
			
			
			
		}
		
	}
	
	public void processMsg(String msg){
		
		//System.out.println(msg);
		
		/*Target t = LCNCommand.recognizeTargetStatus(msg);
		//System.out.println(t.getSegment()+" "+t.getModule()+" "+t.getOutput()+" "+t.getStatus());
		
		ArrayList<Target> targets = SmartHouse.global_data.getTargets();
		//Target ta = targets.get(0);
		//System.out.println(ta.getSegment()+" "+ta.getModule()+" "+ta.getOutput()+" "+ta.getStatus());
		
		for (Target target : targets) {
			// if target equal to one in app memory
			if ( target.getModule().equals(t.getModule()) && target.getSegment().equals(t.getSegment()) && target.getOutput().equals(t.getOutput()) ){
				// then update it's status
				target.setStatus(t.getStatus());
				// notify UI
				if ( objectsHandler != null ){
					Message handlerMsg = Message.obtain(objectsHandler);
					handlerMsg.what = SmartHouse.UPDATE_STATUS;
					objectsHandler.sendMessage(handlerMsg);
				}
				
				
			}
		}*/
		
	}
	public void setObjectsHandler(int handler) {
		
		//objectsHandler = handler;
		
	}


}
