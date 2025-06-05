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

    @GetMapping("/exam03")
    public String showForm() {
        return "exam03";
    }

    @PostMapping("/exam03")
    public String handleTtsRequest(@RequestParam String message, Model model) {
        try {
            URI uri = new URI("ws://192.168.24.189:8000/ws");  // Flask WebSocket 주소

            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "generate_tts");
            payload.put("text", message);
            payload.put("text_lang", "ko");
            payload.put("media_type", "wav");
            payload.put("ref_audio_path", "A-A3-E-055-0101.wav");
            payload.put("prompt_lang", "ko");
            payload.put("prompt_text", "지금이 범인을 찾을 땐가요, 아버지라면 당연히 생사를 오가는 딸 곁에 있어 주셔야죠!");

            FlaskWebSocketClient3 client = new FlaskWebSocketClient3(uri, payload);
            client.connectBlocking(); // 동기 연결

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

        return "exam03";
    }
}
