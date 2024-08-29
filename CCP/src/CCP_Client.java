import java.io.IOException;
import java.net.*;

/*
 * Test CCP client that will set up a connection between MCP and CCP and then has a 
 * function to send UDP messages
 * 
 * Will also need to set up a function to manage requests from the MCP
 */
public class CCP_Client {
    
    private final DatagramSocket socket;
    private final InetAddress address;
    private byte[] buf;

    public CCP_Client() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");
    }

    // Starts server
    public void start() throws SocketException {
        MCP_SERVER server = new MCP_SERVER();
        server.start();
    }

    // Send UPD packet as a String. Needs to change so it sends JSON message
    public String sendEcho(String msg) throws IOException {
        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        socket.send(packet);
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());
        return received;
    }

    // Closes socket
    public void close() {
        socket.close();
    }
}
