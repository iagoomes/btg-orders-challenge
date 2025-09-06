package com.btg.challenge.orders.infra.dataprovider;

import com.btg.challenge.orders.domain.OrderDataProvider;
import com.btg.challenge.orders.domain.entity.Customer;
import com.btg.challenge.orders.domain.entity.Order;
import com.btg.challenge.orders.infra.dataprovider.mapper.CustomerRepositoryMapper;
import com.btg.challenge.orders.infra.dataprovider.mapper.OrderRepositoryMapper;
import com.btg.challenge.orders.infra.repository.OrderDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderDataProviderImpl implements OrderDataProvider {

    private final OrderDataRepository orderRepository;
    private final OrderRepositoryMapper orderMapper;
    private final CustomerRepositoryMapper customerMapper;

    @Override
    public Order save(Order order, Customer customer) {
        var customerData = customerMapper.toData(customer);
        var orderData = orderMapper.toData(order, customerData);
        var savedOrderData = orderRepository.save(orderData);
        return orderMapper.toDomain(savedOrderData);
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toDomain);
    }

    @Override
    public Optional<Order> findByIdWithItems(Long orderId) {
        return orderRepository.findByIdWithItems(orderId)
                .map(orderMapper::toDomain);
    }

    @Override
    public Page<Order> findByCustomerId(Long customerId, Pageable pageable) {
        return orderRepository.findByCustomerId(customerId, pageable)
                .map(orderMapper::toDomain);
    }

    @Override
    public long countByCustomerId(Long customerId) {
        return orderRepository.countByCustomerId(customerId);
    }

    @Override
    public boolean existsById(Long orderId) {
        return orderRepository.existsById(orderId);
    }
}