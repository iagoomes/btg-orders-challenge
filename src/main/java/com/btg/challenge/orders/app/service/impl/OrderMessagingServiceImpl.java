package com.btg.challenge.orders.app.service.impl;

import com.btg.challenge.orders.app.service.OrderMessagingService;
import com.btg.challenge.orders.domain.entity.Order;
import com.btg.challenge.orders.domain.usecase.ProcessOrderUseCase;
import com.btg.challenge.orders.infra.mqprovider.OrderMessage;
import com.btg.challenge.orders.app.mapper.OrderMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderMessagingServiceImpl implements OrderMessagingService {

    private final ProcessOrderUseCase processOrderUseCase;
    private final OrderMessageMapper orderMessageMapper;

    @Override
    @Transactional
    public void processOrderMessage(OrderMessage orderMessage) {
        log.info("Processing order message: orderId={}, customerId={}",
                orderMessage.getCodigoPedido(), orderMessage.getCodigoCliente());

        try {
            Order order = orderMessageMapper.toDomain(orderMessage);
            processOrderUseCase.execute(order);
        } catch (Exception e) {
            log.error("Error processing order message: orderId={}, error={}",
                    orderMessage.getCodigoPedido(), e.getMessage(), e);
            throw e;
        }
    }
}
