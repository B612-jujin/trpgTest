package kr.ac.kopo.cjj.trpgtest;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class ReactAppRunner implements CommandLineRunner {

    @Override
    public void run(String... args) {
        try {
            ProcessBuilder builder = new ProcessBuilder("npm", "start");
            builder.directory(new File("../frontend")); // React 프로젝트 디렉토리
            builder.inheritIO(); // 로그를 같이 출력
            builder.start();
            System.out.println("✅ React 앱 실행됨");
        } catch (IOException e) {
            System.err.println("❌ React 앱 실행 실패: " + e.getMessage());
        }
    }
}
