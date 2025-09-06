package com.btg.challenge.orders.infra.dataprovider.mapper;

import com.btg.challenge.orders.domain.entity.OrderItem;
import com.btg.challenge.orders.infra.repository.model.OrderData;
import com.btg.challenge.orders.infra.repository.model.OrderItemData;
import org.springframework.stereotype.Component;

@Component
public class OrderItemRepositoryMapper {

    public OrderItemData toOrderItemData(OrderItem item, OrderData order) {
        return OrderItemData.builder()
                .order(order)
                .product(item.getProduct())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }

    public OrderItem toOrderItemDomain(OrderItemData itemData) {
        OrderItem item = new OrderItem();
        item.setItemId(itemData.getItemId());
        item.setProduct(itemData.getProduct());
        item.setQuantity(itemData.getQuantity());
        item.setPrice(itemData.getPrice());
        item.setTotalPrice(itemData.getTotalPrice());
        return item;
    }
}
