package kr.ac.kopo.cjj.trpgtest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class FlaskWebSocketClient extends WebSocketClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CompletableFuture<String> responseFuture = new CompletableFuture<>();
    private final Map<String, Object> sendData;
    private final Consumer<String> messageCallback;

    public FlaskWebSocketClient(URI serverUri, Map<String, Object> sendData, Consumer<String> messageCallback) {
        super(serverUri);
        this.sendData = sendData;
        this.messageCallback = messageCallback;
    }

    public CompletableFuture<String> getResponseFuture() {
        return responseFuture;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        try {
            String jsonString = objectMapper.writeValueAsString(sendData);
            send(jsonString);
            System.out.println("[✅ 연결 후 메시지 전송]: " + jsonString);
        } catch (Exception e) {
            responseFuture.completeExceptionally(e);
        }
    }

    @Override
    public void onMessage(String message) {
        System.out.println("[📩 수신]: " + message);
        if (messageCallback != null) {
            messageCallback.accept(message);
        }
        try {
            JsonNode root = objectMapper.readTree(message);

            boolean isDone = root.has("done") && root.get("done").asBoolean();
            boolean hasImage = root.has("image");
            boolean hasAudio = root.has("audio");
            if (isDone || hasImage || hasAudio) {
                responseFuture.complete(message);
                System.out.printf("[✅ 완료]: %s, 이미지: %b, 오디오: %b%n", isDone, hasImage, hasAudio);
            }



        } catch (Exception e) {
            e.printStackTrace();
            responseFuture.completeExceptionally(e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("[⚠️ 연결 종료]: " + reason);
        if (!responseFuture.isDone()) {
            responseFuture.completeExceptionally(new RuntimeException("WebSocket closed prematurely."));
        }
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("[❌ 에러]: " + ex.getMessage());
        ex.printStackTrace();
        if (!responseFuture.isDone()) {
            responseFuture.completeExceptionally(ex);
        }
    }
}