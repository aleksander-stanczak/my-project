package lcn;

import processing.core.PVector;
import recognition.Symbol;
import geometry.Point3D;

public class LCNObject {
	
	int module;
	int segment;
	int output;
	String name;
	
	Object status;
	
	boolean dimmable;
	
	PVector location;
	Symbol symbol;
	
	
	public LCNObject(String name, PVector location) {

		this.name = name;
		this.location = location;
	}
	
	public LCNObject(String name, int module, int segment, int output, boolean dimmable, PVector location,
			Symbol symbol) {
		super();
		this.module = module;
		this.segment = segment;
		this.name = name;
		this.location = location;
		this.symbol = symbol;
		this.output = output;
		this.dimmable = dimmable;
	}

	@Override
	public String toString() {

		return this.name+" "+this.location;//" ["+this.module+"]";
	}
	
	
	public String flip(){
		
		if ( dimmable )
			return generatePositonCommand()+".A"+output+"TA00";
		else{
			StringBuilder rl = new StringBuilder("--------");
			rl.setCharAt(output-1, 'U');
			return generatePositonCommand()+".RL"+rl;
		}
	}

	private String generatePositonCommand(){
		
		//">M000022.A2TA00"
		
		String segment = String.valueOf(this.segment);
		String module = String.valueOf(this.module);
		
		System.out.println(segment.length());
        if(segment.equals(""))
            segment = "000";
        else if (segment.length() == 1)
            segment = "00"+segment;
        else if (segment.length() == 2)
            segment = "0"+segment;
        if(module.equals(""))
            module = "000";
        else if (module.length() == 1)
            module = "00"+module;
        else if (module.length() == 2)
            module = "0"+module;
		
        String command = ">M"+segment+module;
        //System.out.println(command);
        
        return command;
	}
	
	public static void main(String[] args) {
		
		LCNObject obj = new LCNObject("kinect", 1, 0, 8, false, new PVector(0,0,0), null);
		
		System.out.println(obj.flip());
		
		
	}
}
