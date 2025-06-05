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
    public String doTTS(@RequestParam String text, Model model) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("text", text);
            data.put("text_lang", "ko");
            data.put("ref_audio_path", "A-A3-E-055-0101.wav");
            data.put("prompt_lang", "ko");
            data.put("prompt_text", "지금이 범인을 찾을 땐가요, 아버지라면 당연히 생사를 오가는 딸 곁에 있어 주셔야죠!");
            data.put("media_type", "wav");

            ObjectMapper mapper = new ObjectMapper();
            String jsonPayload = mapper.writeValueAsString(data);

            URI uri = new URI("ws://localhost:8000/ws");
            MyWebSocketClient client = new MyWebSocketClient(uri, jsonPayload);
            String resultJson = client.sendAndReceive().get(); // 블로킹

            // 결과를 맵으로 파싱
            Map result = mapper.readValue(resultJson, Map.class);

            model.addAttribute("recv_text", result.get("recv_text"));
            model.addAttribute("audio_url", result.get("audio_url"));
            model.addAttribute("prompt", result.get("prompt"));
        } catch (Exception e) {
            model.addAttribute("recv_text", "오류: " + e.getMessage());
        }
        return "exam03";
    }
}
