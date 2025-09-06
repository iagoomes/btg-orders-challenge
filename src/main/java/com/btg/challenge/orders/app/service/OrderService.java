package com.btg.challenge.orders.app.service;

import com.btg.challenge.orders.model.OrderTotalResponse;

public interface OrderService {
    OrderTotalResponse getOrderTotal(Long orderId);
}
