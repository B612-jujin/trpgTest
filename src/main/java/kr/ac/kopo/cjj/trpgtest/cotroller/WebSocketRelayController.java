package kr.ac.kopo.cjj.trpgtest.cotroller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.kopo.cjj.trpgtest.FlaskWebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;
// WebSocketRelayController.java
@Controller
public class WebSocketRelayController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/relay")
    public void handleRelay(@Payload Map<String, Object> payload) throws Exception {
        URI uri = new URI("ws://192.168.26.165:8000/ws"); // Flask WebSocket 서버

        FlaskWebSocketClient relayClient = new FlaskWebSocketClient(uri, payload, msg -> {
            // Flask에서 오는 메시지를 React로 전달 (JSON 파싱 후 전달)
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(msg);
                messagingTemplate.convertAndSend("/topic/relay", json);
            } catch (Exception e) {
                System.err.println("JSON 파싱 오류: " + e.getMessage());
            }
        });

        relayClient.connectBlocking();
        relayClient.getResponseFuture().get(400, TimeUnit.SECONDS);
    }
}


