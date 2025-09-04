package com.btg.challenge.orders.domain.usecase;

import com.btg.challenge.orders.domain.CustomerDataProvider;
import com.btg.challenge.orders.domain.OrderDataProvider;
import com.btg.challenge.orders.domain.entity.Customer;
import com.btg.challenge.orders.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessOrderUseCase {
    
    private final OrderDataProvider orderDataProvider;
    private final CustomerDataProvider customerDataProvider;

    public void execute(Order order) {
        log.info("Processing order: orderId={}, customerId={}",
                order.getOrderId(), order.getCustomerId());

        if (!order.isValid()) {
            log.error("Invalid order received: {}", order.getOrderId());
            throw new IllegalArgumentException("Invalid order data");
        }

        if (orderDataProvider.existsById(order.getOrderId())) {
            log.warn("Order already exists: {}", order.getOrderId());
            return;
        }

        Customer customer = findOrCreateCustomer(order.getCustomerId());

        Order savedOrder = orderDataProvider.save(order, customer);

        log.info("Order processed successfully: orderId={}, totalAmount={}, itemsCount={}",
                savedOrder.getOrderId(), savedOrder.getTotalAmount(), savedOrder.getItemsCount());
    }

    private Customer findOrCreateCustomer(Long customerId) {
        return customerDataProvider.findById(customerId)
                .orElseGet(() -> {
                    log.info("Creating new customer: {}", customerId);
                    Customer newCustomer = new Customer(customerId);
                    return customerDataProvider.save(newCustomer);
                });
    }
}