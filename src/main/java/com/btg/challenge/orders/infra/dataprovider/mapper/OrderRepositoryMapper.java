package com.btg.challenge.orders.infra.dataprovider.mapper;

import com.btg.challenge.orders.domain.entity.Order;
import com.btg.challenge.orders.domain.entity.OrderItem;
import com.btg.challenge.orders.infra.repository.model.CustomerData;
import com.btg.challenge.orders.infra.repository.model.OrderData;
import com.btg.challenge.orders.infra.repository.model.OrderItemData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderRepositoryMapper {

    private final OrderItemRepositoryMapper orderItemMapper;

    public OrderData toData(Order order, CustomerData customer) {
        if (order == null) {
            return null;
        }

        OrderData orderData = OrderData.builder()
                .orderId(order.getOrderId())
                .customer(customer)
                .totalAmount(order.getTotalAmount())
                .itemsCount(order.getItemsCount())
                .build();

        List<OrderItemData> itemsData = order.getItems().stream()
                .map(item -> orderItemMapper.toOrderItemData(item, orderData))
                .toList();

        orderData.setItems(itemsData);
        return orderData;
    }

    public Order toDomain(OrderData orderData) {
        if (orderData == null) {
            return null;
        }

        List<OrderItem> items = orderData.getItems().stream()
                .map(orderItemMapper::toOrderItemDomain)
                .toList();

        Order order = new Order();
        order.setOrderId(orderData.getOrderId());
        order.setCustomerId(orderData.getCustomer().getCustomerId());
        order.setTotalAmount(orderData.getTotalAmount());
        order.setItemsCount(orderData.getItemsCount());
        order.setCreatedAt(orderData.getCreatedAt());
        order.setItems(items);

        return order;
    }
}
