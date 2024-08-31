/*
 * Has the role of connecting to and communicating with 
 * the MCP and the ESP32_Manager program.
 */

import java.io.IOException;

class Connect_CCP extends Thread {
    
    public void run() {
        CCP_Connection_Man ccp_connection_man = new CCP_Connection_Man();
        try {
            for (int i = 0; i < 10; i++) {
                ccp_connection_man.setup();
                ccp_connection_man.send_to_MCP("Test");
                ccp_connection_man.receive_message();
                Thread.sleep(1000);
            }
            ccp_connection_man.end_connection();
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class Offboard_Master_CCP {

    public static void main(String[] args) {
        
        Connect_CCP thread = new Connect_CCP();
        thread.start();

    }
}
