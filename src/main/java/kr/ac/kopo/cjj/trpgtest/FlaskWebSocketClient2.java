package kr.ac.kopo.cjj.trpgtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FlaskWebSocketClient2 extends WebSocketClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CompletableFuture<String> responseFuture = new CompletableFuture<>();
    private final Map<String, Object> sendData;

    public FlaskWebSocketClient2(URI serverUri, Map<String, Object> sendData) {
        super(serverUri);
        this.sendData = sendData;
    }

    public CompletableFuture<String> getResponseFuture() {
        return responseFuture;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to Flask WebSocket server");
        try {
            String jsonString = objectMapper.writeValueAsString(sendData);
            send(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            responseFuture.completeExceptionally(e);
        }
    }

    @Override
    public void onMessage(String message) {
        try {
            Map response = objectMapper.readValue(message, Map.class);
            System.out.println("Received JSON FL: " + response);

            // "done": true가 있는 경우에만 future 완료
            Object doneObj = response.get("done");
            boolean isDone = doneObj instanceof Boolean && (Boolean) doneObj;

            if (isDone) {
                responseFuture.complete(message); // 최종 결과만 전달
            } else {
                // 중간 응답은 로깅만 하거나 별도 처리 가능
                System.out.println("중간 응답 수신 (보류): " + message);
            }

        } catch (Exception e) {
            System.out.println("Invalid JSON received FL: " + message);
            responseFuture.completeExceptionally(e);
        }
    }


    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (!responseFuture.isDone()) {
            responseFuture.complete("연결 종료됨: " + reason);
        }
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        if (!responseFuture.isDone()) {
            responseFuture.completeExceptionally(ex);
        }
    }
}
