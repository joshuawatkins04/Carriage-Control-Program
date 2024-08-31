/*
 * Test server that simulates the MCP receiving packets from the CCP
 */

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.Timestamp;

public class MCP_SERVER extends Thread {
    
  private static DatagramSocket serverSocket;
  private boolean running;
  private final byte[] buf = new byte[256];
  DatagramPacket receivePacket, sendPacket;

  public static void main(String[] args) throws SocketException {
    serverSocket = new DatagramSocket(2001);
    new MCP_SERVER().run();
  }

  // Running the server to receive packets
  @Override
  public void run() {
    try {
      receivePacket = new DatagramPacket(buf, buf.length);
      running = true;
      while (running) {
        serverSocket.receive(receivePacket); 
                
        InetAddress IPAddress = receivePacket.getAddress();
        int port = receivePacket.getPort();
        receivePacket = new DatagramPacket(buf, buf.length, IPAddress, port);
        String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    
        System.out.println("[" + 
            timestamp.toString() + ", IP: " + 
            IPAddress + ", Port: " + 
            port + "] Message: " + 
            receivedMessage
        );

        sendPacket = new DatagramPacket(buf, buf.length, receivePacket.getAddress(), receivePacket.getPort());
        serverSocket.send(sendPacket);

        if (receivedMessage.equals("END")) {
            System.out.println("Told to 'end' so closing server.");
            running = false;
            continue;
        }
      }
    } catch (IOException e) {
        System.out.println(e);
    }
  }
}
