package ccp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {
    
    private final DatagramSocket clientSocket;
    private final InetAddress IPAddress;
    private byte[] buf;
    DatagramPacket sendPacket, receivePacket;

    public Client() throws SocketException, UnknownHostException {
        clientSocket = new DatagramSocket();
        IPAddress = InetAddress.getByName("localhost");
    }

    public void sendPacket(String message) {
        buf = message.getBytes();
        sendPacket = new DatagramPacket(buf, buf.length, IPAddress, 2001);
        try {
            clientSocket.send(sendPacket);
        } catch (IOException e) {
            System.out.println("Failed to send packet." + e);
        }
    }

    public String receiveMessage() {
        GetMessageInfo message = new GetMessageInfo();

        receivePacket = new DatagramPacket(buf, buf.length);
        try {
            clientSocket.receive(receivePacket);
            String received = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
            message.getMessage();
            return message.toString();
            //System.out.println("ACK Message received from MCP: " + received);
        } catch (IOException e) {
            System.out.println("Failed to receive packet." + e);
            return null;
        }
    }

    // Closes socket
    public void close() {
        clientSocket.close();
    }
}
