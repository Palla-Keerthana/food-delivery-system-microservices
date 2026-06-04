package com.fooddelivery.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication app =
                new SpringApplication(ConfigServerApplication.class);
        app.setAdditionalProfiles("native"); // ← set profile in code
        app.run(args);
    }
}