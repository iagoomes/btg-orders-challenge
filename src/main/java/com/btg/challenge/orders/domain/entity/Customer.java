package com.btg.challenge.orders.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

public class Customer {

    private Long customerId;
    private LocalDateTime createdAt;
    private List<Order> orders;


    public Customer() {
    }

    public Customer(Long customerId) {
        this.customerId = customerId;
    }

    public Customer(Long customerId, List<Order> orders) {
        this.customerId = customerId;
        this.orders = orders;
    }

    public int getTotalOrders() {
        return orders != null ? orders.size() : 0;
    }

    public boolean hasOrders() {
        return orders != null && !orders.isEmpty();
    }

    public boolean isValid() {
        return customerId != null && customerId > 0;
    }

    public void addOrder(Order order) {
        if (orders != null && order != null && order.getCustomerId().equals(this.customerId)) {
            orders.add(order);
        }
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
