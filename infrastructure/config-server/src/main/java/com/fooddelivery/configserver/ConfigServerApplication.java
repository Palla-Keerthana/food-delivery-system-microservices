package com.fooddelivery.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Main entry point for the Config Server.
 * Acts as the centralized configuration management service
 * for all microservices in the food delivery system.
 *
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>Serves configuration properties to all microservices</li>
 *   <li>Eliminates the need for each service to maintain
 *       its own full application.properties</li>
 *   <li>Uses native profile to load config files from
 *       the local classpath resources folder</li>
 *   <li>Allows centralized management of shared properties
 *       such as database URLs, Eureka config and JWT secret</li>
 * </ul>
 *
 * <p>Config files served (located in resources folder):</p>
 * <ul>
 *   <li>{@code auth-service.properties}</li>
 *   <li>{@code customer-service.properties}</li>
 *   <li>{@code api-gateway.properties}</li>
 *   <li>{@code eureka-server.properties}</li>
 *   <li>{@code menu-service.properties}</li>
 *   <li>{@code delivery-service.properties}</li>
 *   <li>{@code order-service.properties}</li>
 *   <li>{@code payment-service.properties}</li>
 * </ul>
 *
 * <p>Config files accessible via:</p>
 * <ul>
 *   <li>{@code http://localhost:8888/{service-name}/default}</li>
 * </ul>
 *
 * <p>Runs on port {@code 8888} using Spring Boot {@code 3.2.5}.
 * Must be started SECOND after Eureka Server.</p>
 *
 * <p>Uses {@code native} profile to serve config from
 * local filesystem instead of a Git repository.</p>
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    /**
     * Starts the Config Server Spring Boot application.
     * Sets the {@code native} profile programmatically to serve
     * configuration files from the local classpath resources folder.
     * This avoids passing {@code spring.profiles.active=native}
     * in application.properties which would be incorrectly
     * forwarded to client services.
     *
     * @param args command line arguments passed at startup
     */
    public static void main(String[] args) {
        SpringApplication app =
                new SpringApplication(ConfigServerApplication.class);
        app.setAdditionalProfiles("native");
        app.run(args);
    }
}