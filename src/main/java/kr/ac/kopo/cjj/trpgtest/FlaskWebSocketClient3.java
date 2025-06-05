package kr.ac.kopo.cjj.trpgtest;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FlaskWebSocketClient3 extends WebSocketClient {
    private String audioBase64;

    public FlaskWebSocketClient3(URI serverUri, Map<String, Object> requestData) {
        super(serverUri);
        this.requestData = requestData;
    }

    private Map<String, Object> requestData;

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            send(mapper.writeValueAsString(requestData));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<?, ?> response = mapper.readValue(message, Map.class);
            if ("success".equals(response.get("status"))) {
                this.audioBase64 = (String) response.get("audio");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAudioBase64() {
        return audioBase64;
    }

    @Override public void onClose(int code, String reason, boolean remote) {}
    @Override public void onError(Exception ex) {}
}

