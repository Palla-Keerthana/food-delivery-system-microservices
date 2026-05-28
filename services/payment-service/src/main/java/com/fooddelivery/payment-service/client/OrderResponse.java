// OrderResponse.java — in client package
package com.fooddelivery.payment_service.client;

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
    private Double totalAmount;  // ← fetch this
}