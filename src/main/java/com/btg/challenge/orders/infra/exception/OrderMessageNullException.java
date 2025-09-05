package com.btg.challenge.orders.infra.exception;

public class OrderMessageNullException extends IllegalArgumentException {
    public OrderMessageNullException() {
        super("Order message cannot be null");
    }
    public OrderMessageNullException(String message) {
        super(message);
    }
}

