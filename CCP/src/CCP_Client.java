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
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class CCP_Client {
    
    private final DatagramSocket clientSocket;
    private final InetAddress IPAddress;
    private byte[] buf;
    private long timestamp;
    private String status, message, station_id;
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

    // Command: CCIN
    public void handshake() throws IOException {
        timestamp = System.currentTimeMillis() / 1000L;
        JSONObject obj = new JSONObject();
        obj.put("client_type", "ccp");
        obj.put("message", "CCIN");
        obj.put("client_id", "BRXX");
        obj.put("timestamp", "" + timestamp);
        message = JSONValue.toJSONString(obj);
        sendPacket(message);
    }

    // Command: STAT
    // When called, needs to grab status of blade runner
    // Status must be: STOPPED/STARTED/ON/OFF/ERR/CRASH
    public void send_status() throws IOException {
        timestamp = System.currentTimeMillis() / 1000L;
        status = "ON";
        JSONObject obj = new JSONObject();
        obj.put("client_type", "ccp");
        obj.put("message", "STAT");
        obj.put("client_id", "BRXX");
        obj.put("timestamp", "" + timestamp);
        obj.put("status", "" + status);
        message = JSONValue.toJSONString(obj);
        sendPacket(message);
    }

    // Command: STAT
    // Sends packet when at a station
    public void send_at_station() throws IOException {
        timestamp = System.currentTimeMillis() / 1000L;
        status = "STOPPED_AT_STATION";
        station_id = "STXX"; // NEED TO RETURN A VALUE HERE
        JSONObject obj = new JSONObject();
        obj.put("client_type", "ccp");
        obj.put("message", "STAT");
        obj.put("client_id", "BRXX");
        obj.put("timestamp", "" + timestamp);
        obj.put("status", "" + status);
        obj.put("station_id", "" + station_id);
        message = JSONValue.toJSONString(obj);
        sendPacket(message);
    }

    public void receiveMessage() throws IOException {
        receivePacket = new DatagramPacket(buf, buf.length);
        clientSocket.receive(receivePacket);
        String received = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
        System.out.println("ACK Message received from MCP: " + received);
    }

    // Closes socket
    public void close() {
        clientSocket.close();
    }
}
