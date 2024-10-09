package ccp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Server {

    private static InetAddress espAddress, mcpAddress;
    private static String status;
    private static int mcpPort, espPort;
    private static PacketManager packetManager;
    private final Set<String> expectedEspCommands;
    private State currentState;

    public static int ccpPort = 4210;

    public Server(int port) throws IOException {
        packetManager = new PacketManager(port);
        currentState = State.INITIALISING;
        mcpAddress = InetAddress.getByName("192.168.0.103");
        mcpPort = 4000;
        status = "ERR";
        expectedEspCommands = Set.of("STOPC", "STOPO", "FFASTC", "OFLN");
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
                try {
                    DatagramPacket receivePacket = packetManager.receivePacket();
                    String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

                    if (receivePacket.getPort() == 4000) {
                        System.out.println("MCP said: " + receivedMessage);
                    } else {
                        System.out.println("ESP said: " + receivedMessage);
                    }

                    switch (currentState) {
                        case INITIALISING ->
                            handleInitialisingState(receivedMessage, receivePacket);
                        case RUNNING ->
                            handleRunningState(receivedMessage, receivePacket);
                        default ->
                            System.out.println("Tried to go into Unknown State");
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("No packet received");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Shutting down...");
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
    private synchronized void handleInitialisingState(String message, DatagramPacket packet) throws IOException {
        if (message.contains("(INIT) from ESP")) {
            String akinMessage = "";
            // Get ESP IP and Port and then send back an INIT acknowledgement
            espAddress = packet.getAddress();
            espPort = packet.getPort();
            System.out.println("Initialization message received from ESP32.");

            packetManager.sendPacket("(INIT Confirmed) from CCP", espAddress, espPort); // Doesnt need another ACK
            System.out.println("Sent initialization confirmation to ESP32.");

            // Now tell MCP this information and then wait for the AKIN packet from MCP
            akinMessage = sendWithRetries(GenerateMessage.generateInitiationMessage(), mcpAddress, mcpPort);

            if (!akinMessage.isEmpty() && akinMessage.contains("AKIN")) {
                status = "STOPC"; // FIX
                currentState = State.RUNNING;
            }
        }
    }

    /*
     * Running state - Main loop that will check for several commands from MCP and
     * some packets from ESP.
     */
    private synchronized void handleRunningState(String message, DatagramPacket packet) throws IOException {
        if (message.contains("STRQ")) {
            String m = GenerateMessage.generateStatusMessage(status);
            packetManager.sendPacket(m, mcpAddress, mcpPort);
            System.out.println("Sent response: " + m);
        } else if (message.contains("EXEC")) {
            handleExecuteCommand(message);
        } else if (packet.getAddress().equals(espAddress) && packet.getPort() == espPort) {
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
    private synchronized void handleExecuteCommand(String message) throws IOException {
        // AKEK that initial packet was received
        packetManager.sendPacket(GenerateMessage.generateAckMessage(), mcpAddress, mcpPort);

        // Pull a part message and then send it to ESP based on the several commands
        String actionString = extractAction(message); // This is not tested so could be wrong
        int retryCount = 0;
        int maxCount = 10;
        boolean received = false;

        while (!received && retryCount < maxCount) {
            packetManager.sendPacket(actionString, espAddress, espPort);
            System.out.println("Sent response to ESP: " + actionString);

            // Wait for a response from ESP to say that it has executed said command
            // DatagramPacket espPacketACK = packetManager.receivePacket();
            // String ackMessage = validatePacket(espPacketACK, espAddress);
            String espPacketAck = validatePacket(espAddress);

            // String ackMessage = new String(espPacketACK.getData(), 0,
            // espPacketACK.getLength());
            if (!espPacketAck.isEmpty()) {
                received = true;
                System.out.println("ESP SAID: " + espPacketAck);
                if (espPacketAck.contains("OFLN")) {
                    packetManager.sendPacket("OFLN", mcpAddress, mcpPort);
                    currentState = State.QUIT;
                } else {
                    for (String s : expectedEspCommands) {
                        if (espPacketAck.contains(s)) {
                            String statUpdate = GenerateMessage.generateStatusMessage(s);
                            packetManager.sendPacket(statUpdate, mcpAddress, mcpPort);
                            System.out.println("Sent ACK response of new status to MCP: " + s);
                        }
                    }
                }
            } else {
                retryCount++;
                System.out.println("espPacketAck not found, trying again");
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (!received) {
            System.out.println("Failed... retrying");
        }
    }

    private String validatePacket(InetAddress expectedAddress) throws IOException {
        int retryCount = 0;
        int maxCount = 10;

        while (retryCount < maxCount) {
            try {
                DatagramPacket packet = packetManager.receivePacket();
                if (packet.getAddress().equals(expectedAddress)) {
                    return new String(packet.getData(), 0, packet.getLength());
                }
            } catch (SocketTimeoutException e) {
                System.out.println("No packet received");
            }
            retryCount++;

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return "";
    }

    private String sendWithRetries(String message, InetAddress address, int port) throws IOException {
        int retryCount = 0;
        int maxCount = 5;
        boolean received = false;
        String response = "";

        while (!received && retryCount < maxCount) {
            packetManager.sendPacket(message, address, port);
            System.out.println("Sent message: " + message + " to " + address.getHostAddress() + " on port " + port);

            try {
                DatagramPacket packet = packetManager.receivePacket();
                if (packet.getAddress().equals(address)) {
                    response = new String(packet.getData(), 0, packet.getLength());
                    received = true;
                }
            } catch (SocketTimeoutException e) {
                System.out.println("Timed out");
            }

            retryCount++;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (!received) {
            System.out.println("Failed to receive response");
        }

        return response;
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