package com.fooddelivery.delivery.config;

import feign.Logger;
import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}