package com.btg.challenge.orders.domain;

import com.btg.challenge.orders.domain.entity.Customer;

import java.util.Optional;

public interface CustomerDataProvider {

    Customer save(Customer customer);

    Optional<Customer> findById(Long customerId);

    Optional<Customer> findByIdWithOrders(Long customerId);

    long countOrdersByCustomerId(Long customerId);
}
