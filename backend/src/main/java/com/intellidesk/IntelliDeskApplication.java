package com.intellidesk;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IntelliDeskApplication {
    public static void main(String[] args) {
        SpringApplication.run(IntelliDeskApplication.class, args);
    }
}
