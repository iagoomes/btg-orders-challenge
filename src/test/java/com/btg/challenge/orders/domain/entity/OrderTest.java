package com.btg.challenge.orders.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order Unit Tests")
class OrderTest {

    private Order order;
    private List<OrderItem> items;

    @BeforeEach
    void setUp() {
        order = new Order();
        items = new ArrayList<>();
    }

    @Test
    @DisplayName("Should create Order with default constructor")
    void shouldCreateOrderWithDefaultConstructor() {
        // Then
        assertNotNull(order);
        assertNull(order.getOrderId());
        assertNull(order.getCustomerId());
        assertNull(order.getTotalAmount());
        assertNull(order.getItemsCount());
        assertNull(order.getCreatedAt());
        assertNull(order.getItems());
    }

    @Test
    @DisplayName("Should create Order with parametrized constructor and update totals")
    void shouldCreateOrderWithParametrizedConstructorAndUpdateTotals() {
        // Given
        Long orderId = 1L;
        Long customerId = 100L;
        OrderItem item1 = new OrderItem("Notebook", 1, new BigDecimal("1500.00"));
        OrderItem item2 = new OrderItem("Mouse", 2, new BigDecimal("50.00"));
        items.add(item1);
        items.add(item2);

        // When
        Order newOrder = new Order(orderId, customerId, items);

        // Then
        assertNotNull(newOrder);
        assertEquals(orderId, newOrder.getOrderId());
        assertEquals(customerId, newOrder.getCustomerId());
        assertEquals(2, newOrder.getItemsCount());
        assertEquals(new BigDecimal("1600.00"), newOrder.getTotalAmount());
        assertEquals(items, newOrder.getItems());
    }

    @Test
    @DisplayName("Should calculate total amount correctly")
    void shouldCalculateTotalAmountCorrectly() {
        // Given
        OrderItem item1 = new OrderItem("Product1", 2, new BigDecimal("10.00"));
        OrderItem item2 = new OrderItem("Product2", 3, new BigDecimal("15.50"));
        OrderItem item3 = new OrderItem("Product3", 1, new BigDecimal("100.00"));
        items.add(item1);
        items.add(item2);
        items.add(item3);
        order.setItems(items);

        // When
        BigDecimal totalAmount = order.calculateTotalAmount();

        // Then
        assertEquals(new BigDecimal("166.50"), totalAmount);
    }

    @Test
    @DisplayName("Should return zero total amount for empty items list")
    void shouldReturnZeroTotalAmountForEmptyItemsList() {
        // Given
        order.setItems(new ArrayList<>());

        // When
        BigDecimal totalAmount = order.calculateTotalAmount();

        // Then
        assertEquals(BigDecimal.ZERO, totalAmount);
    }

    @Test
    @DisplayName("Should return zero total amount for null items list")
    void shouldReturnZeroTotalAmountForNullItemsList() {
        // Given
        order.setItems(null);

        // When
        BigDecimal totalAmount = order.calculateTotalAmount();

        // Then
        assertEquals(BigDecimal.ZERO, totalAmount);
    }

    @Test
    @DisplayName("Should update totals when items are set")
    void shouldUpdateTotalsWhenItemsAreSet() {
        // Given
        OrderItem item1 = new OrderItem("Product1", 1, new BigDecimal("25.00"));
        OrderItem item2 = new OrderItem("Product2", 2, new BigDecimal("30.00"));
        items.add(item1);
        items.add(item2);

        // When
        order.setItems(items);

        // Then
        assertEquals(2, order.getItemsCount());
        assertEquals(new BigDecimal("85.00"), order.getTotalAmount());
    }

    @Test
    @DisplayName("Should update totals manually")
    void shouldUpdateTotalsManually() {
        // Given
        OrderItem item = new OrderItem("Product", 3, new BigDecimal("20.00"));
        items.add(item);
        order.setItems(items);

        // Modify item after setting
        item.setQuantity(5);

        // When
        order.updateTotals();

        // Then
        assertEquals(1, order.getItemsCount());
        assertEquals(new BigDecimal("100.00"), order.getTotalAmount());
    }

