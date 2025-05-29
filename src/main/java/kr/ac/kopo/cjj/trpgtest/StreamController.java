package kr.ac.kopo.cjj.trpgtest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
public class StreamController {

    private SseEmitter emitter;

    @GetMapping("/stream")
    public SseEmitter stream() {
        emitter = new SseEmitter(0L); // 타임아웃 없음
        return emitter;
    }

    // FlaskWebSocketClient2에서 이 메서드를 통해 메시지를 브라우저로 보냄
    public void sendToClient(String message) {
        if (emitter != null) {
            try {
                emitter.send(message);
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        }
    }
}
