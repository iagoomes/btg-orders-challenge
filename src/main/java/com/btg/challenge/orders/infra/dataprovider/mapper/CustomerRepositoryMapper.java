package com.btg.challenge.orders.infra.dataprovider.mapper;

import com.btg.challenge.orders.domain.entity.Customer;
import com.btg.challenge.orders.domain.entity.Order;
import com.btg.challenge.orders.infra.repository.model.CustomerData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomerRepositoryMapper {

    private final OrderRepositoryMapper orderRepositoryMapper;

    public CustomerData toData(Customer customer) {
        if (customer == null) {
            return null;
        }

        return CustomerData.builder()
                .customerId(customer.getCustomerId())
                .build();
    }

    public Customer toDomain(CustomerData customerData) {
        if (customerData == null) {
            return null;
        }

        Customer customer = new Customer();
        customer.setCustomerId(customerData.getCustomerId());
        customer.setCreatedAt(customerData.getCreatedAt());

        if (customerData.getOrders() != null) {
            List<Order> orders = customerData.getOrders().stream()
                    .map(orderRepositoryMapper::toDomain)
                    .toList();
            customer.setOrders(orders);
        }

        return customer;
    }
}
