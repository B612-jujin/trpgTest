package kr.ac.kopo.cjj.trpgtest;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class FlaskWebSocketClient3 extends WebSocketClient {

    private final Map<String, Object> requestData;
    private String audioBase64;
    private final CountDownLatch latch = new CountDownLatch(1);

    public FlaskWebSocketClient3(URI serverUri, Map<String, Object> requestData) {
        super(serverUri);
        this.requestData = requestData;
        System.out.println("📡 WebSocketClient created with URI: " + serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("🔓 WebSocket opened: " + handshake.getHttpStatusMessage());
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(requestData);
            System.out.println("📤 Sending request data: " + json);
            send(json);
        } catch (Exception e) {
            System.err.println("❌ Error during onOpen: " + e.getMessage());
            e.printStackTrace();
            latch.countDown();
        }
    }

    @Override
    public void onMessage(String message) {
        System.out.println("📥 Received message: " + message);
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<?, ?> response = mapper.readValue(message, Map.class);

            if ("success".equals(response.get("status"))) {
                this.audioBase64 = (String) response.get("audio");
                System.out.println("✅ Audio data received successfully.");
            } else {
                System.out.println("⚠️ Response received but status != success");
            }

            latch.countDown();
        } catch (Exception e) {
            System.err.println("❌ Error during onMessage: " + e.getMessage());
            e.printStackTrace();
            latch.countDown();
        }
    }

    public String getAudioBase64() throws InterruptedException {
        System.out.println("⏳ Waiting for audio data...");
        latch.await();  // 응답이 올 때까지 대기
        System.out.println("🔚 Audio data received, returning.");
        return audioBase64;
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("🔒 WebSocket closed. Code: " + code + ", Reason: " + reason + ", Remote: " + remote);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("💥 WebSocket error: " + ex.getMessage());
        ex.printStackTrace();
        latch.countDown();
    }
}
