package app.mereb.mereb_backend;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MerebAppApplication {

    @PostConstruct
    public void logEnvironment() {
        System.out.println("Active profile: " + System.getProperty("spring.profiles.active"));
    }

    public static void main(String[] args) {
        SpringApplication.run(MerebAppApplication.class, args);
    }

}
