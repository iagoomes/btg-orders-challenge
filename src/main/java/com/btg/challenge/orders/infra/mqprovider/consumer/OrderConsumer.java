package com.btg.challenge.orders.infra.mqprovider.consumer;

import com.btg.challenge.orders.app.service.OrderMessagingService;
import com.btg.challenge.orders.infra.mqprovider.OrderMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConsumer {
    private final OrderMessagingService orderMessagingService;

    @RabbitListener(queues = "${orders.queue.name}")
    public void processOrder(OrderMessage orderMessage) {
        log.info("Received order message: orderId={}, customerId={}",
                orderMessage.getCodigoPedido(), orderMessage.getCodigoCliente());

        orderMessagingService.processOrderMessage(orderMessage);
    }
}
