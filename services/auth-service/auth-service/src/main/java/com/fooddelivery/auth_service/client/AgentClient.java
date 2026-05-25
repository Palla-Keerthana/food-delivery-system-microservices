package com.fooddelivery.auth_service.client;

import com.fooddelivery.auth_service.dto.AgentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "AGENT-SERVICE")
public interface AgentClient {

    @PostMapping("/api/agents")
    void createProfile(@RequestBody AgentRequest request);
}