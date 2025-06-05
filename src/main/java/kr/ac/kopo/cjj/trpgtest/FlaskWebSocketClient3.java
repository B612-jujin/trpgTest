package kr.ac.kopo.cjj.trpgtest;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FlaskWebSocketClient3 extends WebSocketClient {

    private final Map<String, Object> requestData;
    private String audioBase64;
    private final CountDownLatch latch = new CountDownLatch(1);

    public FlaskWebSocketClient3(URI serverUri, Map<String, Object> requestData) {
        super(serverUri);
        this.requestData = requestData;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            send(mapper.writeValueAsString(requestData));
        } catch (Exception e) {
            e.printStackTrace();
            latch.countDown(); // 실패해도 대기 풀어줌
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

            latch.countDown();  // 응답 도착 시 대기 해제
        } catch (Exception e) {
            e.printStackTrace();
            latch.countDown();
        }
    }

    public String getAudioBase64() throws InterruptedException {
        latch.await();  // "응답이 올 때까지" 대기
        return audioBase64;
    }

    @Override public void onClose(int code, String reason, boolean remote) {}
    @Override public void onError(Exception ex) {
        latch.countDown();
    }
}
