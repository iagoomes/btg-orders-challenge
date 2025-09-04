package com.btg.challenge.orders.app.resource;

import com.btg.challenge.orders.api.CustomersApiDelegate;
import com.btg.challenge.orders.app.service.CustomerService;
import com.btg.challenge.orders.infra.exception.CustomerNotFoundException;
import com.btg.challenge.orders.model.CustomerOrderCountResponse;
import com.btg.challenge.orders.model.CustomerOrdersResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomersResource implements CustomersApiDelegate {

    private final CustomerService customerService;

    @Override
    public CompletableFuture<ResponseEntity<CustomerOrderCountResponse>> getCustomerOrderCount(Long customerId) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Getting order count for customerId: {}", customerId);

            try {
                CustomerOrderCountResponse response = customerService.getOrderCount(customerId);
                return ResponseEntity.ok(response);
            } catch (CustomerNotFoundException e) {
                log.error("Customer not found: {}", customerId);
                return ResponseEntity.notFound().build();
            } catch (Exception e) {
                log.error("Error getting order count for customerId: {}", customerId, e);
                return ResponseEntity.internalServerError().build();
            }
        });
    }

    @Override
    public CompletableFuture<ResponseEntity<CustomerOrdersResponse>> getCustomerOrders(
            Long customerId,
            Integer page,
            Integer size,
            Pageable pageable) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Getting orders for customerId: {}, pageable: {}", customerId, pageable);

            try {
                CustomerOrdersResponse response = customerService.getCustomerOrders(customerId, pageable);
                return ResponseEntity.ok(response);
            } catch (CustomerNotFoundException e) {
                log.error("Customer not found: {}", customerId);
                return ResponseEntity.notFound().build();
            } catch (Exception e) {
                log.error("Error getting orders for customerId: {}", customerId, e);
                return ResponseEntity.internalServerError().build();
            }
        });
    }
}
