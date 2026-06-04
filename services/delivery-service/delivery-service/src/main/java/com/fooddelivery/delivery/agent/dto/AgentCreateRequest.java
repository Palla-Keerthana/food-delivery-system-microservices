package com.fooddelivery.delivery.agent.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgentCreateRequest {
    private Long userId;  // ← matches auth-service AgentRequest
    private String name;
    private String phone;
}