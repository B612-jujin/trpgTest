package kr.ac.kopo.cjj.trpgtest.cotroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.kopo.cjj.trpgtest.MyWebSocketClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String handleTTS(@RequestParam String text, Model model) {
        try {
            // 1. JSON 데이터 준비
            Map<String, Object> data = new HashMap<>();
            data.put("text", text);
            data.put("text_lang", "ko");
            data.put("ref_audio_path", "A-A3-E-055-0101.wav");
            data.put("prompt_lang", "ko");
            data.put("prompt_text", "지금이 범인을 찾을 땐가요, 아버지라면 당연히 생사를 오가는 딸 곁에 있어 주셔야죠!");
            data.put("media_type", "wav");

            ObjectMapper mapper = new ObjectMapper();
            String jsonPayload = mapper.writeValueAsString(data);

            // 2. WebSocket으로 Flask에 전송
            URI uri = new URI("ws://localhost:8000/ws");
            MyWebSocketClient client = new MyWebSocketClient(uri, jsonPayload);
            String responseJson = client.sendAndReceive().get(); // 동기(blocking)

            // 3. JSON 응답 파싱
            Map<String, Object> result = mapper.readValue(responseJson, Map.class);

            // 4. 모델에 결과 추가 (HTML로 전달)
            model.addAttribute("recv_text", result.get("recv_text"));
            model.addAttribute("audio_url", result.get("audio_url"));
            model.addAttribute("prompt", result.get("prompt"));
        } catch (Exception e) {
            model.addAttribute("recv_text", "오류: " + e.getMessage());
        }
        return "exam03";
    }
}
