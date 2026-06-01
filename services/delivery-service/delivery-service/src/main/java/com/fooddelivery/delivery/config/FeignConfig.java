package com.fooddelivery.delivery.config;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    // log feign requests for debugging
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    // connection timeout 5s, read timeout 10s
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(5000, 10000);
    }

    @Component
    public static class FeignAuthInterceptor
            implements RequestInterceptor {

        @Override
        public void apply(RequestTemplate template) {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder
                            .getRequestAttributes();

            if (attributes != null) {
                String token = attributes
                        .getRequest()
                        .getHeader("Authorization");

                if (token != null) {
                    template.header(
                            "Authorization", token);
                }
            }
        }
    }
}