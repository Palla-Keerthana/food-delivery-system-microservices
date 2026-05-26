package com.fooddelivery.delivery.delivery.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequest {

    private Long orderId;
    private Long restaurantId;
    private String deliveryAddress;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
}