package kr.ac.kopo.cjj.trpgtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MyWebSocketClient extends WebSocketClient {
    private final String payload;
    private final CompletableFuture<String> future = new CompletableFuture<>();

    public MyWebSocketClient(URI serverUri, String payload) {
        super(serverUri);
        this.payload = payload;
    }

    public CompletableFuture<String> sendAndReceive() throws InterruptedException {
        this.connectBlocking();
        this.send(payload);
        return future;
    }

    @Override
    public void onMessage(String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map result = mapper.readValue(message, Map.class);
            if ("tts_generated".equals(result.get("type"))) {
                future.complete(message); // 최종 결과만 반환
                this.close();
            }
            // 진행중 메시지 등은 무시
        } catch (Exception e) {
            future.completeExceptionally(e);
            this.close();
        }
    }

    @Override public void onOpen(ServerHandshake handshake) {}
    @Override public void onClose(int code, String reason, boolean remote) {}
    @Override public void onError(Exception ex) { future.completeExceptionally(ex); }
}