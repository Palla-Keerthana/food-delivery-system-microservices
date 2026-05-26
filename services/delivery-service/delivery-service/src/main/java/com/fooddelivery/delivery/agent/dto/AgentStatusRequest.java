package com.fooddelivery.delivery.agent.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgentStatusRequest {

    private boolean isAvailable;
}