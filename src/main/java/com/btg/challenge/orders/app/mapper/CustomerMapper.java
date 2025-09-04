package com.btg.challenge.orders.app.mapper;

import com.btg.challenge.orders.domain.entity.Customer;
import com.btg.challenge.orders.domain.entity.Order;
import com.btg.challenge.orders.model.CustomerOrderCountResponse;
import com.btg.challenge.orders.model.CustomerOrdersResponse;
import com.btg.challenge.orders.model.OrderSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomerMapper {

    private final OrderMapper orderMapper;

    public CustomerOrderCountResponse toOrderCountResponse(Long customerId, long orderCount) {
        CustomerOrderCountResponse response = new CustomerOrderCountResponse();
        response.setCustomerId(customerId);
        response.setOrderCount(orderCount);
        return response;
    }

    public CustomerOrdersResponse toCustomerOrdersResponse(Page<Order> orderPage) {
        CustomerOrdersResponse response = new CustomerOrdersResponse();

        if (!orderPage.isEmpty()) {
            response.setCustomerId(orderPage.getContent().getFirst().getCustomerId());

            List<OrderSummary> orderSummaries = orderPage.getContent().stream()
                    .map(orderMapper::toOrderSummary)
                    .toList();
            response.setOrders(orderSummaries);
        }

        response.setTotalElements(orderPage.getTotalElements());
        response.setTotalPages(orderPage.getTotalPages());
        response.setCurrentPage(orderPage.getNumber());
        response.setPageSize(orderPage.getSize());

        return response;
    }

    public CustomerOrdersResponse toCustomerOrdersResponse(Customer customer, Page<Order> orderPage) {
        CustomerOrdersResponse response = toCustomerOrdersResponse(orderPage);
        response.setCustomerId(customer.getCustomerId());
        return response;
    }
}