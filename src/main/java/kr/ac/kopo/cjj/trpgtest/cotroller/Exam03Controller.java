package kr.ac.kopo.cjj.trpgtest.cotroller;

import kr.ac.kopo.cjj.trpgtest.FlaskWebSocketClient3;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AudioController {

    @GetMapping("/exam01")
    public String getAudio(Model model) {
        try {
            URI uri = new URI("ws://localhost:8000/ws");

            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "generate_tts");
            payload.put("message", "안녕하세요. 이것은 테스트 음성입니다.");
            payload.put("media_type", "mp3");

            FlaskWebSocketClient3 client = new FlaskWebSocketClient3(uri, payload);
            client.connectBlocking();  // 동기 연결
            Thread.sleep(1000); // 응답 대기 (임시)

            String base64Audio = client.getAudioBase64();
            model.addAttribute("audioData", base64Audio);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "TTS 생성 실패: " + e.getMessage());
        }

        return "exam03";
    }
}
