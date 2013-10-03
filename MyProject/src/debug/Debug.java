package debug;

import java.util.ArrayList;
import java.util.Arrays;

public class Debug {
	
	public static ArrayList<Object> debugs = new ArrayList<Object>();
	
	public static void out(Object var){
		System.out.println("Variable: "+var);
	}
	
	public static void out(Object var, String comment){
		
		if ( var instanceof Object[])
			System.out.println("Variable Array "+comment+": "+Arrays.toString((Object[])var));
			
		else
		System.out.println("Variable "+comment+": "+var);
	}
	
	public static String txt = "";
	
	public static void writeOnScreen(String newTxt){
		
		debugs.add(newTxt);
		txt  = newTxt;
	}

}
