package kr.ac.kopo.cjj.trpgtest;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
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
        future.complete(message);
        this.close();
    }

    @Override public void onOpen(ServerHandshake handshake) {}
    @Override public void onClose(int code, String reason, boolean remote) {}
    @Override public void onError(Exception ex) {
        future.completeExceptionally(ex);
    }
}
