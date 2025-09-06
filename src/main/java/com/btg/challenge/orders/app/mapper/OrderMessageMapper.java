package com.btg.challenge.orders.app.mapper;

import com.btg.challenge.orders.domain.entity.Order;
import com.btg.challenge.orders.domain.entity.OrderItem;
import com.btg.challenge.orders.infra.mqprovider.OrderItemMessage;
import com.btg.challenge.orders.infra.mqprovider.OrderMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMessageMapper {

    public Order toDomain(OrderMessage message) {
        if (message == null) {
            return null;
        }

        List<OrderItem> items = message.getItens().stream()
                .map(this::toOrderItemDomain)
                .toList();

        return new Order(message.getCodigoPedido(), message.getCodigoCliente(), items);
    }

    private OrderItem toOrderItemDomain(OrderItemMessage itemMessage) {
        return new OrderItem(
                itemMessage.getProduto(),
                itemMessage.getQuantidade(),
                itemMessage.getPreco()
        );
    }
}