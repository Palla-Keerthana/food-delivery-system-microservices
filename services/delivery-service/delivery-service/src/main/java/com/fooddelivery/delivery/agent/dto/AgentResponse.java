package com.fooddelivery.delivery.agent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentResponse {

    private Long agentId;
    private String name;
    private String phone;
    private boolean isAvailable;
    private Long currentDeliveryId;
    private Integer totalDeliveries;
    private Double totalEarnings;
    private Double rating;
    private Integer totalRatings;
}