package ccp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Server {

    private static final int ccpPort = 4210, mcpPort = 4000;
    private static final int bufferSize = 1024;
    private static DatagramPacket receivePacket, responsePacket;
    private static DatagramSocket socket;
    private static InetAddress espAddress, mcpAddress;
    private static String receivedMessage, status, sendToEsp;
    private static int espPort;
    private static Set<String> espCommands;
    private static State currentState;

    private enum State {
        INITIALISING,
        RUNNING,
        QUIT
    }

    public static void main(String[] args) throws UnknownHostException {

        mcpAddress = InetAddress.getByName("192.168.0.103");
        currentState = State.INITIALISING;
        status = "ERR";
        socket = null;
        espCommands = new HashSet<>();

        espCommands.add("STOPC");
        espCommands.add("STOPO");
        espCommands.add("FFASTC");
        espCommands.add("OFLN");

        System.out.print("\033[H\033[2J");
        System.out.flush();

        try {
            socket = new DatagramSocket(ccpPort);
            System.out.println("Server listening on port: " + ccpPort);

            /* Main Server loop */
            while (currentState != State.QUIT) {

                // Main loop that waits for a packet to be received, then acts according based
                // on the info
                receivePacket = receivePacket(socket);
                receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

                System.out.println("Received message: " + receivedMessage);

                /*
                 * Initialising state - Will wait for a packet from ESP containing INIT
                 * and then sending Initiation message to MCP and then waiting for a
                 * AKIN response from MCP before entering RUNNING state
                 */
                // if (currentState == State.INITIALISING && receivedMessage.contains("(INIT)
                // from ESP")) {
                switch (currentState) {
                    case INITIALISING:
                        if (receivedMessage.contains("(INIT) from ESP")) {
                            // Get ESP IP and Port and then send back an INIT acknowledgement
                            espAddress = receivePacket.getAddress();
                            espPort = receivePacket.getPort();
                            System.out.println("Initialization message received from ESP32.");
                            sendPacket(socket, "(INIT Confirmed) from CCP", espAddress, espPort);
                            System.out.println("Sent initialization confirmation to ESP32.");

                            // Now tell MCP this information and then wait for the AKIN packet from MCP
                            sendPacket(socket, GenerateMessage.generateInitiationMessage(), mcpAddress, mcpPort);
                            DatagramPacket mcpPacketACK = receivePacket(socket);
                            String akinMessage = new String(mcpPacketACK.getData(), 0, mcpPacketACK.getLength());
                            if (akinMessage.contains("AKIN")) {
                                status = "STOPC"; // Default state. not good though because doors could be open
                                currentState = State.RUNNING;
                            }

                        }

                        /*
                         * Running state - Main loop that will check for several commands from MCP and
                         * some packets from ESP.
                         */
                        // else if (currentState == State.RUNNING) {
                    case RUNNING:
                        // Check if MCP is requesting STAT message
                        if (receivedMessage.contains("STRQ")) {
                            sendPacket(socket, GenerateMessage.generateStatusMessage(status), mcpAddress, mcpPort);
                        }

                        /*
                         * If from MCP and it has any of the commands do following:
                         * - Send the command packet to ESP
                         * - Wait for ACK from ESP that it has executed that command
                         * - Send back an ACK to MCP
                         */
                        if (receivedMessage.contains("EXEC")) {

                            // AKEK that initial packet was received
                            sendPacket(socket, GenerateMessage.generateAckMessage(), mcpAddress, mcpPort);

                            // Pull a part message and then send it to ESP based on the several commands
                            String actionString = extractAction(receivedMessage); // This is not tested so could be
                                                                                  // wrong

                            // Handle disconnect message
                            if (actionString.contains("DISCONNECT")) {
                                currentState = State.QUIT;
                            }
                            // sendToEsp = handleMcpCommand(actionString);
                            sendPacket(socket, actionString, espAddress, espPort);
                            System.out.println("Sent response: " + actionString);

                            // Wait for a response from ESP to say that it has executed said command
                            DatagramPacket espPacketACK = receivePacket(socket);
                            String ackMessage = new String(espPacketACK.getData(), 0, espPacketACK.getLength());
                            for (String s : espCommands) {
                                if (ackMessage.contains(s)) {
                                    sendPacket(socket, GenerateMessage.generateStatusMessage(s), mcpAddress, mcpPort);
                                    break;
                                }
                            }
                        }
                        /*
                         * The ESP will only ever need to send a message directly to CCP when it changes
                         * status
                         * for an unexpected reason such as a case where it has to emergency stop and
                         * its status changes
                         */
                        else if (receivePacket.getPort() == espPort) {
                            // Maybe include something to handle ESP sending a "Emergency Stop" command
                            // maybe?
                            // Update status message to MCP
                            if (receivedMessage.contains("EMERGENCY STOP")) {
                                status = "STOPC";
                                sendPacket(socket, GenerateMessage.generateStatusMessage(status), mcpAddress, mcpPort);
                            }
                        }
                    default:
                        break;
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

    private static String extractAction(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            GetMessageInfo info = objectMapper.readValue(message, GetMessageInfo.class);
            return info.getAction();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error with finding action";
        }
    }

    // private static String handleMcpCommand(String command) {
    // return switch (command) {
    // case "STOPC" -> "STOPC";
    // case "STOPO" -> "STOPO";
    // case "FSLOWC" -> "FSLOWC";
    // case "FFASTC" -> "FFASTC";
    // case "RSLOWC" -> "RSLOWC";
    // case "DISCONNECT" -> "DISCONNECT";
    // default -> "Unknown command";
    // };
    // }

    // private static void handleMcpJson(String mcpMessage) {
    // ObjectMapper objectMapper = new ObjectMapper();
    // try {
    // GetMessageInfo info = objectMapper.readValue(mcpMessage,
    // GetMessageInfo.class);
    // String message = info.getMessage();
    // String action = info.getAction();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

    // Function extracts "STOPC" for example out of the JSON string
    // Will fix so it uses JSON Object mapper
    // static String extractAction(String message) {
    // String extractAfter = "action:";
    // if (message.contains(extractAfter)) {
    // String[] parts = message.split(extractAfter);
    // if (parts.length > 1) {
    // String remainder = parts[1].trim();
    // int endIndex = remainder.indexOf(",");
    // if (endIndex != -1) return remainder.substring(0, endIndex).trim();
    // else return remainder.replace("}", "").trim();
    // }
    // }
    // return "";
    // }
}