    @Test
    @DisplayName("Should validate Order correctly")
    void shouldValidateOrderCorrectly() {
        // Given - invalid cases
        assertFalse(order.isValid()); // all null

        order.setOrderId(1L);
        assertFalse(order.isValid()); // missing customerId and items

        order.setCustomerId(100L);
        assertFalse(order.isValid()); // missing items

        order.setItems(new ArrayList<>());
        assertFalse(order.isValid()); // empty items

        // Add invalid item
        OrderItem invalidItem = new OrderItem();
        items.add(invalidItem);
        order.setItems(items);
        assertFalse(order.isValid()); // invalid item

        // Given - valid case
        items.clear();
        OrderItem validItem = new OrderItem("Product", 1, new BigDecimal("10.00"));
        items.add(validItem);
        order.setOrderId(1L);
        order.setCustomerId(100L);
        order.setItems(items);

        // Then
        assertTrue(order.isValid());
    }

    @Test
    @DisplayName("Should add item correctly")
    void shouldAddItemCorrectly() {
        // Given
        OrderItem existingItem = new OrderItem("Product1", 1, new BigDecimal("10.00"));
        items.add(existingItem);
        order.setItems(items);

        OrderItem newItem = new OrderItem("Product2", 2, new BigDecimal("15.00"));

        // When
        order.addItem(newItem);

        // Then
        assertEquals(2, order.getItems().size());
        assertTrue(order.getItems().contains(newItem));
        assertEquals(2, order.getItemsCount());
        assertEquals(new BigDecimal("40.00"), order.getTotalAmount());
    }

    @Test
    @DisplayName("Should not add item when items list is null")
    void shouldNotAddItemWhenItemsListIsNull() {
        // Given
        order.setItems(null);
        OrderItem newItem = new OrderItem("Product", 1, new BigDecimal("10.00"));

        // When
        order.addItem(newItem);

        // Then
        assertNull(order.getItems());
    }

    @Test
    @DisplayName("Should handle null items count correctly when items is null")
    void shouldHandleNullItemsCountCorrectlyWhenItemsIsNull() {
        // Given
        order.setItems(null);

        // When
        order.updateTotals();

        // Then
        assertEquals(0, order.getItemsCount());
        assertEquals(BigDecimal.ZERO, order.getTotalAmount());
    }

    @Test
    @DisplayName("Should set and get all properties correctly")
    void shouldSetAndGetAllPropertiesCorrectly() {
        // Given
        Long orderId = 123L;
        Long customerId = 456L;
        LocalDateTime createdAt = LocalDateTime.now();
        OrderItem item = new OrderItem("Product", 1, new BigDecimal("10.00"));
        items.add(item);

        // When
        order.setOrderId(orderId);
        order.setCustomerId(customerId);
        order.setCreatedAt(createdAt);
        order.setItems(items);
        // Set values after setItems to override the calculated values
        Integer itemsCount = 3;
        BigDecimal totalAmount = new BigDecimal("250.00");
        order.setItemsCount(itemsCount);
        order.setTotalAmount(totalAmount);

        // Then
        assertEquals(orderId, order.getOrderId());
        assertEquals(customerId, order.getCustomerId());
        assertEquals(totalAmount, order.getTotalAmount());
        assertEquals(itemsCount, order.getItemsCount());
        assertEquals(createdAt, order.getCreatedAt());
        assertEquals(items, order.getItems());
    }

    @Test
    @DisplayName("Should handle complex calculation with multiple items")
    void shouldHandleComplexCalculationWithMultipleItems() {
        // Given
        OrderItem item1 = new OrderItem("Expensive Item", 1, new BigDecimal("999.99"));
        OrderItem item2 = new OrderItem("Cheap Item", 100, new BigDecimal("0.01"));
        OrderItem item3 = new OrderItem("Medium Item", 5, new BigDecimal("123.45"));
        items.add(item1);
        items.add(item2);
        items.add(item3);

        // When
        order.setItems(items);

        // Then
        assertEquals(3, order.getItemsCount());
        // 999.99 + (100 * 0.01) + (5 * 123.45) = 999.99 + 1.00 + 617.25 = 1618.24
        assertEquals(new BigDecimal("1618.24"), order.getTotalAmount());
    }

    @Test
    @DisplayName("Should update item total prices when updating order totals")
    void shouldUpdateItemTotalPricesWhenUpdatingOrderTotals() {
        // Given
        OrderItem item = new OrderItem("Product", 2, new BigDecimal("10.00"));
        items.add(item);
        order.setItems(items);

        // Modify item properties directly
        item.setQuantity(3);
        item.setPrice(new BigDecimal("15.00"));

        // When
        order.updateTotals();

        // Then
        assertEquals(new BigDecimal("45.00"), item.getTotalPrice());
        assertEquals(new BigDecimal("45.00"), order.getTotalAmount());
    }
}
