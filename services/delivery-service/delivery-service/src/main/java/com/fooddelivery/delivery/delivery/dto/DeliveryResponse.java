package com.fooddelivery.delivery.delivery.dto;

import com.fooddelivery.delivery.delivery.model.DeliveryStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryResponse {

    private Long deliveryId;
    private Long orderId;
    private Long agentId;
    private String deliveryAddress;
    private DeliveryStatus status;
    private LocalDateTime assignedAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime estimatedDeliveryTime;
}