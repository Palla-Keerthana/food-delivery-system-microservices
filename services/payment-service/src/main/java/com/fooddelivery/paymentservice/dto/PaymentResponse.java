package com.fooddelivery.paymentservice.dto;

import com.fooddelivery.paymentservice.model.PaymentMethod;
import com.fooddelivery.paymentservice.model.PaymentStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long paymentId;
    private Long orderId;
    private Long customerId;
    private Double amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String transactionId;
    private String failureReason;
    private LocalDateTime paymentTime;
}