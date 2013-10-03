package lcn;

public class LCNCommand {

	public static LCNCommand createCommand(LCNObject obj){
		
		return null;
	}
	
	/*// every LCN command can be constructed from Action + it's target
	private Target target;
	private Action action;
	
	
	public LCNCommand(Target target, Action action) {
		super();
		this.target = target;
		this.action = action;
	}

	// TO DO make it objective
	public String createCommandString(){
		
        //>M000006.A1DI000004
		
		String module = String.valueOf(target.getModule());
		String segment = String.valueOf(target.getSegment());

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
        

        /////////////////////////////

        // output
        String output = String.valueOf(target.getOutput());
        if(output.equalsIgnoreCase("output 1"))
            output = "1";
        else if(output.equalsIgnoreCase("output 2"))
            output = "2";
        else if(output.equalsIgnoreCase("output 3"))
            output = "3";
        
        /////////////////////////////

        // action
        String act = action.getHex_string();

        String command = ">M"+segment+module+".A"+output+act+"004\n";
        
        return command;
	}
	
	public static Target recognizeTargetStatus(String status){
		
		//System.out.println("Status recognision process...");
		
		//System.out.println(status.substring(2, 5));
		//System.out.println(status.substring(5, 8));
		//System.out.println(status.substring(9, 10));
		
		Target target = new Target("target",ObjectType.Light, // name and type
				Integer.valueOf(status.substring(5, 8)), // module
				Integer.valueOf(status.substring(2, 5)), // segment
				Integer.valueOf(status.substring(9, 10))); // output
		
		//System.out.println(status.substring(10, 13));
		target.setStatus(Integer.valueOf(status.substring(10, 13))); // status
		
		return target;
	}*/
}
