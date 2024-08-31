/*
 * Has the role of connecting to and communicating with 
 * the MCP and the ESP32_Manager program.
 */

import java.io.IOException;

class Connect_CCP extends Thread {
    
    public void run() {
        CCP_Connection_Man ccp_connection_man = new CCP_Connection_Man();
        boolean arrived_at_station = true; // NEED TO HAVE SOMETHING CHANGE THIS VALUE
        try {
            ccp_connection_man.setup();
            ccp_connection_man.handshake();
            ccp_connection_man.receive_message();
            
            // Controls speed of loop for sending packets
            // Loops every 3 seconds
            for (int i = 0; i < 3; i++) {
                ccp_connection_man.send_status();
                ccp_connection_man.receive_message();

                if (arrived_at_station) {
                    ccp_connection_man.send_at_station();
                }

                Thread.sleep(3000);
            }
            ccp_connection_man.end_connection();
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class Offboard_Master_CCP {

    public static void main(String[] args) {
        
        System.out.print("\033[H\033[2J");  
        System.out.flush();

        Connect_CCP thread = new Connect_CCP();
        thread.start();

    }
}
