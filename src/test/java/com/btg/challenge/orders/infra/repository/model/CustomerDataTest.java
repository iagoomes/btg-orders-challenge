package com.btg.challenge.orders.infra.repository.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CustomerData Unit Tests")
class CustomerDataTest {

    private CustomerData customerData;
    private OrderData orderData;

    @BeforeEach
    void setUp() {
        customerData = new CustomerData();
        orderData = new OrderData();
    }

    @Test
    @DisplayName("Should create CustomerData with default constructor")
    void shouldCreateCustomerDataWithDefaultConstructor() {
        // When
        CustomerData customer = new CustomerData();

        // Then
        assertNotNull(customer);
        assertNull(customer.getCustomerId());
        assertNull(customer.getCreatedAt());
        assertNull(customer.getUpdatedAt());
        assertNull(customer.getOrders());
    }

    @Test
    @DisplayName("Should create CustomerData with all args constructor")
    void shouldCreateCustomerDataWithAllArgsConstructor() {
        // Given
        Long customerId = 1L;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        List<OrderData> orders = new ArrayList<>();

        // When
        CustomerData customer = new CustomerData(customerId, createdAt, updatedAt, orders);

        // Then
        assertNotNull(customer);
        assertEquals(customerId, customer.getCustomerId());
        assertEquals(createdAt, customer.getCreatedAt());
        assertEquals(updatedAt, customer.getUpdatedAt());
        assertEquals(orders, customer.getOrders());
    }

    @Test
    @DisplayName("Should create CustomerData with builder")
    void shouldCreateCustomerDataWithBuilder() {
        // Given
        Long customerId = 123L;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        List<OrderData> orders = new ArrayList<>();

        // When
        CustomerData customer = CustomerData.builder()
                .customerId(customerId)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .orders(orders)
                .build();

        // Then
        assertNotNull(customer);
        assertEquals(customerId, customer.getCustomerId());
        assertEquals(createdAt, customer.getCreatedAt());
        assertEquals(updatedAt, customer.getUpdatedAt());
        assertEquals(orders, customer.getOrders());
    }

    @Test
    @DisplayName("Should set and get customerId")
    void shouldSetAndGetCustomerId() {
        // Given
        Long customerId = 456L;

        // When
        customerData.setCustomerId(customerId);

        // Then
        assertEquals(customerId, customerData.getCustomerId());
    }

    @Test
    @DisplayName("Should set and get createdAt")
    void shouldSetAndGetCreatedAt() {
        // Given
        LocalDateTime createdAt = LocalDateTime.now();

        // When
        customerData.setCreatedAt(createdAt);

        // Then
        assertEquals(createdAt, customerData.getCreatedAt());
    }

    @Test
    @DisplayName("Should set and get updatedAt")
    void shouldSetAndGetUpdatedAt() {
        // Given
        LocalDateTime updatedAt = LocalDateTime.now();

        // When
        customerData.setUpdatedAt(updatedAt);

        // Then
        assertEquals(updatedAt, customerData.getUpdatedAt());
    }

    @Test
    @DisplayName("Should set and get orders")
    void shouldSetAndGetOrders() {
        // Given
        List<OrderData> orders = new ArrayList<>();
        orders.add(orderData);

        // When
        customerData.setOrders(orders);

        // Then
        assertEquals(orders, customerData.getOrders());
        assertEquals(1, customerData.getOrders().size());
    }

    @Test
    @DisplayName("Should handle null orders list")
    void shouldHandleNullOrdersList() {
        // When
        customerData.setOrders(null);

        // Then
        assertNull(customerData.getOrders());
    }

    @Test
    @DisplayName("Should handle empty orders list")
    void shouldHandleEmptyOrdersList() {
        // Given
        List<OrderData> emptyOrders = new ArrayList<>();

        // When
        customerData.setOrders(emptyOrders);

        // Then
        assertNotNull(customerData.getOrders());
        assertTrue(customerData.getOrders().isEmpty());
    }

    @Test
    @DisplayName("Should implement equals correctly based on customerId")
    void shouldImplementEqualsCorrectlyBasedOnCustomerId() {
        // Given
        Long customerId = 789L;
        CustomerData customer1 = CustomerData.builder()
                .customerId(customerId)
                .createdAt(LocalDateTime.now())
                .build();

        CustomerData customer2 = CustomerData.builder()
                .customerId(customerId)
                .createdAt(LocalDateTime.now().plusHours(1))
                .build();

        CustomerData customer3 = CustomerData.builder()
                .customerId(999L)
                .createdAt(LocalDateTime.now())
                .build();

        // Then
        assertEquals(customer1, customer2); // Same customerId
        assertNotEquals(customer1, customer3); // Different customerId
        assertNotNull(customer1);
        assertNotEquals("not a customer", customer1);
    }

    @Test
    @DisplayName("Should implement hashCode correctly based on customerId")
    void shouldImplementHashCodeCorrectlyBasedOnCustomerId() {
        // Given
        Long customerId = 555L;
        CustomerData customer1 = CustomerData.builder()
                .customerId(customerId)
                .build();

        CustomerData customer2 = CustomerData.builder()
                .customerId(customerId)
                .build();

        // Then
        assertEquals(customer1.hashCode(), customer2.hashCode());
    }

    @Test
    @DisplayName("Should implement toString correctly")
    void shouldImplementToStringCorrectly() {
        // Given
        Long customerId = 777L;
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 10, 0, 0);

        CustomerData customer = CustomerData.builder()
                .customerId(customerId)
                .createdAt(createdAt)
                .build();

        // When
        String toString = customer.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("CustomerData"));
        assertTrue(toString.contains("customerId=" + customerId));
        assertTrue(toString.contains("createdAt=" + createdAt));
    }

    @Test
    @DisplayName("Should handle null customerId in equals")
    void shouldHandleNullCustomerIdInEquals() {
        // Given
        CustomerData customer1 = new CustomerData();
        CustomerData customer2 = new CustomerData();
        CustomerData customer3 = CustomerData.builder().customerId(1L).build();

        // Then
        assertEquals(customer1, customer2); // Both have null customerId
        assertNotEquals(customer1, customer3); // One null, one not null
        assertNotEquals(customer3, customer1); // One not null, one null
    }

    @Test
    @DisplayName("Should allow modification of orders list")
    void shouldAllowModificationOfOrdersList() {
        // Given
        List<OrderData> orders = new ArrayList<>();
        customerData.setOrders(orders);

        // When
        OrderData newOrder = new OrderData();
        customerData.getOrders().add(newOrder);

        // Then
        assertEquals(1, customerData.getOrders().size());
        assertEquals(newOrder, customerData.getOrders().getFirst());
    }
}
