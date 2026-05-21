package com.fooddelivery.menumodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient  // ← registers this service with Eureka!
public class MenumoduleApplication {

	public static void main(String[] args) {
		SpringApplication.run(MenumoduleApplication.class, args);
	}
}