package com.btg.challenge.orders.infra.repository.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderData Unit Tests")
class OrderDataTest {

    private OrderData orderData;
    private CustomerData customerData;
    private OrderItemData orderItemData;

    @BeforeEach
    void setUp() {
        orderData = new OrderData();
        customerData = new CustomerData();
        orderItemData = new OrderItemData();
    }

    @Test
    @DisplayName("Should create OrderData with default constructor")
    void shouldCreateOrderDataWithDefaultConstructor() {
        // When
        OrderData order = new OrderData();

        // Then
        assertNotNull(order);
        assertNull(order.getOrderId());
        assertNull(order.getCustomer());
        assertNull(order.getTotalAmount());
        assertNull(order.getItemsCount());
        assertNull(order.getCreatedAt());
        assertNull(order.getUpdatedAt());
        assertNull(order.getItems());
    }

    @Test
    @DisplayName("Should create OrderData with all args constructor")
    void shouldCreateOrderDataWithAllArgsConstructor() {
        // Given
        Long orderId = 1L;
        CustomerData customer = new CustomerData();
        BigDecimal totalAmount = BigDecimal.valueOf(100.50);
        Integer itemsCount = 3;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        List<OrderItemData> items = new ArrayList<>();

        // When
        OrderData order = new OrderData(orderId, customer, totalAmount, itemsCount, createdAt, updatedAt, items);

        // Then
        assertNotNull(order);
        assertEquals(orderId, order.getOrderId());
        assertEquals(customer, order.getCustomer());
        assertEquals(totalAmount, order.getTotalAmount());
        assertEquals(itemsCount, order.getItemsCount());
        assertEquals(createdAt, order.getCreatedAt());
        assertEquals(updatedAt, order.getUpdatedAt());
        assertEquals(items, order.getItems());
    }

    @Test
    @DisplayName("Should create OrderData with builder")
    void shouldCreateOrderDataWithBuilder() {
        // Given
        Long orderId = 123L;
        CustomerData customer = CustomerData.builder().customerId(456L).build();
        BigDecimal totalAmount = BigDecimal.valueOf(250.75);
        Integer itemsCount = 2;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        List<OrderItemData> items = new ArrayList<>();

        // When
        OrderData order = OrderData.builder()
                .orderId(orderId)
                .customer(customer)
                .totalAmount(totalAmount)
                .itemsCount(itemsCount)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .items(items)
                .build();

        // Then
        assertNotNull(order);
        assertEquals(orderId, order.getOrderId());
        assertEquals(customer, order.getCustomer());
        assertEquals(totalAmount, order.getTotalAmount());
        assertEquals(itemsCount, order.getItemsCount());
        assertEquals(createdAt, order.getCreatedAt());
        assertEquals(updatedAt, order.getUpdatedAt());
        assertEquals(items, order.getItems());
    }

    @Test
    @DisplayName("Should set and get orderId")
    void shouldSetAndGetOrderId() {
        // Given
        Long orderId = 789L;

        // When
        orderData.setOrderId(orderId);

        // Then
        assertEquals(orderId, orderData.getOrderId());
    }

    @Test
    @DisplayName("Should set and get customer")
    void shouldSetAndGetCustomer() {
        // Given
        customerData.setCustomerId(100L);

        // When
        orderData.setCustomer(customerData);

        // Then
        assertEquals(customerData, orderData.getCustomer());
        assertEquals(100L, orderData.getCustomer().getCustomerId());
    }

    @Test
    @DisplayName("Should set and get totalAmount")
    void shouldSetAndGetTotalAmount() {
        // Given
        BigDecimal totalAmount = BigDecimal.valueOf(99.99);

        // When
        orderData.setTotalAmount(totalAmount);

        // Then
        assertEquals(totalAmount, orderData.getTotalAmount());
        assertEquals(0, totalAmount.compareTo(orderData.getTotalAmount()));
    }

    @Test
    @DisplayName("Should set and get itemsCount")
    void shouldSetAndGetItemsCount() {
        // Given
        Integer itemsCount = 5;

        // When
        orderData.setItemsCount(itemsCount);

        // Then
        assertEquals(itemsCount, orderData.getItemsCount());
    }

    @Test
    @DisplayName("Should set and get createdAt")
    void shouldSetAndGetCreatedAt() {
        // Given
        LocalDateTime createdAt = LocalDateTime.now();

        // When
        orderData.setCreatedAt(createdAt);

        // Then
        assertEquals(createdAt, orderData.getCreatedAt());
    }

    @Test
    @DisplayName("Should set and get updatedAt")
    void shouldSetAndGetUpdatedAt() {
        // Given
        LocalDateTime updatedAt = LocalDateTime.now();

        // When
        orderData.setUpdatedAt(updatedAt);

        // Then
        assertEquals(updatedAt, orderData.getUpdatedAt());
    }

    @Test
    @DisplayName("Should set and get items")
    void shouldSetAndGetItems() {
        // Given
        List<OrderItemData> items = new ArrayList<>();
        items.add(orderItemData);

        // When
        orderData.setItems(items);

        // Then
        assertEquals(items, orderData.getItems());
        assertEquals(1, orderData.getItems().size());
    }

    @Test
    @DisplayName("Should handle null customer")
    void shouldHandleNullCustomer() {
        // When
        orderData.setCustomer(null);

        // Then
        assertNull(orderData.getCustomer());
    }

