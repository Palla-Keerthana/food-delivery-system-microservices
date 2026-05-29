package com.fooddelivery.paymentservice.model;

public enum PaymentStatus {
    PENDING,      // payment initiated
    SUCCESS,      // payment successful
    FAILED,       // payment failed
    REFUNDED      // payment refunded
}