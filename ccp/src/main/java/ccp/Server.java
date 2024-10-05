package ccp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Server {
    private static final int ccpPort = 4210, mcpPort = 4000;
    private static final int bufferSize = 256;
    private static DatagramPacket receivePacket, responsePacket;
    private static String receivedMessage, responseMessage;
    private static InetAddress espAddress, mcpAddress;
    private static int espPort;

    private enum State {
        INITIALISING,
        RUNNING,
        STOPPED,
        QUIT
    }

    public static void main(String[] args) throws UnknownHostException {
        
        DatagramSocket socket = null;
        State currentState = State.INITIALISING;
        mcpAddress = InetAddress.getByName("192.168.0.103");

        System.out.print("\033[H\033[2J");
        System.out.flush();

        try {
            socket = new DatagramSocket(ccpPort);
            System.out.println("Server listening on port: " + ccpPort);

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
                    System.out.println(akinMessage);

                } else if (currentState == State.RUNNING) {
                    responseMessage = handleCommand(receivedMessage);
                    sendPacket(socket, responseMessage, espAddress, espPort);
                    System.out.println("Sent response: " + responseMessage);
                } else if (currentState == State.STOPPED) {

                }
            }

        } catch (Exception e) {
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

    private static String handleCommand(String command) {
        switch (command) {
            case "COMMAND_1":
                return "Response for Command 1";
            case "COMMAND_2":
                return "Response for Command 2";
            case "COMMAND_3":
                return "Response for Command 3";
            default:
                return "Unknown command";
        }
    }
}