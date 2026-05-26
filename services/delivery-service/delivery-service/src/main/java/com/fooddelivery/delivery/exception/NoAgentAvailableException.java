package com.fooddelivery.delivery.exception;

public class NoAgentAvailableException
        extends RuntimeException {

    public NoAgentAvailableException(String message) {
        super(message);
    }
}