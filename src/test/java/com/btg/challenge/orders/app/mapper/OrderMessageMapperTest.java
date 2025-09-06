package com.btg.challenge.orders.app.mapper;

import com.btg.challenge.orders.domain.entity.Order;
import com.btg.challenge.orders.infra.mqprovider.OrderItemMessage;
import com.btg.challenge.orders.infra.mqprovider.OrderMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderMessageMapper Unit Tests")
class OrderMessageMapperTest {

    private final OrderMessageMapper mapper = new OrderMessageMapper();

    @Test
    @DisplayName("Should map OrderMessage to Order domain object correctly")
    void shouldMapOrderMessageToOrderDomainCorrectly() {
        OrderItemMessage item1 = new OrderItemMessage();
        item1.setProduto("lápis");
        item1.setQuantidade(100);
        item1.setPreco(new BigDecimal("1.10"));

        OrderItemMessage item2 = new OrderItemMessage();
        item2.setProduto("caderno");
        item2.setQuantidade(10);
        item2.setPreco(new BigDecimal("1.00"));

        OrderMessage message = new OrderMessage();
        message.setCodigoPedido(1001L);
        message.setCodigoCliente(1L);
        message.setItens(List.of(item1, item2));

        Order order = mapper.toDomain(message);

        assertNotNull(order);
        assertEquals(1001L, order.getOrderId());
        assertEquals(1L, order.getCustomerId());
        assertEquals(2, order.getItems().size());
        assertEquals("lápis", order.getItems().getFirst().getProduct());
        assertEquals(100, order.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("1.10"), order.getItems().get(0).getPrice());
        assertEquals("caderno", order.getItems().get(1).getProduct());
        assertEquals(10, order.getItems().get(1).getQuantity());
        assertEquals(new BigDecimal("1.00"), order.getItems().get(1).getPrice());
    }

    @Test
    @DisplayName("Should return null when OrderMessage is null")
    void shouldReturnNullWhenOrderMessageIsNull() {
        Order order = mapper.toDomain(null);
        assertNull(order);
    }

    @Test
    @DisplayName("Should map empty items list correctly")
    void shouldMapEmptyItemsListCorrectly() {
        OrderMessage message = new OrderMessage();
        message.setCodigoPedido(1002L);
        message.setCodigoCliente(2L);
        message.setItens(List.of());

        Order order = mapper.toDomain(message);

        assertNotNull(order);
        assertEquals(1002L, order.getOrderId());
        assertEquals(2L, order.getCustomerId());
        assertNotNull(order.getItems());
        assertTrue(order.getItems().isEmpty());
    }

    @Test
    @DisplayName("Should map single item correctly")
    void shouldMapSingleItemCorrectly() {
        OrderItemMessage item = new OrderItemMessage();
        item.setProduto("notebook");
        item.setQuantidade(1);
        item.setPreco(new BigDecimal("2500.00"));

        OrderMessage message = new OrderMessage();
        message.setCodigoPedido(2001L);
        message.setCodigoCliente(3L);
        message.setItens(List.of(item));

        Order order = mapper.toDomain(message);

        assertNotNull(order);
        assertEquals(2001L, order.getOrderId());
        assertEquals(3L, order.getCustomerId());
        assertEquals(1, order.getItems().size());
        assertEquals("notebook", order.getItems().getFirst().getProduct());
        assertEquals(1, order.getItems().getFirst().getQuantity());
        assertEquals(new BigDecimal("2500.00"), order.getItems().getFirst().getPrice());
    }

    @Test
    @DisplayName("Should throw NullPointerException if any item in OrderMessage is null")
    void shouldThrowExceptionIfAnyItemIsNull() {
        OrderItemMessage item1 = new OrderItemMessage();
        item1.setProduto("lápis");
        item1.setQuantidade(100);
        item1.setPreco(new BigDecimal("1.10"));

        OrderMessage message = new OrderMessage();
        message.setCodigoPedido(1003L);
        message.setCodigoCliente(4L);
        message.setItens(Arrays.asList(item1, null));

        assertThrows(NullPointerException.class, () -> mapper.toDomain(message));
    }
}
