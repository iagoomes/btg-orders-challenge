package com.btg.challenge.orders.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {

    private Long orderId;
    private Long customerId;
    private BigDecimal totalAmount;
    private Integer itemsCount;
    private LocalDateTime createdAt;
    private List<OrderItem> items;

    // Constructors
    public Order() {
    }

    public Order(Long orderId, Long customerId, List<OrderItem> items) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = items;
        updateTotals();
    }


    public BigDecimal calculateTotalAmount() {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void updateTotals() {
        this.itemsCount = items != null ? items.size() : 0;
        this.totalAmount = calculateTotalAmount();

        // Atualiza o total de cada item também
        if (items != null) {
            items.forEach(OrderItem::updateTotalPrice);
        }
    }

    public boolean isValid() {
        return orderId != null
                && customerId != null
                && items != null
                && !items.isEmpty()
                && items.stream().allMatch(OrderItem::isValid);
    }

    public void addItem(OrderItem item) {
        if (items != null) {
            items.add(item);
            updateTotals();
        }
    }

    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getItemsCount() {
        return itemsCount;
    }

    public void setItemsCount(Integer itemsCount) {
        this.itemsCount = itemsCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        updateTotals();
    }
}
