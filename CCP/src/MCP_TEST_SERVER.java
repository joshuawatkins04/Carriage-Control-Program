import java.io.IOException;
import java.net.*;

/*
 * Test server that simulates the MCP receiving packets from the CCP
 */
public class MCP_TEST_SERVER extends Thread {
    
    private final DatagramSocket socket;
    private boolean running;
    private final byte[] buf = new byte[256];

    // Sets the socket with a specific port to receive UDP packets
    public MCP_TEST_SERVER() throws SocketException {
        socket = new DatagramSocket(4445);
    }

    // Running the server to receive packets
    @Override
    public void run() {
        try (socket) {
            running = true;
            
            while (running) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(packet);
                } catch (IOException e) {
                }
                
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                String received = new String(packet.getData(), 0, packet.getLength());
                
                if (received.equals("end")) {
                    running = false;
                    continue;
                }
                try {
                    socket.send(packet);
                } catch (IOException e) {
                }
            }
        }
    }
}
