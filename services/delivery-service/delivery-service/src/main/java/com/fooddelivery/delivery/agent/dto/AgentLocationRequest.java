package com.fooddelivery.delivery.agent.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgentLocationRequest {

    private Double latitude;
    private Double longitude;
}