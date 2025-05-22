package kr.ac.kopo.cjj.trpgtest;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class FlaskWebSocketClient extends WebSocketClient {


    public FlaskWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("Connected to Flask WebSocket server");
        send("Hello from Spring Boot!");

    }

    @Override
    public void onMessage(String s) {
        System.out.println("Received from Flask: " + s);

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

}
