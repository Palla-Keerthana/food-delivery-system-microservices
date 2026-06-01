package com.fooddelivery.delivery.agent.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequest {
    private Long customerId;
    private Long orderId;
    private Double rating;
    private String review;
}