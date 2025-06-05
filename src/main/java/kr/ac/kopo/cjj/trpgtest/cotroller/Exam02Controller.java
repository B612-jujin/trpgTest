package kr.ac.kopo.cjj.trpgtest.cotroller;


import kr.ac.kopo.cjj.trpgtest.StreamController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.ac.kopo.cjj.trpgtest.FlaskWebSocketClient;
import kr.ac.kopo.cjj.trpgtest.FlaskWebSocketClient2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Controller
public class Exam02Controller {
    @Autowired
    private StreamController streamController;

    // 로그를 위한 Logger 객체 생성
    private static final Logger logger = LoggerFactory.getLogger(Exam02Controller.class);

    @GetMapping("/exam02")
    public String showForm() {
        return "exam02"; // templates/exam02.html
    }

    @PostMapping("/exam02")
    public String handleSubmit(@RequestParam String type,
                               @RequestParam String message,
                               Model model) {
        try {
            URI uri = new URI("ws://192.168.26.165:8000/ws");

            Map<String, Object> data = new HashMap<>();
            data.put("type", type);
            data.put("message", message);

            logger.debug("WebSocket 연결을 시도합니다. URI: {}", uri);

            FlaskWebSocketClient2 client = new FlaskWebSocketClient2(uri, data, streamController);
            client.connectBlocking();
            String rawJson = client.getResponseFuture().get(50, TimeUnit.SECONDS);
            System.out.println("Flask 응답 JSON: " + rawJson);
            logger.info("Flask 응답 JSON: {}", rawJson);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(rawJson);
            String aiResponse = root.path("response").asText();

            model.addAttribute("type", type);
            model.addAttribute("message", message);
            model.addAttribute("response", aiResponse);
            model.addAttribute("rawJson", rawJson);

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "exam02"; // 다시 폼 페이지로
    }

}
