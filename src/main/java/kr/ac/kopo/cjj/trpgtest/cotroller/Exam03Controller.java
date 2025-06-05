package kr.ac.kopo.cjj.trpgtest.cotroller;

import kr.ac.kopo.cjj.trpgtest.FlaskWebSocketClient3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(Exam03Controller.class);

    @GetMapping("/exam03")
    public String showForm() {
        logger.info("[GET] /exam03 요청 - 입력 폼 렌더링");
        return "exam03";
    }

    @PostMapping("/exam03")
    public String handleTtsRequest(@RequestParam String message, Model model) {
        logger.info("[POST] /exam03 요청 - message: {}", message);

        try {
            URI uri = new URI("ws://192.168.24.189:8000/ws");
            logger.debug("WebSocket URI 설정됨: {}", uri);

            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "generate_tts");
            payload.put("text", message);
            payload.put("text_lang", "ko");
            payload.put("media_type", "wav");
            payload.put("ref_audio_path", "A-A3-E-055-0101.wav");
            payload.put("prompt_lang", "ko");
            payload.put("prompt_text", "지금이 범인을 찾을 땐가요, 아버지라면 당연히 생사를 오가는 딸 곁에 있어 주셔야죠!");

            logger.debug("요청 Payload: {}", payload);

            FlaskWebSocketClient3 client = new FlaskWebSocketClient3(uri, payload);
            client.connectBlocking(); // 동기 연결
            logger.info("WebSocket 연결 성공");

            String base64Audio = client.getAudioBase64();

            if (base64Audio == null) {
                logger.warn("TTS 응답 없음: audioBase64 is null");
                model.addAttribute("error", "TTS 응답을 받지 못했습니다.");
            } else {
                logger.info("TTS 오디오 데이터 수신 성공 (base64 길이: {})", base64Audio.length());
                model.addAttribute("audioData", base64Audio);
            }

        } catch (Exception e) {
            logger.error("예외 발생: {}", e.getMessage(), e);
            model.addAttribute("error", "TTS 처리 중 오류 발생: " + e.getMessage());
        }

        return "exam03";
    }
}
