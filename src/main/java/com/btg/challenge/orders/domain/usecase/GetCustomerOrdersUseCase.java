package com.btg.challenge.orders.domain.usecase;

import com.btg.challenge.orders.domain.CustomerDataProvider;
import com.btg.challenge.orders.domain.OrderDataProvider;
import com.btg.challenge.orders.domain.entity.Order;
import com.btg.challenge.orders.infra.exception.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetCustomerOrdersUseCase {

    private final OrderDataProvider orderDataProvider;
    private final CustomerDataProvider customerDataProvider;

    @Transactional(readOnly = true)
    public Page<Order> execute(Long customerId, Pageable pageable) {
        log.info("Getting orders for customerId: {}, pageable: {}", customerId, pageable);

        if (customerId == null || customerId <= 0) {
            log.warn("Invalid customerId provided: {}", customerId);
            throw new CustomerNotFoundException("Invalid customer ID: " + customerId);
        }

        if (!customerExists(customerId)) {
            log.warn("Customer not found with id: {}", customerId);
            throw new CustomerNotFoundException(customerId);
        }

        Pageable pageableWithSort = addDefaultSorting(pageable);

        Page<Order> ordersPage = orderDataProvider.findByCustomerId(customerId, pageableWithSort);

        log.info("Found {} orders for customerId: {} (page {}/{})", ordersPage.getNumberOfElements(), customerId, ordersPage.getNumber() + 1, ordersPage.getTotalPages());

        return ordersPage;
    }

    private Pageable addDefaultSorting(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdAt").descending());
        }
        return pageable;
    }

    private boolean customerExists(Long customerId) {
        return customerDataProvider.findById(customerId).isPresent();
    }
}