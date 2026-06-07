package com.fooddelivery.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main entry point for the Customer Service.
 * Responsible for managing customer profiles in the
 * food delivery system.
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Create customer profile linked to user account</li>
 *   <li>Retrieve customer profile by customerId or userId</li>
 *   <li>Update customer profile details</li>
 *   <li>Eureka service registration for service discovery</li>
 *   <li>Feign client enabled for inter-service communication</li>
 * </ul>
 *
 * <p>Runs on port {@code 8082} and registers with
 * Eureka as {@code CUSTOMER-SERVICE}.</p>
 *
 * <p>All endpoints are protected by JWT token validated
 * at the API Gateway level.</p>
 */
@SpringBootApplication
@EnableFeignClients
public class CustomerServiceApplication {

    /**
     * Starts the Customer Service Spring Boot application.
     *
     * @param args command line arguments passed at startup
     */
    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}