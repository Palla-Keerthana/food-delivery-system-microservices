package com.fooddelivery.paymentservice.exception;

public class PaymentAlreadyDoneException
        extends RuntimeException {
    public PaymentAlreadyDoneException(String message) {
        super(message);
    }
}