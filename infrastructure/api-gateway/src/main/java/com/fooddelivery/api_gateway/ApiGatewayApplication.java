package com.fooddelivery.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the API Gateway.
 * Acts as the single entry point for all client requests
 * in the food delivery system.
 *
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Routes incoming requests to appropriate microservices</li>
 *   <li>Validates JWT tokens for all protected endpoints</li>
 *   <li>Forwards user email and role as request headers
 *       to downstream services</li>
 *   <li>Load balances requests across service instances
 *       via Eureka discovery</li>
 *   <li>Blocks unauthorized requests before they reach services</li>
 * </ul>
 *
 * <p>Routing rules:</p>
 * <ul>
 *   <li>{@code /auth/**}            → AUTH-SERVICE      (8081)</li>
 *   <li>{@code /api/customers/**}   → CUSTOMER-SERVICE  (8082)</li>
 *   <li>{@code /api/menu/**}        → MENU-SERVICE      (8083)</li>
 *   <li>{@code /api/restaurants/**} → MENU-SERVICE      (8083)</li>
 *   <li>{@code /api/orders/**}      → ORDER-SERVICE     (8084)</li>
 *   <li>{@code /api/payments/**}    → PAYMENT-SERVICE   (8085)</li>
 *   <li>{@code /api/delivery/**}    → DELIVERY-SERVICE  (8086)</li>
 *   <li>{@code /api/agents/**}      → DELIVERY-SERVICE  (8086)</li>
 * </ul>
 *
 * <p>Public routes (no token required):</p>
 * <ul>
 *   <li>{@code POST /auth/register}</li>
 *   <li>{@code POST /auth/login}</li>
 * </ul>
 *
 * <p>Runs on port {@code 8090}. Must be started LAST
 * after all services are registered with Eureka.</p>
 */
@SpringBootApplication
public class ApiGatewayApplication {

    /**
     * Starts the API Gateway Spring Boot application.
     *
     * @param args command line arguments passed at startup
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}