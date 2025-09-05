package com.btg.challenge.orders.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Customer Unit Tests")
class CustomerTest {

    private Customer customer;
    private List<Order> orders;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        orders = new ArrayList<>();
    }

    @Test
    @DisplayName("Should create Customer with default constructor")
    void shouldCreateCustomerWithDefaultConstructor() {
        // Then
        assertNotNull(customer);
        assertNull(customer.getCustomerId());
        assertNull(customer.getCreatedAt());
        assertNull(customer.getOrders());
    }

    @Test
    @DisplayName("Should create Customer with customerId constructor")
    void shouldCreateCustomerWithCustomerIdConstructor() {
        // Given
        Long customerId = 123L;

        // When
        Customer newCustomer = new Customer(customerId);

        // Then
        assertNotNull(newCustomer);
        assertEquals(customerId, newCustomer.getCustomerId());
        assertNull(newCustomer.getCreatedAt());
        assertNull(newCustomer.getOrders());
    }

    @Test
    @DisplayName("Should create Customer with customerId and orders constructor")
    void shouldCreateCustomerWithCustomerIdAndOrdersConstructor() {
        // Given
        Long customerId = 456L;
        OrderItem item = new OrderItem("Product", 1, new BigDecimal("10.00"));
        List<OrderItem> items = List.of(item);
        Order order = new Order(1L, customerId, items);
        orders.add(order);

        // When
        Customer newCustomer = new Customer(customerId, orders);

        // Then
        assertNotNull(newCustomer);
        assertEquals(customerId, newCustomer.getCustomerId());
        assertEquals(orders, newCustomer.getOrders());
        assertEquals(1, newCustomer.getTotalOrders());
    }

    @Test
    @DisplayName("Should get total orders correctly")
    void shouldGetTotalOrdersCorrectly() {
        // Given - no orders
        assertEquals(0, customer.getTotalOrders());

        // Given - with orders
        OrderItem item1 = new OrderItem("Product1", 1, new BigDecimal("10.00"));
        OrderItem item2 = new OrderItem("Product2", 2, new BigDecimal("15.00"));
        Order order1 = new Order(1L, 100L, List.of(item1));
        Order order2 = new Order(2L, 100L, List.of(item2));
        orders.add(order1);
        orders.add(order2);
        customer.setOrders(orders);

        // Then
        assertEquals(2, customer.getTotalOrders());
    }

    @Test
    @DisplayName("Should return zero total orders when orders list is null")
    void shouldReturnZeroTotalOrdersWhenOrdersListIsNull() {
        // Given
        customer.setOrders(null);

        // When
        int totalOrders = customer.getTotalOrders();

        // Then
        assertEquals(0, totalOrders);
    }

    @Test
    @DisplayName("Should check if customer has orders correctly")
    void shouldCheckIfCustomerHasOrdersCorrectly() {
        // Given - no orders
        assertFalse(customer.hasOrders());

        // Given - null orders
        customer.setOrders(null);
        assertFalse(customer.hasOrders());

        // Given - empty orders
        customer.setOrders(new ArrayList<>());
        assertFalse(customer.hasOrders());

        // Given - with orders
        OrderItem item = new OrderItem("Product", 1, new BigDecimal("10.00"));
        Order order = new Order(1L, 100L, List.of(item));
        orders.add(order);
        customer.setOrders(orders);

        // Then
        assertTrue(customer.hasOrders());
    }

    @Test
    @DisplayName("Should validate customer correctly")
    void shouldValidateCustomerCorrectly() {
        // Given - invalid cases
        assertFalse(customer.isValid()); // null customerId

        customer.setCustomerId(0L);
        assertFalse(customer.isValid()); // zero customerId

        customer.setCustomerId(-1L);
        assertFalse(customer.isValid()); // negative customerId

        // Given - valid case
        customer.setCustomerId(1L);

        // Then
        assertTrue(customer.isValid());
    }

    @Test
    @DisplayName("Should add order correctly when conditions are met")
    void shouldAddOrderCorrectlyWhenConditionsAreMet() {
        // Given
        Long customerId = 100L;
        customer.setCustomerId(customerId);
        customer.setOrders(new ArrayList<>());

        OrderItem item = new OrderItem("Product", 1, new BigDecimal("10.00"));
        Order order = new Order(1L, customerId, List.of(item));

        // When
        customer.addOrder(order);

        // Then
        assertEquals(1, customer.getOrders().size());
        assertTrue(customer.getOrders().contains(order));
        assertEquals(1, customer.getTotalOrders());
    }

    @Test
    @DisplayName("Should not add order when orders list is null")
    void shouldNotAddOrderWhenOrdersListIsNull() {
        // Given
        Long customerId = 100L;
        customer.setCustomerId(customerId);
        customer.setOrders(null);

        OrderItem item = new OrderItem("Product", 1, new BigDecimal("10.00"));
        Order order = new Order(1L, customerId, List.of(item));

        // When
        customer.addOrder(order);

        // Then
        assertNull(customer.getOrders());
    }

    @Test
    @DisplayName("Should not add order when order is null")
    void shouldNotAddOrderWhenOrderIsNull() {
        // Given
        customer.setCustomerId(100L);
        customer.setOrders(new ArrayList<>());

        // When
        customer.addOrder(null);

        // Then
        assertEquals(0, customer.getOrders().size());
    }

    @Test
    @DisplayName("Should not add order when customer IDs do not match")
    void shouldNotAddOrderWhenCustomerIdsDoNotMatch() {
        // Given
        Long customerId = 100L;
        Long differentCustomerId = 200L;
        customer.setCustomerId(customerId);
        customer.setOrders(new ArrayList<>());

        OrderItem item = new OrderItem("Product", 1, new BigDecimal("10.00"));
        Order order = new Order(1L, differentCustomerId, List.of(item));

        // When
        customer.addOrder(order);

        // Then
        assertEquals(0, customer.getOrders().size());
    }

    @Test
    @DisplayName("Should set and get all properties correctly")
    void shouldSetAndGetAllPropertiesCorrectly() {
        // Given
        Long customerId = 789L;
        LocalDateTime createdAt = LocalDateTime.now();
        OrderItem item = new OrderItem("Product", 1, new BigDecimal("10.00"));
        Order order = new Order(1L, customerId, List.of(item));
        orders.add(order);

        // When
        customer.setCustomerId(customerId);
        customer.setCreatedAt(createdAt);
        customer.setOrders(orders);

        // Then
        assertEquals(customerId, customer.getCustomerId());
        assertEquals(createdAt, customer.getCreatedAt());
        assertEquals(orders, customer.getOrders());
    }

    @Test
    @DisplayName("Should handle multiple orders correctly")
    void shouldHandleMultipleOrdersCorrectly() {
        // Given
        Long customerId = 100L;
        customer.setCustomerId(customerId);
        customer.setOrders(new ArrayList<>());

        OrderItem item1 = new OrderItem("Product1", 1, new BigDecimal("10.00"));
        OrderItem item2 = new OrderItem("Product2", 2, new BigDecimal("15.00"));
        OrderItem item3 = new OrderItem("Product3", 1, new BigDecimal("50.00"));

        Order order1 = new Order(1L, customerId, List.of(item1));
        Order order2 = new Order(2L, customerId, List.of(item2));
        Order order3 = new Order(3L, customerId, List.of(item3));

        // When
        customer.addOrder(order1);
        customer.addOrder(order2);
        customer.addOrder(order3);

        // Then
        assertEquals(3, customer.getTotalOrders());
        assertTrue(customer.hasOrders());
        assertTrue(customer.getOrders().contains(order1));
        assertTrue(customer.getOrders().contains(order2));
        assertTrue(customer.getOrders().contains(order3));
    }

    @Test
    @DisplayName("Should maintain consistency after multiple operations")
    void shouldMaintainConsistencyAfterMultipleOperations() {
        // Given
        Long customerId = 500L;
        customer.setCustomerId(customerId);
        customer.setOrders(new ArrayList<>());

        // When - add orders
        OrderItem item1 = new OrderItem("Product1", 2, new BigDecimal("20.00"));
        Order order1 = new Order(1L, customerId, List.of(item1));
        customer.addOrder(order1);

        OrderItem item2 = new OrderItem("Product2", 1, new BigDecimal("30.00"));
        Order order2 = new Order(2L, customerId, List.of(item2));
        customer.addOrder(order2);

        // Then
        assertTrue(customer.isValid());
        assertTrue(customer.hasOrders());
        assertEquals(2, customer.getTotalOrders());

        // When - try to add order with wrong customer ID
        OrderItem item3 = new OrderItem("Product3", 1, new BigDecimal("40.00"));
        Order wrongOrder = new Order(3L, 999L, List.of(item3));
        customer.addOrder(wrongOrder);

        // Then - should not be added
        assertEquals(2, customer.getTotalOrders());
        assertFalse(customer.getOrders().contains(wrongOrder));
    }
}
