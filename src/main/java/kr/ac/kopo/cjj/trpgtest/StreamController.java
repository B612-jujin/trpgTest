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
        this.emitter = new SseEmitter(0L); // 타임아웃 없음
        return this.emitter;
    }

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
