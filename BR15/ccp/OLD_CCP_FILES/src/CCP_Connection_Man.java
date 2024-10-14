import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class CCP_Connection_Man {

    CCP_Client client;

    public CCP_Connection_Man() {}

    public void setup() throws SocketException, UnknownHostException {
        client = new CCP_Client();
    }

    public void handshake() throws IOException {
        client.handshake();
    }
    public void send_status() throws IOException {
        client.send_status();
    }
    public void send_at_station() throws IOException {
        client.send_at_station();
    }

    public void receive_message() throws IOException {
        client.receiveMessage();
    }

    public void end_connection() throws IOException {
        client.sendPacket("END");
        client.close();
    }
}
