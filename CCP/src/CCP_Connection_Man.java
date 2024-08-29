/*
 * Needs to connect to the MCP successfully
 * 
 * BUT also needs to receive information from
 * ESP32??? to then provide information to
 * Offboard CCP?
 */

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class CCP_Connection_Man {

    CCP_Client client;

    public CCP_Connection_Man() {

    }

    public void setup() throws SocketException, UnknownHostException {
        new CCP_Client().start();
        client = new CCP_Client();
    }

    public void send_to_MCP(String Message) throws IOException {
        client.sendEcho(Message);
    }

    public void end_connection() throws IOException {
        client.sendEcho("end");
        client.close();
    }
}
