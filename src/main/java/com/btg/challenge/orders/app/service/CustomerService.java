package com.btg.challenge.orders.app.service;

import com.btg.challenge.orders.model.CustomerOrderCountResponse;
import com.btg.challenge.orders.model.CustomerOrdersResponse;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    CustomerOrderCountResponse getOrderCount(Long customerId);

    CustomerOrdersResponse getCustomerOrders(Long customerId, Pageable pageable);
}
