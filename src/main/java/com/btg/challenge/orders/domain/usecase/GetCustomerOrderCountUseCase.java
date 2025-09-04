package com.btg.challenge.orders.domain.usecase;

import com.btg.challenge.orders.domain.CustomerDataProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetCustomerOrderCountUseCase {

    private final CustomerDataProvider customerDataProvider;

    public long execute(Long customerId) {
        log.info("Getting order count for customerId: {}", customerId);

        if (customerId == null || customerId <= 0) {
            log.warn("Invalid customerId provided: {}", customerId);
            return 0L;
        }

        long orderCount = customerDataProvider.countOrdersByCustomerId(customerId);

        log.info("Order count for customerId {}: {}", customerId, orderCount);

        return orderCount;
    }
}