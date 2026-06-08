package com.fooddelivery.eureka_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Main entry point for the Eureka Server.
 * Acts as the service registry for all microservices
 * in the food delivery system.
 *
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Maintains a registry of all running microservices</li>
 *   <li>Allows services to register themselves on startup</li>
 *   <li>Enables service discovery by name instead of hardcoded URLs</li>
 *   <li>Monitors service health via heartbeat mechanism</li>
 *   <li>Removes unresponsive services from the registry</li>
 * </ul>
 *
 * <p>Registered services:</p>
 * <ul>
 *   <li>AUTH-SERVICE      → port 8081</li>
 *   <li>CUSTOMER-SERVICE  → port 8082</li>
 *   <li>MENU-SERVICE      → port 8083</li>
 *   <li>ORDER-SERVICE     → port 8084</li>
 *   <li>PAYMENT-SERVICE   → port 8085</li>
 *   <li>DELIVERY-SERVICE  → port 8086</li>
 *   <li>API-GATEWAY       → port 8090</li>
 * </ul>
 *
 * <p>Runs on port {@code 8761}. Dashboard available at
 * {@code http://localhost:8761}.</p>
 *
 * <p>Must be started FIRST before all other services.</p>
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    /**
     * Starts the Eureka Server Spring Boot application.
     *
     * @param args command line arguments passed at startup
     */
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}