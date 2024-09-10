package ccp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Timestamp;

class udptoESP {
  private DatagramSocket socket;
  private byte[] buf = new byte[256];
  private InetAddress address;

  public udptoESP() throws SocketException, UnknownHostException {
      socket = new DatagramSocket();
      address = InetAddress.getByName("localhost");
  }

  String read() throws IOException{
      DatagramPacket packet = new DatagramPacket(buf, buf.length);
      socket.receive(packet);
      String received = new String(packet.getData(), 0, packet.getLength());
      return received; 
  }

  void write(String msg) throws IOException{
      buf = msg.getBytes();
      DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
      socket.send(packet);
  }

  public void close() {
      socket.close();
  }
}


public class TestServer extends Thread {
    
  private static DatagramSocket serverSocket;
  private boolean running;
  private byte[] buf;
  DatagramPacket receivePacket, sendPacket;

  udptoESP client;

  public static void main(String[] args) throws SocketException {
    serverSocket = new DatagramSocket(3016);
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
        //System.out.println(serverSocket.getPort());
        buf = new byte[256];
        receivePacket = new DatagramPacket(buf, buf.length);
        serverSocket.receive(receivePacket); 
                
        InetAddress IPAddress = receivePacket.getAddress();
        int port = receivePacket.getPort();
        receivePacket = new DatagramPacket(buf, buf.length, IPAddress, port);
        //String receivedMessage = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
        String receivedMessage = client.read();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    
        System.out.println("[" + 
            timestamp.toString() + ", IP: " + 
            IPAddress + ", Port: " + 
            port + "] Message: " + 
            receivedMessage
        );

        
        client.write("START");

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
