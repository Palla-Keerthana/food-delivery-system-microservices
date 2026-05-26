package com.fooddelivery.delivery.delivery.dto;

import com.fooddelivery.delivery.delivery.model.DeliveryStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryStatusRequest {

    private DeliveryStatus status;
}