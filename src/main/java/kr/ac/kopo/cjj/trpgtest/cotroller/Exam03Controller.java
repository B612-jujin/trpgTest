package kr.ac.kopo.cjj.trpgtest.cotroller;

import kr.ac.kopo.cjj.trpgtest.FlaskWebSocketClient3;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Controller
public class Exam03Controller {

    @GetMapping("/tts")
    public String showForm() {
        return "tts_form"; // templates/tts_form.html
    }

    @PostMapping("/tts")
    public String handleTtsRequest(@RequestParam String message, Model model) {
        try {
            URI uri = new URI("ws://localhost:8000/ws"); // Flask WebSocket 주소

            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "generate_tts");
            payload.put("message", message);
            payload.put("media_type", "mp3");

            FlaskWebSocketClient3 client = new FlaskWebSocketClient3(uri, payload);
            client.connectBlocking();  // 동기 연결
            Thread.sleep(1000); // 잠깐 대기

            String base64Audio = client.getAudioBase64();
            if (base64Audio == null) {
                model.addAttribute("error", "TTS 응답을 받지 못했습니다.");
            } else {
                model.addAttribute("audioData", base64Audio);
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "TTS 처리 중 오류 발생: " + e.getMessage());
        }

        return "exam03"; // 같은 페이지로 응답
    }
}

