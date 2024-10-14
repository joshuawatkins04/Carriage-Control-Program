package ccp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class PacketManager {

    private static final int bufferSize = 256;
    private static DatagramPacket receivePacket, responsePacket;
    private static DatagramSocket socket;

    public PacketManager(int port) throws IOException {
        socket = new DatagramSocket(port);
        socket.setSoTimeout(5000);
    }

    public DatagramPacket receivePacket() throws IOException {
        byte[] receiveBuffer = new byte[bufferSize];
        receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(receivePacket);
        return receivePacket;
    }

    public void sendPacket(String message, InetAddress address, int port)
            throws IOException {
        byte[] responseBuffer = message.getBytes();
        responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, address, port);
        socket.send(responsePacket);
    }

    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
