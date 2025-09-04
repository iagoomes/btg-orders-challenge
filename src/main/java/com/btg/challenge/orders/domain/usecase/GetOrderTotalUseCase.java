package com.btg.challenge.orders.domain.usecase;

import com.btg.challenge.orders.domain.OrderDataProvider;
import com.btg.challenge.orders.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetOrderTotalUseCase {

    private final OrderDataProvider orderDataProvider;

    public Optional<Order> execute(Long orderId) {
        log.info("Getting order total for orderId: {}", orderId);

        if (orderId == null || orderId <= 0) {
            log.warn("Invalid orderId provided: {}", orderId);
            return Optional.empty();
        }

        Optional<Order> orderOptional = orderDataProvider.findByIdWithItems(orderId);

        if (orderOptional.isEmpty()) {
            log.warn("Order not found with id: {}", orderId);
            return Optional.empty();
        }

        Order order = orderOptional.get();

        order.updateTotals();

        log.info("Order found - orderId: {}, total: {}, itemsCount: {}",
                orderId, order.getTotalAmount(), order.getItemsCount());

        return Optional.of(order);
    }
}
