package com.fooddelivery.auth.client;

import com.fooddelivery.auth.dto.AgentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for communicating with the delivery-service.
 * Used to create agent profiles when a new user registers
 * with the role {@code AGENT}.
 */
@FeignClient(name = "DELIVERY-SERVICE", url = "http://localhost:8086")
public interface AgentClient {

    /**
     * Sends a request to the delivery-service to create a new agent profile.
     * Called during user registration when role is AGENT.
     *
     * @param request the agent request containing userId, name and phone
     */
    @PostMapping("/api/agents/create")
    void registerAgent(@RequestBody AgentRequest request);
}