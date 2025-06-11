package kr.ac.kopo.cjj.trpgtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class FlaskWebSocketClient2 extends WebSocketClient {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CompletableFuture<String> responseFuture = new CompletableFuture<>();
    private final Map<String,Object> sendData;
    private final Consumer<String> messageCallback;

    public FlaskWebSocketClient2(URI uri, Map<String,Object> data, Consumer<String> cb) {
        super(uri);
        this.sendData = data;
        this.messageCallback = cb;
    }

    @Override
    public void onOpen(ServerHandshake hand) {
        try {
            send(objectMapper.writeValueAsString(sendData));
        } catch(Exception e) {
            responseFuture.completeExceptionally(e);
        }
    }

    @Override
    public void onMessage(String msg) {
        if(messageCallback != null) messageCallback.accept(msg);
        try {
            Map<?,?> resp = objectMapper.readValue(msg, Map.class);
            if(Boolean.TRUE.equals(resp.get("done"))) {
                responseFuture.complete(msg);
            }
        } catch(Exception e) {
            responseFuture.completeExceptionally(e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if(!responseFuture.isDone()){
            responseFuture.complete("closed:"+reason);
        }
    }

    @Override
    public void onError(Exception ex) {
        if(!responseFuture.isDone()){
            responseFuture.completeExceptionally(ex);
        }
    }

    public CompletableFuture<String> getResponseFuture() {
        return responseFuture;
    }
}
