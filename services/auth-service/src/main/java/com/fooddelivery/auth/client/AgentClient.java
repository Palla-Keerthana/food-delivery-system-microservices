package com.fooddelivery.auth.client;

import com.fooddelivery.auth.dto.AgentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "DELIVERY-SERVICE", url = "http://localhost:8086")
public interface AgentClient {

    @PostMapping("/api/agents/create")
    void registerAgent(@RequestBody AgentRequest request);
}