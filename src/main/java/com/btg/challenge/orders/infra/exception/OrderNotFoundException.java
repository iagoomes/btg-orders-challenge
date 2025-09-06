package com.btg.challenge.orders.infra.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(Long orderId) {
        super("Order not found with id: " + orderId);
    }
}