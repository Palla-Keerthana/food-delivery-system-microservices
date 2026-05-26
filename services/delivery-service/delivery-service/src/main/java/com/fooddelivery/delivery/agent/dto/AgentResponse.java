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
//    private String vehicleType;
//    private String vehicleNumber;
    @JsonProperty("isAvailable")
    private boolean isAvailable;
    @JsonProperty("isAvailable")   // ← shows "isAvailable"
    public boolean isAvailable() {
        return isAvailable;
    }
    private Double currentLatitude;
    private Double currentLongitude;
    private Integer totalDeliveries;
    private Double rating;
    private Double totalEarnings;
}