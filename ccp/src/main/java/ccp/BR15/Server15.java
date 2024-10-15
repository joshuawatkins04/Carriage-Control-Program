package ccp.BR15;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

public class Server15 {

    private static final String initExpectedResponse = "XXinitXX";
    private static final String execExpectedResponse = "XXexecXX";
    private static InetAddress espAddress, mcpAddress;
    private static String status;
    private static int mcpPort, espPort;
    private static PacketManager15 packetManager;
    private final Set<String> espCommands;
    private State currentState;

    public static int ccpPort = 3015;

    public Server15(int port) throws IOException {
        packetManager = new PacketManager15(port);
        currentState = State.INITIALISING;
        mcpAddress = InetAddress.getByName("10.20.30.143");
        espAddress = InetAddress.getByName("10.20.30.115");
        mcpPort = 2001;
        espPort = 4210;
        status = "ERR";
        espCommands = Set.of("STOPC", "STOPO", "FFASTC", "OFLN");
    }

    public static void main(String[] args) {
        try {
            Server15 server = new Server15(ccpPort); // choose run without mcp or with mcp
            server.start();
        } catch (IOException e) {
            System.out.println("[SERVER] MAJOR ERROR: Server failed to start");
        }
    }

    public void start() {

        System.out.print("\033[H\033[2J");
        System.out.flush();

        try {
            System.out.println("[SERVER] Listening on port: " + ccpPort);

            while (currentState != State.QUIT) {
                try {
                    DatagramPacket receivePacket = packetManager.receivePacket();
                    String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

                    if (receivePacket.getPort() == 2001) {
                        System.out.println("[SERVER] Received MCP message: " + receivedMessage);
                    } else {
                        System.out.println("[SERVER] Received ESP message: " + receivedMessage);
                    }

                    switch (currentState) {
                        case INITIALISING ->
                            handleInitialisingState(receivedMessage, receivePacket);
                        case RUNNING ->
                            handleRunningState(receivedMessage, receivePacket);
                        default ->
                            System.out.println("[SERVER] ERROR: Tried to go into Unknown State");
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("[SERVER] ERROR: SocketTimeoutException, No packet received");
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
    private void handleInitialisingState(String message, DatagramPacket packet) throws IOException {
        if (message.contains("EXEC_INIT")) {
            espAddress = packet.getAddress();
            espPort = packet.getPort();
            System.out.println("[SERVER] Received INIT from ESP32.");

            String espAckWithStat = attemptSendPacket("INIT_CONF", espAddress, espPort, initExpectedResponse, 10);

            if (!espAckWithStat.isEmpty()) {
                String akinMessage = attemptSendPacket(GenerateMessage15.generateInitiationMessage(), mcpAddress, mcpPort,
                        "AKIN", 10);

                if (!akinMessage.isEmpty()) {
                    status = espAckWithStat;
                    currentState = State.RUNNING;
                    System.out.println("\n[SERVER] SUCCESS: Moving into RUNNING state\n");
                } else {
                    System.out.println("[SERVER] ERROR: FAILED INITIALISATION");
                }
            }
        }
    }

    /*
     * Running state - Main loop that will check for several commands from MCP and
     * some packets from ESP.
     */
    private void handleRunningState(String message, DatagramPacket packet) throws IOException {
        if (message.contains("STAT")) {
            String stat = GenerateMessage15.generateStatusMessage(status);
            String mcpACKST = attemptSendPacket(stat, mcpAddress, mcpPort, "AKST", 10);

            if (!mcpACKST.isEmpty()) {
                System.out.println("[SERVER] Received AKST from MCP");
            } else {
                System.out.println("[SERVER] ERROR: Failed to receive AKST from MCP");
            }
        } else if (message.contains("EXEC")) {
            handleExecuteCommand(message);
        } else if (packet.getPort() == espPort && packet.getAddress().equals(espAddress)) {
            if (message.contains("EMERGENCY STOP")) {
                if (message.contains("STOPC")) {
                    status = "STOPC";
                } else if (message.contains("STOPO")) {
                    status = "STOPO";
                }
                packetManager.sendPacket(GenerateMessage15.generateStatusMessage(status), mcpAddress, mcpPort);
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
        packetManager.sendPacket(GenerateMessage15.generateAckMessage(), mcpAddress, mcpPort);

        String actionString = extractAction(message);
        String espResponse = attemptSendPacket(actionString, espAddress, espPort, execExpectedResponse, 10);

        if (!espResponse.isEmpty()) {
            if (espResponse.contains("OFLN")) {
                packetManager.sendPacket("OFLN", mcpAddress, mcpPort);
                currentState = State.QUIT;
            } else {
                System.out.println("[SERVER] Received ESP AKIN packet containing updated status: " + espResponse);
                for (String s : espCommands) {
                    if (espResponse.contains(s)) {
                        String statUpdate = GenerateMessage15.generateStatusMessage(s);
                        packetManager.sendPacket(statUpdate, mcpAddress, mcpPort); // STAT doesnt need ACK
                        System.out.println("[SERVER] Sent ACK response of new status to MCP. Status: " + s);
                        break;
                    }
                }
            }
        }
    }

    private String attemptSendPacket(String message, InetAddress address, int port, String expectedResponse,
            int maxCount) throws IOException {
        int retryCount = 0;
        boolean received = false;
        String response = "";

        while (!received && retryCount < maxCount) {
            packetManager.sendPacket(message, address, port);
            System.out.println("[Count: " + retryCount + " ] Send attempt for " + address + " with packet " + message);

            try {
                DatagramPacket packet = packetManager.receivePacket();
                if (packet != null && packet.getAddress().equals(address)) {
                    if (expectedResponse.equals("XXexecXX")) {
                        response = new String(packet.getData(), 0, packet.getLength());
                        received = true;
                    } else {
                        response = new String(packet.getData(), 0, packet.getLength());
                        if (expectedResponse.equals("XXinitXX")) {
                            for (String s : espCommands) {
                                if (response.equals(s)) {
                                    received = true;
                                }
                            }
                        } else if (response.contains(expectedResponse)) {
                            received = true;
                        }
                    }
                }
            } catch (SocketTimeoutException e) {
                System.out.println("[SERVER] ERROR: SocketTimeoutException No packet received");
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
            System.out.println("[SERVER] ERROR: Response not received after " + retryCount + " tries");
        }

        return response;
    }
    
    private static String extractAction(String message) {
        ObjectMapper objectMapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        
        try {
            GetMessageInfo15 info = objectMapper.readValue(message, GetMessageInfo15.class);
            return info.getAction();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Error with finding action";
        }
    }
}