package com.btg.challenge.orders.app.resource;

import com.btg.challenge.orders.api.OrdersApiDelegate;
import com.btg.challenge.orders.app.service.OrderService;
import com.btg.challenge.orders.infra.exception.OrderNotFoundException;
import com.btg.challenge.orders.model.OrderTotalResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrdersResource implements OrdersApiDelegate {

    private final OrderService orderService;

    @Override
    public CompletableFuture<ResponseEntity<OrderTotalResponse>> getOrderTotal(Long orderId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                OrderTotalResponse response = orderService.getOrderTotal(orderId);
                return ResponseEntity.ok(response);
            } catch (OrderNotFoundException e) {
                log.error("Order not found: {}", orderId);
                return ResponseEntity.notFound().build();
            } catch (Exception e) {
                log.error("Error getting order total for orderId: {}", orderId, e);
                return ResponseEntity.internalServerError().build();
            }
        });
    }
}
