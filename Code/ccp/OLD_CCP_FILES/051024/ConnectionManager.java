package ccp;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ConnectionManager {

    Client client;

    public void setup() throws SocketException, UnknownHostException {
        client = new Client();
    }

    public void handshake() throws IOException {
        client.sendPacket(GenerateMessage.generateHandshakeMessage(System.currentTimeMillis()));
    }
    public void send_status() throws IOException {
        client.sendPacket(GenerateMessage.generateStatusMessage(System.currentTimeMillis()));
    }
    public void send_at_station() throws IOException {
        client.sendPacket(GenerateMessage.generateStationStatusMessage(System.currentTimeMillis()));
    }

    public String receive_message() throws IOException {
        return client.receiveMessage();
    }

    public void end_connection() throws IOException {
        client.sendPacket("END");
        client.close();
    }
}
