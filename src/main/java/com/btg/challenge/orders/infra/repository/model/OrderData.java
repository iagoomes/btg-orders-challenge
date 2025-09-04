package com.btg.challenge.orders.infra.repository.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderData {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerData customer;

    @Column(name = "total_amount", precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "items_count")
    private Integer itemsCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItemData> items;

//    todo: colocar na classe de dominio
//     Business methods
//    public BigDecimal calculateTotalAmount() {
//        if (items == null || items.isEmpty()) {
//            return BigDecimal.ZERO;
//        }
//
//        return items.stream()
//                .map(OrderItemD::getTotalPrice)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }
//
//    public void updateTotals() {
//        this.itemsCount = items != null ? items.size() : 0;
//        this.totalAmount = calculateTotalAmount();
//    }
//
//    @PrePersist
//    @PreUpdate
//    private void prePersist() {
//        updateTotals();
//    }
}