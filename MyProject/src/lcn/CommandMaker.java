/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lcn;


/**
 *
 * @author Aleks
 */
public class CommandMaker {

    public static String createASCIICmd(String segment, String module, String output, String cmd){

        //>M000006.A1DI000004

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

/*        if(output.equalsIgnoreCase("output 1"))
            output = "1";
        else if(output.equalsIgnoreCase("output 2"))
            output = "2";
        else if(output.equalsIgnoreCase("output 3"))
            output = "3";*/

        if(cmd.equalsIgnoreCase("turn on"))
            cmd = "100";
        else if(cmd.equalsIgnoreCase("turn off"))
            cmd = "000";

        String command = ">M"+segment+module+".A"+output+"DI"+cmd+"004\n";
        System.out.println(command);
        return command;
    }

}
