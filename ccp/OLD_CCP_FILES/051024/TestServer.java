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
  private int recieveport = 4445;
  private int sendport = 4446;

  private InetAddress senderaddress;
  private int senderport;

  public udptoESP() throws SocketException, UnknownHostException {
      socket = new DatagramSocket(recieveport);
      socket.setSoTimeout(1000);
      address = InetAddress.getByName("localhost"); // Destination address
  }

  String read() throws IOException{
      DatagramPacket packet = new DatagramPacket(buf, buf.length);
      try {
        
        socket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());
  
        senderaddress = packet.getAddress();
        senderport = packet.getPort();
  
        return received; 
      } catch (Exception e) {
        return null;
      }
  }

  InetAddress getIP() throws IOException{
    return senderaddress;
  }

  int getPort() throws IOException{
    return senderport;
  }

  void write(String msg) throws IOException{
      buf = msg.getBytes();
      DatagramPacket packet = new DatagramPacket(buf, buf.length, address, sendport);
      socket.send(packet);
  }

  public void close() {
      socket.close();
  }
}


public class TestServer extends Thread {
    
  private static DatagramSocket serverSocket;
  private boolean running;
  private byte[] buf = new byte[256];
  DatagramPacket receivePacket, sendPacket;

  udptoESP client;

  public static void main(String[] args) throws SocketException {
    new TestServer().run();
  }

  
  // Running the server to receive packets
  @Override
  public void run() {

    
    try {
      client = new udptoESP();
      running = true;
      while (running) {
        
        client.write("START");

        String msg = client.read();
        if (msg != null){
          
          Timestamp timestamp = new Timestamp(System.currentTimeMillis());
          System.out.println("[" + 
            timestamp.toString() + ", IP: " + 
            client.getIP() + ", Port: " + 
            client.getPort() + "] Message: " + 
            msg
          );

        

          if (msg.contains("AKIN")) {
            System.out.println("AKIN received");
          }

          if (msg.contains("END")) {
              System.out.println("Told to 'end' so closing server.");
              running = false;
              serverSocket.close();
              continue;
          }
        }
      }
    } catch (IOException e) {
        System.out.println(e);
    }
  }

  /*
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
    */
}
