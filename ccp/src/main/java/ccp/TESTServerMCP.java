package ccp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TESTServerMCP {

    private static final int bufferSize = 1024;
    private static final int mcpPort = 4000;
    private static final String mcpAddress = "192.168.0.37";
    private static final int serverPort = 4210;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        DatagramSocket socket = null;

        System.out.print("\033[H\033[2J");
        System.out.flush();
        try {
            socket = new DatagramSocket(mcpPort);
            InetAddress serverAddress = InetAddress.getByName(mcpAddress);

            while (true) {
                // Receive packet from Server
                DatagramPacket receivedPacket = receivePacket(socket);
                String receivedMessage = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                System.out.println("Received message from Server: " + receivedMessage);

                if (receivedMessage.equals("STOPC")) {
                    System.out.println("Received packet that says, " + receivedMessage);
                } else if (receivedMessage.equals("ERR")) {
                    System.out.println("Received packet that says, " + receivedMessage);
                } else {
                    // Parse the received message to GetMessageInfo object
                    GetMessageInfo messageInfo;
                    try {
                        messageInfo = objectMapper.readValue(receivedMessage, GetMessageInfo.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                
                
                // Create a basic response based on the message
                String responseMessage = generateResponse(messageInfo);

                // Send response back to Server
                sendPacket(socket, responseMessage, serverAddress, serverPort);
                
                System.out.println("Sent response to Server: " + responseMessage);
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

    private static String generateResponse(GetMessageInfo messageInfo) {
        // Simple response logic based on the received message
        if (messageInfo.getMessage().equals("CCIN")) {
            messageInfo.setMessage("AKIN");
            return convertToJson(messageInfo);
        } else {
            return "UNKNOWN_COMMAND";
        }
    }

    private static String convertToJson(GetMessageInfo message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static DatagramPacket receivePacket(DatagramSocket socket) throws IOException {
        byte[] receiveBuffer = new byte[bufferSize];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(receivePacket);
        return receivePacket;
    }

    private static void sendPacket(DatagramSocket socket, String message, InetAddress address, int port)
            throws IOException {
        byte[] responseBuffer = message.getBytes();
        DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, address, port);
        socket.send(responsePacket);
    }
}

