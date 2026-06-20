package com.example.demo;

public class ProductNotFoundException extends RuntimeException{
    private final String orderId;

    public ProductNotFoundException(String message, String orderId) {
        super(message);
        this.orderId = orderId;
    }
    public String getOrderId() {
        return orderId;
    }
}
