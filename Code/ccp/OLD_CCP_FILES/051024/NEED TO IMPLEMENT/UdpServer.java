package ccp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpServer {
    public static void main(String[] args) {
        int port = 4210;

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(port);
            System.out.println("Server listening on port: " + port);

            boolean initialized = false;

            while (true) {
                byte[] receiveBuffer = new byte[256];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                System.out.println("Waiting for a packet...");

                socket.receive(receivePacket);

                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Received message: " + receivedMessage);

                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                if (receivedMessage.equals("INIT from ESP") && !initialized) {
                    System.out.println("Initialization message received from ESP32.");

                    String initResponse = "INIT from Java";
                    byte[] responseBuffer = initResponse.getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, clientAddress, clientPort);
                    socket.send(responsePacket);
                    System.out.println("Sent initialization confirmation to ESP32.");
                    
                    initialized = true;
                } 
                else if (initialized) {
                    String responseMessage;

                    switch (receivedMessage) {
                        case "COMMAND_1":
                            responseMessage = "Response for Command 1";
                            break;
                        case "COMMAND_2":
                            responseMessage = "Response for Command 2";
                            break;
                        case "COMMAND_3":
                            responseMessage = "Response for Command 3";
                            break;
                        default:
                            responseMessage = "Unknown command";
                            break;
                    }

                    byte[] responseBuffer = responseMessage.getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, clientAddress, clientPort);
                    socket.send(responsePacket);
                    System.out.println("Sent response: " + responseMessage);
                } else {
                    System.out.println("Received unexpected message: " + receivedMessage);
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
}