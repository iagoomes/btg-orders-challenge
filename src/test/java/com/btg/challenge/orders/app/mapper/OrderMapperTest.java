package com.btg.challenge.orders.app.mapper;

import com.btg.challenge.orders.domain.entity.Order;
import com.btg.challenge.orders.domain.entity.OrderItem;
import com.btg.challenge.orders.model.OrderItemSummary;
import com.btg.challenge.orders.model.OrderSummary;
import com.btg.challenge.orders.model.OrderTotalResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderMapper Unit Tests")
class OrderMapperTest {

    private final OrderMapper orderMapper = new OrderMapper();

    @Test
    @DisplayName("Should map Order to OrderTotalResponse correctly")
    void shouldMapOrderToOrderTotalResponseCorrectly() {
        Order order = new Order();
        order.setOrderId(1001L);
        order.setTotalAmount(new BigDecimal("120.50"));

        OrderTotalResponse response = orderMapper.toOrderTotalResponse(order);

        assertNotNull(response);
        assertEquals(1001L, response.getOrderId());
        assertEquals(120.50, response.getTotal());
        assertEquals("BRL", response.getCurrency());
    }

    @Test
    @DisplayName("Should map Order to OrderSummary with items")
    void shouldMapOrderToOrderSummaryWithItems() {
        OrderItem item1 = new OrderItem();
        item1.setProduct("lápis");
        item1.setQuantity(100);
        item1.setPrice(new BigDecimal("1.10"));

        OrderItem item2 = new OrderItem();
        item2.setProduct("caderno");
        item2.setQuantity(10);
        item2.setPrice(new BigDecimal("1.00"));

        Order order = new Order();
        order.setOrderId(1001L);
        order.setCustomerId(1L);
        order.setItems(List.of(item1, item2));
        order.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30));
        order.updateTotals();

        BigDecimal expectedTotal = item1.getPrice().multiply(BigDecimal.valueOf(item1.getQuantity()))
                .add(item2.getPrice().multiply(BigDecimal.valueOf(item2.getQuantity())));

        OrderSummary summary = orderMapper.toOrderSummary(order);

        assertNotNull(summary);
        assertEquals(1001L, summary.getOrderId());
        assertEquals(1L, summary.getCustomerId());
        assertEquals(expectedTotal, summary.getTotalAmount());
        assertEquals(2, summary.getItemsCount());
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 30), summary.getCreatedAt());
        assertNotNull(summary.getItems());
        assertEquals(2, summary.getItems().size());
        assertEquals("lápis", summary.getItems().getFirst().getProduct());
        assertEquals(100, summary.getItems().get(0).getQuantity());
        assertEquals(1.10, summary.getItems().get(0).getPrice());
        assertEquals("caderno", summary.getItems().get(1).getProduct());
        assertEquals(10, summary.getItems().get(1).getQuantity());
        assertEquals(1.00, summary.getItems().get(1).getPrice());
    }

    @Test
    @DisplayName("Should map Order to OrderSummary with null items")
    void shouldMapOrderToOrderSummaryWithNullItems() {
        Order order = new Order();
        order.setOrderId(1002L);
        order.setCustomerId(2L);
        order.setItems(null);
        order.setCreatedAt(LocalDateTime.of(2024, 2, 10, 8, 0));
        order.updateTotals();

        OrderSummary summary = orderMapper.toOrderSummary(order);

        assertNotNull(summary);
        assertEquals(1002L, summary.getOrderId());
        assertEquals(2L, summary.getCustomerId());
        assertEquals(BigDecimal.ZERO, summary.getTotalAmount());
        assertEquals(0, summary.getItemsCount());
        assertEquals(LocalDateTime.of(2024, 2, 10, 8, 0), summary.getCreatedAt());
        assertTrue(summary.getItems() == null || summary.getItems().isEmpty());
    }

    @Test
    @DisplayName("Should map OrderItem to OrderItemSummary correctly")
    void shouldMapOrderItemToOrderItemSummaryCorrectly() {
        OrderItem item = new OrderItem();
        item.setProduct("borracha");
        item.setQuantity(5);
        item.setPrice(new BigDecimal("2.50"));

        Order order = new Order();
        order.setItems(List.of(item));
        order.updateTotals();
        OrderSummary summary = orderMapper.toOrderSummary(order);
        OrderItemSummary itemSummary = summary.getItems().getFirst();

        assertNotNull(itemSummary);
        assertEquals("borracha", itemSummary.getProduct());
        assertEquals(5, itemSummary.getQuantity());
        assertEquals(2.50, itemSummary.getPrice());
    }

    @Test
    @DisplayName("Should handle Order with null values in toOrderTotalResponse")
    void shouldHandleOrderWithNullValuesInToOrderTotalResponse() {
        // Given
        Order order = new Order();
        order.setOrderId(null);
        order.setTotalAmount(null);

        // When & Then
        assertThrows(NullPointerException.class, () -> orderMapper.toOrderTotalResponse(order));
    }

    @Test
    @DisplayName("Should handle zero total amount in toOrderTotalResponse")
    void shouldHandleZeroTotalAmountInToOrderTotalResponse() {
        // Given
        Order order = new Order();
        order.setOrderId(2001L);
        order.setTotalAmount(BigDecimal.ZERO);

        // When
        OrderTotalResponse response = orderMapper.toOrderTotalResponse(order);

        // Then
        assertNotNull(response);
        assertEquals(2001L, response.getOrderId());
        assertEquals(0.0, response.getTotal());
        assertEquals("BRL", response.getCurrency());
    }

    @Test
    @DisplayName("Should handle large total amount in toOrderTotalResponse")
    void shouldHandleLargeTotalAmountInToOrderTotalResponse() {
        // Given
        Order order = new Order();
        order.setOrderId(3001L);
        order.setTotalAmount(new BigDecimal("999999.99"));

        // When
        OrderTotalResponse response = orderMapper.toOrderTotalResponse(order);

        // Then
        assertNotNull(response);
        assertEquals(3001L, response.getOrderId());
        assertEquals(999999.99, response.getTotal());
        assertEquals("BRL", response.getCurrency());
    }

    @Test
    @DisplayName("Should handle Order with empty items list in toOrderSummary")
    void shouldHandleOrderWithEmptyItemsListInToOrderSummary() {
        // Given
        Order order = new Order();
        order.setOrderId(4001L);
        order.setCustomerId(4L);
        order.setItems(List.of());
        order.setCreatedAt(LocalDateTime.of(2024, 3, 1, 12, 0));
        order.updateTotals();

        // When
        OrderSummary summary = orderMapper.toOrderSummary(order);

        // Then
        assertNotNull(summary);
        assertEquals(4001L, summary.getOrderId());
        assertEquals(4L, summary.getCustomerId());
        assertEquals(BigDecimal.ZERO, summary.getTotalAmount());
        assertEquals(0, summary.getItemsCount());
        assertEquals(LocalDateTime.of(2024, 3, 1, 12, 0), summary.getCreatedAt());
        assertNotNull(summary.getItems());
        assertTrue(summary.getItems().isEmpty());
    }

    @Test
    @DisplayName("Should handle OrderItem with null price in toOrderItemSummary")
    void shouldHandleOrderItemWithNullPriceInToOrderItemSummary() {
        // Given
        OrderItem item = new OrderItem();
        item.setProduct("item-sem-preco");
        item.setQuantity(1);
        item.setPrice(null);

        Order order = new Order();
        order.setItems(List.of(item));
        order.updateTotals();

        // When & Then
        assertThrows(NullPointerException.class, () -> orderMapper.toOrderSummary(order));
    }

    @Test
    @DisplayName("Should handle OrderItem with negative price correctly")
    void shouldHandleOrderItemWithNegativePriceCorrectly() {
        // Given
        OrderItem item = new OrderItem();
        item.setProduct("item-desconto");
        item.setQuantity(1);
        item.setPrice(new BigDecimal("-10.00"));

        Order order = new Order();
        order.setItems(List.of(item));
        order.updateTotals();
        OrderSummary summary = orderMapper.toOrderSummary(order);
        OrderItemSummary itemSummary = summary.getItems().getFirst();

        // Then
        assertNotNull(itemSummary);
        assertEquals("item-desconto", itemSummary.getProduct());
        assertEquals(1, itemSummary.getQuantity());
        assertEquals(-10.0, itemSummary.getPrice());
    }

    @Test
    @DisplayName("Should preserve decimal precision in toOrderTotalResponse")
    void shouldPreserveDecimalPrecisionInToOrderTotalResponse() {
        // Given
        Order order = new Order();
        order.setOrderId(5001L);
        order.setTotalAmount(new BigDecimal("123.456"));

        // When
        OrderTotalResponse response = orderMapper.toOrderTotalResponse(order);

        // Then
        assertNotNull(response);
        assertEquals(5001L, response.getOrderId());
        assertEquals(123.456, response.getTotal(), 0.001);
        assertEquals("BRL", response.getCurrency());
    }
}