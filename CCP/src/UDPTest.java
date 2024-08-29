
import java.io.IOException;
import java.net.*;

public class UDPTest {
    
    CCP_Client client;

    public void setup() throws SocketException, UnknownHostException {
        new CCP_Client().start();
        client = new CCP_Client();
    }

    public void testSend() throws IOException {
        System.out.println(client.sendEcho("hello server"));
        System.out.println(client.sendEcho("server is working"));
    }

    public void tearDown() throws IOException {
        System.out.println(client.sendEcho("end"));
        client.close();
    }
}
