package com.btg.challenge.orders.app.service.impl;

import com.btg.challenge.orders.app.mapper.OrderMapper;
import com.btg.challenge.orders.app.service.OrderService;
import com.btg.challenge.orders.domain.usecase.GetOrderTotalUseCase;
import com.btg.challenge.orders.infra.exception.OrderNotFoundException;
import com.btg.challenge.orders.model.OrderTotalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final GetOrderTotalUseCase getOrderTotalUseCase;
    private final OrderMapper orderResponseMapper;

    @Override
    public OrderTotalResponse getOrderTotal(Long orderId) {
        var orderTotal = getOrderTotalUseCase.execute(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return orderResponseMapper.toOrderTotalResponse(orderTotal);
    }
}
