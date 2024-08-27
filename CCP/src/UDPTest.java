
import java.io.IOException;
import java.net.*;

public class UDPTest {
    
    CCP_TEST_CLIENT client;

    public void setup() throws SocketException, UnknownHostException {
        new CCP_TEST_CLIENT().start();
        client = new CCP_TEST_CLIENT();
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
