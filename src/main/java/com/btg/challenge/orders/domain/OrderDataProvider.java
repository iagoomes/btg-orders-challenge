package com.btg.challenge.orders.domain;

import com.btg.challenge.orders.domain.entity.Customer;
import com.btg.challenge.orders.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderDataProvider {

    Order save(Order order, Customer customer);

    Optional<Order> findById(Long orderId);

    Optional<Order> findByIdWithItems(Long orderId);

    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    long countByCustomerId(Long customerId);

    boolean existsById(Long orderId);
}
