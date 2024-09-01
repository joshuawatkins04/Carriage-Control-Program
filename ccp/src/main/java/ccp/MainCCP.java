package ccp;

import java.io.IOException;

// Old Code
/*class Connect_CCP extends Thread {

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
}*/

public class MainCCP {

    private static int CCPState = 0;
    private static Boolean InterruptStatus = false;
    private static String MCPMessageToSend = "";
    private static String MCPMessageRecieved = "";
    private static String ESPMessageToSend = "";
    private static String ESPMessageRecieved = "";

    public static void main(String[] args) {

        System.out.print("\033[H\033[2J");
        System.out.flush();
        
        MainCCP Main = new MainCCP();
        Main.start();

        while(CCPState!= 0){
            if (MCPMessageRecieved == "SlowDown"){
                ESPMessageToSend = "SlowDown";
            }
            if (MCPMessageRecieved == "SpeedUp"){
                ESPMessageToSend = "SpeedUp";
            }
            if (MCPMessageRecieved == "Stop"){
                ESPMessageToSend = "Stop";
            }
            //etc
        }

    }

    Thread ThreadMCP = new Thread() {
        public void run() {
            ConnectionManager connectionManagerMCP = new ConnectionManager();
            try {
                connectionManagerMCP.setup();
                connectionManagerMCP.handshake();
                connectionManagerMCP.receive_message();
                // Controls speed of loop for sending packets
                // Loops every 3 seconds
                for (int i = 0; i < 3; i++) {
                    if (MCPMessageToSend != ""){
                        connectionManagerMCP.send_at_station();
                        connectionManagerMCP.receive_message(); // This needs Visibility to give me the ability to set MCPMessageRecieved
                    }
                    else {
                        connectionManagerMCP.send_status();
                        connectionManagerMCP.receive_message(); // This needs Visibility to give me the ability to set MCPMessageRecieved
                    }
                    if(MCPMessageRecieved == "Confirm"){
                        MCPMessageToSend = "";
                    }
                    Thread.sleep(3000);
                }
                connectionManagerMCP.end_connection();
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
            }
        }
    };

    // This entire thread needs to be updated to new ESP connection manager
    Thread ThreadESP = new Thread() {
        public void run() {
            ConnectionManager connectionManagerESP = new ConnectionManager();
            try {
                connectionManagerESP.setup();
                connectionManagerESP.handshake();
                connectionManagerESP.receive_message();
                // Controls speed of loop for sending packets
                // Loops every 3 seconds
                for (int i = 0; i < 3; i++) {
                    if (MCPMessageToSend != ""){
                        connectionManagerESP.send_at_station();
                        connectionManagerESP.receive_message(); // This needs Visibility to give me the ability to set ESPMessageRecieved
                    }
                    else {
                        connectionManagerESP.send_status();
                        connectionManagerESP.receive_message(); // This needs Visibility to give me the ability to set ESPMessageRecieved
                    }
                    if(ESPMessageRecieved == "Confirm"){
                        ESPMessageToSend = "";
                    }
                    Thread.sleep(3000);
                }
                connectionManagerESP.end_connection();
            } catch (InterruptedException | IOException e) {
                Thread.currentThread().interrupt();
            }
        }
    };

    public void interrupt() {
        InterruptStatus = true;
    }

    public void start() {
        // Stopped State CCPState == 0
        if (CCPState == 0) {
            // Connect_CCP thread = new Connect_CCP();
            // Thread Thread1 = new Thread(this::thread1).start();
            // new Thread(this::thread2).start(); 
            ThreadMCP.start();
            ThreadESP.start();
            CCPState = 1;// Running
        } else {
            System.out.println("Error: Failed Start - Can only start from Off State");
        }
    }

    public void stop() {
        // Running State CCPState == 1
        if (CCPState == 1) {
            ThreadMCP.interrupt();
            ThreadESP.interrupt();
            CCPState = 0;// Stopped
        } else {
            System.out.println("Error: Failed Stop - Can only start from Running State");
        }
    }

}
