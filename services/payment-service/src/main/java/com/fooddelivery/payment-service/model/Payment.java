package com.fooddelivery.payment_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private Long orderId;
    private Long customerId;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    // UPI specific
    private String upiId;

    // Card specific
    private String cardLastFourDigits;

    // Wallet specific
    private String walletType;  // PAYTM, PHONEPE, GPAY

    private String transactionId;   // unique transaction id
    private String failureReason;   // reason if failed

    private LocalDateTime paymentTime;
    private LocalDateTime refundTime;

    @PrePersist
    public void onCreate() {
        paymentTime = LocalDateTime.now();
        //status = PaymentStatus.PENDING;
    }
}