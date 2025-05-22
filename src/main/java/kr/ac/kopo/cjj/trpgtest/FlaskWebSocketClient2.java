package kr.ac.kopo.cjj.trpgtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class FlaskWebSocketClient extends WebSocketClient {


    private final ObjectMapper objectMapper = new ObjectMapper();

    public FlaskWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to Flask WebSocket server");

        try {
            Map<String, Object> data = new HashMap<>();
            data.put("type", "greeting");
            data.put("message", "자바 설명좀 해줘");
            String jsonString = objectMapper.writeValueAsString(data);
            send(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String message) {
        try {
            Map response = objectMapper.readValue(message, Map.class);
            System.out.println("Received JSON FL: " + response);
        } catch (Exception e) {
            System.out.println("Invalid JSON received FL: " + message);
        }
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
