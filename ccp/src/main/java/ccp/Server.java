package ccp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Server {
    
    private static final int ccpPort = 4210, mcpPort = 4000;
    private static final int bufferSize = 1024;
    private static DatagramPacket receivePacket, responsePacket;
    private static String receivedMessage, status, sendToEsp;
    private static InetAddress espAddress, mcpAddress;
    private static int espPort;
    private static State currentState;

    private enum State {
        INITIALISING,
        RUNNING,
        STOPPED,
        QUIT
    }

    public static void main(String[] args) throws UnknownHostException {

        currentState = State.INITIALISING;
        mcpAddress = InetAddress.getByName("192.168.0.103");
        status = "ERR";
        DatagramSocket socket = null;

        System.out.print("\033[H\033[2J");
        System.out.flush();

        try {
            socket = new DatagramSocket(ccpPort);
            System.out.println("Server listening on port: " + ccpPort);

            /* NO NEED FOR SCHEDULER. CHANGE SO THAT IT 
             * CHECKS FOR STRQ COMMAND OR SENDS STATUS
             * MESSAGE EVERYTIME BR STATUS CHANGES
             */

            while (currentState != State.QUIT) {
                receivePacket = receivePacket(socket);
                receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

                System.out.println("Received message: " + receivedMessage);

                if (currentState == State.INITIALISING && receivedMessage.contains("(INIT) from ESP")) {

                    // Get ESP IP and Port and then send back an INIT acknowledgement
                    espAddress = receivePacket.getAddress();
                    espPort = receivePacket.getPort();
                    System.out.println("Initialization message received from ESP32.");
                    sendPacket(socket, "(INIT Confirmed) from CCP", espAddress, espPort);
                    System.out.println("Sent initialization confirmation to ESP32.");

                    // Now tell MCP this information and then wait for the AKIN packet from MCP
                    sendPacket(socket, GenerateMessage.generateInitiationMessage(), mcpAddress, mcpPort);
                    DatagramPacket akinPacket = receivePacket(socket);
                    String akinMessage = new String(akinPacket.getData(), 0, akinPacket.getLength());
                    if (akinMessage.contains("AKIN")) {
                        status = "STOPC"; // Default state. not good though because doors could be open
                        currentState = State.RUNNING;
                    }

                } else if (currentState == State.RUNNING) {

                    // check if it is requesting STAT message
                    if (receivePacket.getPort() == mcpPort && receivedMessage.contains("STRQ")) {
                        // send stat message to MCP
                    }

                    // Determine whether its a message from ESP or MCP
                    else if (receivePacket.getPort() == mcpPort && receivedMessage.contains("EXEC")) {
                        
                        // MUST SEND A AKEX packet to MCP to tell
                        // it that packet was received for every
                        // MCP packet
                        // Also might not need the second part of if statement

                        sendPacket(socket, GenerateMessage.generateAckMessage(), mcpAddress, mcpPort);

                        // If from MCP and it has any of the commands do following:
                        // - Send the command packet to ESP
                        // - Wait for ACK from ESP that it has executed that command
                        // - Send back an ACK to MCP
                        String actionString = extractAction(receivedMessage);
                        sendToEsp = handleMcpCommand(actionString);
                        sendPacket(socket, sendToEsp, espAddress, espPort);
                        System.out.println("Sent response: " + sendToEsp);
                        
                        // NEED TO FINISH
                        // Code a wait for ACK from ESP
                        
                        // Then send this info back to MCP
                        // sendToMcp = GenerateMessage.gen

                    } else if (receivePacket.getPort() == espPort) {
                        
                        // Needs to check the status values and send
                        // this to MCP.
                        // Make a handleEspCommand() function with the 
                        // commands
                    }

                } else if (currentState == State.STOPPED) {

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    private static DatagramPacket receivePacket(DatagramSocket socket) throws IOException {
        byte[] receiveBuffer = new byte[bufferSize];
        receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(receivePacket);
        return receivePacket;
    }

    private static void sendPacket(DatagramSocket socket, String message, InetAddress address, int port)
            throws IOException {
        byte[] responseBuffer = message.getBytes();
        responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, address, port);
        socket.send(responsePacket);
    }

    private static String handleMcpCommand(String command) {
        return switch (command) {
            case "STOPC" -> "STOPC";
            case "STOPO" -> "STOPO";
            case "FSLOWC" -> "FSLOWC";
            case "FFASTC" -> "FFASTC";
            case "RSLOWC" -> "RSLOWC";
            case "DISCONNECT" -> "DISCONNECT";
            default -> "Unknown command";
        };
    }

    // Function extracts "STOPC" for example out of the JSON string
    // Will fix so it uses JSON Object mapper
    static String extractAction(String message) {
        String extractAfter = "action:";
        if (message.contains(extractAfter)) {
            String[] parts = message.split(extractAfter);
            if (parts.length > 1) {
                String remainder = parts[1].trim();
                int endIndex = remainder.indexOf(",");
                if (endIndex != -1) return remainder.substring(0, endIndex).trim();
                else return remainder.replace("}", "").trim();
            }
        }
        return "";
    }
}