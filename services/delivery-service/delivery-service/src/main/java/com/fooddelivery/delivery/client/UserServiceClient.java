package com.fooddelivery.delivery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@FeignClient(name = "customer-service")
public interface UserServiceClient {

    @GetMapping("/api/users/{userId}")
    Map<String, Object> getAgentProfile(
            @PathVariable Long userId);
}