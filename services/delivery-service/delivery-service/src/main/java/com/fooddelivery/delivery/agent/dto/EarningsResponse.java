package com.fooddelivery.delivery.agent.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EarningsResponse {

    private Long agentId;
    private Double totalEarnings;
    private Integer totalDeliveries;
    private Double earningsToday;
    private Double earningsThisWeek;
    private Double earningsThisMonth;
}