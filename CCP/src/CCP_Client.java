/*
 * Test CCP client that will set up a connection between MCP and CCP and then has a 
 * function to send UDP messages
 * 
 * Will also need to set up a function to manage requests from the MCP
 */

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.SocketException;
import java.sql.Timestamp;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.JSONArray;

public class CCP_Client {
    
    private final DatagramSocket clientSocket;
    private final InetAddress IPAddress;
    private byte[] buf;
    DatagramPacket sendPacket, receivePacket;

    public CCP_Client() throws SocketException, UnknownHostException {
        clientSocket = new DatagramSocket();
        IPAddress = InetAddress.getByName("localhost");
    }

    // Send UPD packet as a String. Needs to change so it sends JSON message
    public void sendPacket(String message) throws IOException {
        buf = message.getBytes();
        sendPacket = new DatagramPacket(buf, buf.length, IPAddress, 2001);
        clientSocket.send(sendPacket);
    }

    // Does handshake CCIN command
    public void command1() throws IOException {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        JSONObject obj = new JSONObject();
        obj.put("client_type", "ccp");
        obj.put("message", "CCIN");
        obj.put("client_id", "BRXX");
        obj.put("timestamp", timestamp);
        String message = JSONValue.toJSONString(obj);
        sendPacket(message);
    }

    public void receiveMessage() throws IOException {
        receivePacket = new DatagramPacket(buf, buf.length);
        clientSocket.receive(receivePacket);
        String received = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println("ACK Message received from MCP: " + received);
    }

    // Closes socket
    public void close() {
        clientSocket.close();
    }
}
