package ccp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TESTServerMCP {

    private static final int bufferSize = 1024;
    // private static final String ccpAddr = "192.168.0.37";
    private static final int ccpPort = 4210;
    private static final int mcpPort = 4000;
    private static DatagramSocket socket;
    private static InetAddress ccpAddress;
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        
        // DatagramSocket socket = null;

        System.out.print("\033[H\033[2J");
        System.out.flush();

        try {
            // Initialize the socket and server address
            socket = new DatagramSocket(mcpPort);
            // ccpAddress = InetAddress.getByName(ccpAddr);
            ccpAddress = InetAddress.getByName("localhost");

            // Test Case 1: Send a valid command "CCIN" and expect "AKIN" as a response
            String command = "CCIN";
            sendCommand(command);
            String response = receiveResponse();
            System.out.println("Test Case 1 - Sent: " + command + ", Received: " + response);

            // Test Case 2: Send an unknown command and expect "UNKNOWN_COMMAND" as a
            // response
            command = "UNKNOWN";
            sendCommand(command);
            response = receiveResponse();
            System.out.println("Test Case 2 - Sent: " + command + ", Received: " + response);

            // Test Case 3: Send a valid command "STRQ" and expect a specific response
            command = "STRQ";
            sendCommand(command);
            response = receiveResponse();
            System.out.println("Test Case 3 - Sent: " + command + ", Received: " + response);

            // Test Case 4.1: Send a valid command "EXEC" with JSON message and expect a
            // specific response
            command = generateExecCommand("STOPC");
            sendCommand(command);
            response = receiveResponse();
            System.out.println("Test Case 4.1 - Sent: EXEC STOPC command, Received: " + response);

            // Test Case 4.2: Send a valid command "EXEC" with JSON message and expect a
            // specific response
            command = generateExecCommand("STOPO");
            sendCommand(command);
            response = receiveResponse();
            System.out.println("Test Case 4.2 - Sent: EXEC command, Received: " + response);

            // Test Case 4.3: Send a valid command "EXEC" with JSON message and expect a
            // specific response
            command = generateExecCommand("FSLOWC");
            sendCommand(command);
            response = receiveResponse();
            System.out.println("Test Case 4.3 - Sent: EXEC command, Received: " + response);

            // Test Case 4.4: Send a valid command "EXEC" with JSON message and expect a
            // specific response
            command = generateExecCommand("FFASTC");
            sendCommand(command);
            response = receiveResponse();
            System.out.println("Test Case 4.4 - Sent: EXEC command, Received: " + response);

            // Test Case 4.5: Send a valid command "EXEC" with JSON message and expect a
            // specific response
            command = generateExecCommand("RSLOWC");
            sendCommand(command);
            response = receiveResponse();
            System.out.println("Test Case 4.5 - Sent: EXEC command, Received: " + response);

            // Test Case 4.6: Send a valid command "EXEC" with JSON message and expect a
            // specific response
            command = generateExecCommand("DISCONNECT");
            sendCommand(command);
            response = receiveResponse();
            System.out.println("Test Case 4.6 - Sent: EXEC command, Received: " + response);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    private static String generateExecCommand(String action) {
        try {
            GetMessageInfo execMessage = new GetMessageInfo("CCP", "EXEC", "BRXX", 1000, action, null, null);
            return objectMapper.writeValueAsString(execMessage);
        } catch (IOException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    private static void sendCommand(String command) throws IOException {
        byte[] sendBuffer = command.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, ccpAddress, ccpPort);
        socket.send(sendPacket);
    }

    private static String receiveResponse() throws IOException {
        byte[] receiveBuffer = new byte[bufferSize];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(receivePacket);
        return new String(receivePacket.getData(), 0, receivePacket.getLength());
    }
}