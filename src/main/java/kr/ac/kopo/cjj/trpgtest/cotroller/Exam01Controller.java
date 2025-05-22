package kr.ac.kopo.cjj.trpgtest.cotroller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.kopo.cjj.trpgtest.FlaskWebSocketClient;
import kr.ac.kopo.cjj.trpgtest.FlaskWebSocketClient2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class Exam01Controller {

    @GetMapping("/exam01")
    public String showForm() {
        return "exam01";
    }

    @PostMapping("/exam01")
    public String handleSubmit(@RequestParam String type,
                               @RequestParam String prompt,
                               @RequestParam(required = false) String negative_prompt,
                               @RequestParam(required = false) Integer width,
                               @RequestParam(required = false) Integer height,
                               @RequestParam(required = false) Integer steps,
                               @RequestParam(required = false) Double cfg_scale,
                               @RequestParam(required = false) String sampler_name,
                               @RequestParam(required = false) Long seed,
                               Model model) {
        try {
            URI uri = new URI("ws://192.168.24.189:8000/ws");

            Map<String, Object> data = new HashMap<>();
            data.put("type", type);
            data.put("prompt", prompt);

            Map<String, Object> params = new HashMap<>();
            if (negative_prompt != null) params.put("negative_prompt", negative_prompt);
            if (width != null) params.put("width", width);
            if (height != null) params.put("height", height);
            if (steps != null) params.put("steps", steps);
            if (cfg_scale != null) params.put("cfg_scale", cfg_scale);
            if (sampler_name != null) params.put("sampler_name", sampler_name);
            if (seed != null) params.put("seed", seed);

            data.put("params", params);

            FlaskWebSocketClient client = new FlaskWebSocketClient(uri, data);
            client.connect();

            String rawJson = client.getResponseFuture().get(10, TimeUnit.SECONDS); // 시간 여유 ↑

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(rawJson);
            model.addAttribute("response", root.toPrettyString());

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
        }

        return "exam01";
    }
}
