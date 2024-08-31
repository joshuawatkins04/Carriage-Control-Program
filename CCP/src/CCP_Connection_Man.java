import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class CCP_Connection_Man {

    CCP_Client client;

    public CCP_Connection_Man() {}

    public void setup() throws SocketException, UnknownHostException {
        client = new CCP_Client();
    }

    // public void send_to_MCP(String Message) throws IOException {
    //     client.sendPacket(Message);
    // }

    public void send_command1() throws IOException {
        client.command1();
    }

    public void receive_message() throws IOException {
        client.receiveMessage();
    }

    public void end_connection() throws IOException {
        client.sendPacket("END");
        client.close();
    }
}