    @Test
    @DisplayName("Should handle null totalAmount")
    void shouldHandleNullTotalAmount() {
        // When
        orderData.setTotalAmount(null);

        // Then
        assertNull(orderData.getTotalAmount());
    }

    @Test
    @DisplayName("Should handle zero totalAmount")
    void shouldHandleZeroTotalAmount() {
        // Given
        BigDecimal zeroAmount = BigDecimal.ZERO;

        // When
        orderData.setTotalAmount(zeroAmount);

        // Then
        assertEquals(BigDecimal.ZERO, orderData.getTotalAmount());
        assertEquals(0, orderData.getTotalAmount().compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Should handle null itemsCount")
    void shouldHandleNullItemsCount() {
        // When
        orderData.setItemsCount(null);

        // Then
        assertNull(orderData.getItemsCount());
    }

    @Test
    @DisplayName("Should handle zero itemsCount")
    void shouldHandleZeroItemsCount() {
        // When
        orderData.setItemsCount(0);

        // Then
        assertEquals(Integer.valueOf(0), orderData.getItemsCount());
    }

    @Test
    @DisplayName("Should handle null items list")
    void shouldHandleNullItemsList() {
        // When
        orderData.setItems(null);

        // Then
        assertNull(orderData.getItems());
    }

    @Test
    @DisplayName("Should handle empty items list")
    void shouldHandleEmptyItemsList() {
        // Given
        List<OrderItemData> emptyItems = new ArrayList<>();

        // When
        orderData.setItems(emptyItems);

        // Then
        assertNotNull(orderData.getItems());
        assertTrue(orderData.getItems().isEmpty());
    }

    @Test
    @DisplayName("Should implement equals correctly based on orderId")
    void shouldImplementEqualsCorrectlyBasedOnOrderId() {
        // Given
        Long orderId = 999L;
        OrderData order1 = OrderData.builder()
                .orderId(orderId)
                .totalAmount(BigDecimal.valueOf(100.00))
                .build();

        OrderData order2 = OrderData.builder()
                .orderId(orderId)
                .totalAmount(BigDecimal.valueOf(200.00))
                .build();

        OrderData order3 = OrderData.builder()
                .orderId(888L)
                .totalAmount(BigDecimal.valueOf(100.00))
                .build();

        // Then
        assertEquals(order1, order2); // Same orderId
        assertNotEquals(order1, order3); // Different orderId
        assertNotNull(order1);
        assertNotEquals("not an order", order1);
    }

    @Test
    @DisplayName("Should implement hashCode correctly based on orderId")
    void shouldImplementHashCodeCorrectlyBasedOnOrderId() {
        // Given
        Long orderId = 777L;
        OrderData order1 = OrderData.builder()
                .orderId(orderId)
                .build();

        OrderData order2 = OrderData.builder()
                .orderId(orderId)
                .build();

        // Then
        assertEquals(order1.hashCode(), order2.hashCode());
    }

    @Test
    @DisplayName("Should implement toString correctly")
    void shouldImplementToStringCorrectly() {
        // Given
        Long orderId = 555L;
        BigDecimal totalAmount = BigDecimal.valueOf(150.50);
        Integer itemsCount = 2;

        OrderData order = OrderData.builder()
                .orderId(orderId)
                .totalAmount(totalAmount)
                .itemsCount(itemsCount)
                .build();

        // When
        String toString = order.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("OrderData"));
        assertTrue(toString.contains("orderId=" + orderId));
        assertTrue(toString.contains("totalAmount=" + totalAmount));
        assertTrue(toString.contains("itemsCount=" + itemsCount));
    }

    @Test
    @DisplayName("Should handle null orderId in equals")
    void shouldHandleNullOrderIdInEquals() {
        // Given
        OrderData order1 = new OrderData();
        OrderData order2 = new OrderData();
        OrderData order3 = OrderData.builder().orderId(1L).build();

        // Then
        assertEquals(order1, order2); // Both have null orderId
        assertNotEquals(order1, order3); // One null, one not null
        assertNotEquals(order3, order1); // One not null, one null
    }

    @Test
    @DisplayName("Should allow modification of items list")
    void shouldAllowModificationOfItemsList() {
        // Given
        List<OrderItemData> items = new ArrayList<>();
        orderData.setItems(items);

        // When
        OrderItemData newItem = new OrderItemData();
        orderData.getItems().add(newItem);

        // Then
        assertEquals(1, orderData.getItems().size());
        assertEquals(newItem, orderData.getItems().getFirst());
    }

    @Test
    @DisplayName("Should handle large totalAmount values")
    void shouldHandleLargeTotalAmountValues() {
        // Given
        BigDecimal largeAmount = new BigDecimal("999999999999999.99");

        // When
        orderData.setTotalAmount(largeAmount);

        // Then
        assertEquals(largeAmount, orderData.getTotalAmount());
        assertEquals(0, largeAmount.compareTo(orderData.getTotalAmount()));
    }

    @Test
    @DisplayName("Should handle negative totalAmount")
    void shouldHandleNegativeTotalAmount() {
        // Given
        BigDecimal negativeAmount = BigDecimal.valueOf(-50.00);

        // When
        orderData.setTotalAmount(negativeAmount);

        // Then
        assertEquals(negativeAmount, orderData.getTotalAmount());
        assertTrue(orderData.getTotalAmount().compareTo(BigDecimal.ZERO) < 0);
    }
}
