package ccp;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.Timestamp;

public class TestServer extends Thread {
    
  private static DatagramSocket serverSocket;
  private boolean running;
  private byte[] buf;
  DatagramPacket receivePacket, sendPacket;

  public static void main(String[] args) throws SocketException {
    serverSocket = new DatagramSocket(2001);
    new TestServer().run();
  }

  // Running the server to receive packets
  @Override
  public void run() {
    System.out.print("\033[H\033[2J");  
    System.out.flush();
    try {
      running = true;
      while (running) {
        buf = new byte[256];
        receivePacket = new DatagramPacket(buf, buf.length);
        serverSocket.receive(receivePacket); 
                
        InetAddress IPAddress = receivePacket.getAddress();
        int port = receivePacket.getPort();
        receivePacket = new DatagramPacket(buf, buf.length, IPAddress, port);
        String receivedMessage = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    
        System.out.println("[" + 
            timestamp.toString() + ", IP: " + 
            IPAddress + ", Port: " + 
            port + "] Message: " + 
            receivedMessage
        );

        sendPacket = new DatagramPacket(buf, buf.length, receivePacket.getAddress(), receivePacket.getPort());
        serverSocket.send(sendPacket);

        if (receivedMessage.contains("AKIN")) {
          System.out.println("AKIN received");
        }

        if (receivedMessage.contains("END")) {
            System.out.println("Told to 'end' so closing server.");
            running = false;
            serverSocket.close();
            continue;
        }
      }
    } catch (IOException e) {
        System.out.println(e);
    }
  }
}
