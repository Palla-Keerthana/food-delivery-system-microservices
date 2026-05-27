package com.fooddelivery.payment_service.dto;

import com.fooddelivery.payment_service.model.PaymentMethod;
import com.fooddelivery.payment_service.model.PaymentStatus;
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