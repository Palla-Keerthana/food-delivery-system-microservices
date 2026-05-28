package com.fooddelivery.payment_service.dto;

import com.fooddelivery.payment_service.model.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull(message = "Order id is required")
    private Long orderId;

    @NotNull(message = "Customer id is required")
    private Long customerId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    // UPI
    private String upiId;

    // Card
    private String cardNumber;
    private String cardExpiry;
    private String cardCvv;

    // Wallet
    private String walletType;  // PAYTM, PHONEPE, GPAY
}