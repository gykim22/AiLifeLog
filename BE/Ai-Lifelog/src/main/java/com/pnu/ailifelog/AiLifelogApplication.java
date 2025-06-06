package com.pnu.ailifelog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AiLifelogApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiLifelogApplication.class, args);
    }
}
