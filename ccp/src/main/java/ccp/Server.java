package ccp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Server {

    private static InetAddress espAddress, mcpAddress;
    private static String status;
    private static int ccpPort, mcpPort, espPort;
    private final PacketManager packetManager;
    private final Set<String> espCommands;
    private State currentState;

    public Server(int port) throws IOException {
        packetManager = new PacketManager(port);
        currentState = State.INITIALISING;
        mcpAddress = InetAddress.getByName("192.168.0.103");
        ccpPort = 4210;
        mcpPort = 4000;
        status = "ERR";
        espCommands = Set.of("STOPC", "STOPO", "FFASTC", "OFLN");
    }

    public static void main(String[] args) {
        try {
            Server server = new Server(ccpPort);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {

        System.out.print("\033[H\033[2J");
        System.out.flush();

        try {
            System.out.println("Server listening on port: " + ccpPort);

            /* Main Server loop */
            while (currentState != State.QUIT) {

                DatagramPacket receivePacket = packetManager.receivePacket();
                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

                System.out.println("Received message: " + receivedMessage);

                switch (currentState) {
                    case INITIALISING ->
                        handleInitialisingState(receivedMessage, receivePacket);
                    case RUNNING ->
                        handleRunningState(receivedMessage, receivePacket);
                    default ->
                        System.out.println("Tried to go into Unknown State");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            packetManager.close();
        }
    }

    private enum State {
        INITIALISING,
        RUNNING,
        QUIT
    }

    /*
     * Initialising state - Will wait for a packet from ESP containing INIT
     * and then sending Initiation message to MCP and then waiting for a
     * AKIN response from MCP before entering RUNNING state
     */
    private void handleInitialisingState(String message, DatagramPacket packet) throws IOException {
        if (message.contains("(INIT) from ESP")) {
            // Get ESP IP and Port and then send back an INIT acknowledgement
            espAddress = packet.getAddress();
            espPort = packet.getPort();
            System.out.println("Initialization message received from ESP32.");
            packetManager.sendPacket("(INIT Confirmed) from CCP", espAddress, espPort);
            System.out.println("Sent initialization confirmation to ESP32.");

            // Now tell MCP this information and then wait for the AKIN packet from MCP
            packetManager.sendPacket(GenerateMessage.generateInitiationMessage(), mcpAddress, mcpPort);
            DatagramPacket mcpPacketACK = packetManager.receivePacket();
            String akinMessage = new String(mcpPacketACK.getData(), 0, mcpPacketACK.getLength());
            if (akinMessage.contains("AKIN")) {
                status = "STOPC"; // Default state. not good though because doors could be open
                currentState = State.RUNNING;
            }
        }
    }

    /*
     * Running state - Main loop that will check for several commands from MCP and
     * some packets from ESP.
     */
    private void handleRunningState(String message, DatagramPacket packet) throws IOException {
        // Check if MCP is requesting STAT message
        if (message.contains("STRQ")) {
            packetManager.sendPacket(GenerateMessage.generateStatusMessage(status), mcpAddress, mcpPort);
        } else if (message.contains("EXEC")) {
            handleExecuteCommand(message);
        } /*
         * The ESP will only ever need to send a message directly to CCP when it changes status
         * for an unexpected reason such as a case where it has to emergency stop and its status changes
         */ else if (packet.getPort() == espPort) {
            // Maybe include something to handle ESP sending a "Emergency Stop" command
            // maybe?
            // Update status message to MCP
            if (message.contains("EMERGENCY STOP")) {
                status = "STOPC";
                packetManager.sendPacket(GenerateMessage.generateStatusMessage(status), mcpAddress, mcpPort);
            }
        }
    }

    /*
     * If from MCP and it has any of the commands do following:
     * - Send the command packet to ESP
     * - Wait for ACK from ESP that it has executed that command
     * - Send back an ACK to MCP
     */
    private void handleExecuteCommand(String message) throws IOException {
        // AKEK that initial packet was received
        packetManager.sendPacket(GenerateMessage.generateAckMessage(), mcpAddress, mcpPort);

        // Pull a part message and then send it to ESP based on the several commands
        String actionString = extractAction(message); // This is not tested so could be wrong

        // Handle disconnect message
        if (actionString.contains("DISCONNECT")) {
            currentState = State.QUIT;
        } else {
            // sendToEsp = handleMcpCommand(actionString);
            packetManager.sendPacket(actionString, espAddress, espPort);
            System.out.println("Sent response: " + actionString);

            // Wait for a response from ESP to say that it has executed said command
            DatagramPacket espPacketACK = packetManager.receivePacket();
            String ackMessage = new String(espPacketACK.getData(), 0, espPacketACK.getLength());
            for (String s : espCommands) {
                if (ackMessage.contains(s)) {
                    packetManager.sendPacket(GenerateMessage.generateStatusMessage(s), mcpAddress, mcpPort);
                }
            }
        }
    }

    private static String extractAction(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            GetMessageInfo info = objectMapper.readValue(message, GetMessageInfo.class);
            return info.getAction();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Error with finding action";
        }
    }
}
