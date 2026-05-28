package com.fooddelivery.auth_service.client;

import com.fooddelivery.auth_service.dto.AgentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "delivery-service")
public interface AgentClient {

    @PostMapping("/api/agents/register")
    void registerAgent(@RequestBody AgentRequest request);
}