package app.mereb.mereb_backend;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class MerebAppApplication {
    public static void main(String[] args) {
        log.info("Starting MerebAppApplication");
        log.warn("⚠️ TEST LOG — This should appear in logs");
        SpringApplication.run(MerebAppApplication.class, args);
    }

}
