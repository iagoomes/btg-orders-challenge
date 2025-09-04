package com.btg.challenge.orders.app.mapper;

import com.btg.challenge.orders.domain.entity.Order;
import com.btg.challenge.orders.domain.entity.OrderItem;
import com.btg.challenge.orders.model.OrderItemSummary;
import com.btg.challenge.orders.model.OrderSummary;
import com.btg.challenge.orders.model.OrderTotalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    public OrderTotalResponse toOrderTotalResponse(Order order) {
        OrderTotalResponse response = new OrderTotalResponse();
        response.setOrderId(order.getOrderId());
        response.setTotal(order.getTotalAmount().doubleValue());
        response.setCurrency("BRL");
        return response;
    }

    public OrderSummary toOrderSummary(Order order) {
        OrderSummary summary = new OrderSummary();
        summary.setOrderId(order.getOrderId());
        summary.setCustomerId(order.getCustomerId());
        summary.setTotalAmount(order.getTotalAmount().doubleValue());
        summary.setItemsCount(order.getItemsCount());
        summary.setCreatedAt(order.getCreatedAt());

        if (order.getItems() != null) {
            List<OrderItemSummary> itemSummaries = order.getItems().stream()
                    .map(this::toOrderItemSummary)
                    .toList();
            summary.setItems(itemSummaries);
        }

        return summary;
    }

    private OrderItemSummary toOrderItemSummary(OrderItem domainItem) {
        OrderItemSummary itemSummary = new OrderItemSummary();
        itemSummary.setProduct(domainItem.getProduct());
        itemSummary.setQuantity(domainItem.getQuantity());
        itemSummary.setPrice(domainItem.getPrice().doubleValue());
        return itemSummary;
    }
}