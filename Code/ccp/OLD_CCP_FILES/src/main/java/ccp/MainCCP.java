package ccp;

import java.io.IOException;

class Connect_CCP extends Thread {

    public void run() {
        ConnectionManager connectionManager = new ConnectionManager();
        boolean arrived_at_station = true; // NEED TO HAVE SOMETHING CHANGE THIS VALUE
        
        try {
            connectionManager.setup();
            connectionManager.handshake();
            connectionManager.receive_message();
            
            // Controls speed of loop for sending packets
            // Loops every 3 seconds
            for (int i = 0; i < 3; i++) {
                if (arrived_at_station) {
                    connectionManager.send_at_station();
                    connectionManager.receive_message();
                } else {
                    connectionManager.send_status();
                    connectionManager.receive_message();
                }
                Thread.sleep(3000);
            }
            connectionManager.end_connection();
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class MainCCP {

    public static void main(String[] args) {
        
        System.out.print("\033[H\033[2J");  
        System.out.flush();

        Connect_CCP thread = new Connect_CCP();
        thread.start();

    }
}