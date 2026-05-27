package com.fooddelivery.auth_service.dto;

import lombok.Data;

@Data
public class AgentRequest {
    private Long userId;
    private String name;
    private String phone;
}