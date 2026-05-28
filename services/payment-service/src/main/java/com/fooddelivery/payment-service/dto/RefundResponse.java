package com.fooddelivery.payment_service.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundResponse {

    private Long paymentId;
    private Long orderId;
    private Double refundAmount;
    private String status;
    private LocalDateTime refundTime;
}