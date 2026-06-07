package com.fooddelivery.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main entry point for the Auth Service.
 * Responsible for user registration, login and password management
 * for all roles: CUSTOMER, RESTAURANT_OWNER and AGENT.
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>User registration with BCrypt password encoding</li>
 *   <li>JWT token generation upon successful login</li>
 *   <li>Role-based profile creation via Feign clients</li>
 *   <li>Password update with current password verification</li>
 *   <li>Eureka service registration for service discovery</li>
 * </ul>
 *
 * <p>Runs on port {@code 8081} and registers with
 * Eureka as {@code AUTH-SERVICE}.</p>
 */
@SpringBootApplication
@EnableFeignClients
public class AuthServiceApplication {

    /**
     * Starts the Auth Service Spring Boot application.
     *
     * @param args command line arguments passed at startup
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}