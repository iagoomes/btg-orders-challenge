package com.btg.challenge.orders.infra.repository;

import com.btg.challenge.orders.infra.repository.model.OrderItemData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemDataRepository extends JpaRepository<OrderItemData, Long> {

    @Query("SELECT oi FROM OrderItemData oi WHERE oi.order.orderId = :orderId")
    List<OrderItemData> findByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT oi FROM OrderItemData oi WHERE oi.order.customer.customerId = :customerId")
    List<OrderItemData> findByCustomerId(@Param("customerId") Long customerId);
}
