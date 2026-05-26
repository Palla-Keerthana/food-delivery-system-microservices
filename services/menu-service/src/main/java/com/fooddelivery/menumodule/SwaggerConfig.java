package com.fooddelivery.menumodule;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration for Menu Service.
 * Configures API documentation title, description and version.
 * Access documentation at: http://localhost:8082/swagger-ui.html
 */
@Configuration
public class SwaggerConfig {

    /**
     * Creates OpenAPI bean with API information.
     *
     * @return OpenAPI object with title, description and version
     */
    @Bean
    public OpenAPI menuServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Menu Service API")
                        .description(
                                "REST APIs for Menu and Restaurant management. " +
                                        "Handles adding, updating, deleting and viewing menu items.")
                        .version("1.0.0"));
    }
}