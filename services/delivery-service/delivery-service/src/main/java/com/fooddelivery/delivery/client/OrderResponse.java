package com.fooddelivery.delivery.client;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private Long customerId;
    private Long restaurantId;
    private String status;
    private Double totalAmount;
    private String customerAddress;
}