package com.btg.challenge.orders.app.service.impl;

import com.btg.challenge.orders.app.mapper.CustomerMapper;
import com.btg.challenge.orders.app.service.CustomerService;
import com.btg.challenge.orders.domain.usecase.GetCustomerOrderCountUseCase;
import com.btg.challenge.orders.domain.usecase.GetCustomerOrdersUseCase;
import com.btg.challenge.orders.model.CustomerOrderCountResponse;
import com.btg.challenge.orders.model.CustomerOrdersResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final GetCustomerOrderCountUseCase getOrderCountUseCase;
    private final GetCustomerOrdersUseCase getCustomerOrdersUseCase;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerOrderCountResponse getOrderCount(Long customerId) {
        var orderCount = getOrderCountUseCase.execute(customerId);
        return customerMapper.toOrderCountResponse(customerId, orderCount);
    }

    @Override
    public CustomerOrdersResponse getCustomerOrders(Long customerId, Pageable pageable) {
        var customerOrdersPage = getCustomerOrdersUseCase.execute(customerId, pageable);
        return customerMapper.toCustomerOrdersResponse(customerOrdersPage);
    }
}