package kr.ac.kopo.cjj.trpgtest.cotroller;

import kr.ac.kopo.cjj.trpgtest.FlaskWebSocketClient2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class WebSocketMessageController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void handleChat(@Payload Map<String, Object> payload) throws Exception {
        URI uri = new URI("ws://127.0.0.1:8000/ws");

        FlaskWebSocketClient2 client = new FlaskWebSocketClient2(uri, payload, msg -> {
            messagingTemplate.convertAndSend("/topic/chat", msg);
        });

        client.connectBlocking();
        client.getResponseFuture().get(60, TimeUnit.SECONDS);
    }
}

