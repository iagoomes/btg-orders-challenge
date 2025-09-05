package com.btg.challenge.orders.app.mapper;

import com.btg.challenge.orders.domain.entity.Customer;
import com.btg.challenge.orders.domain.entity.Order;
import com.btg.challenge.orders.domain.entity.OrderItem;
import com.btg.challenge.orders.model.CustomerOrderCountResponse;
import com.btg.challenge.orders.model.CustomerOrdersResponse;
import com.btg.challenge.orders.model.OrderSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerMapper Unit Tests")
class CustomerMapperTest {

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private CustomerMapper customerMapper;

    private Long customerId;
    private Order order1;
    private Order order2;
    private OrderSummary orderSummary1;
    private OrderSummary orderSummary2;
    private Pageable pageable;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customerId = 1L;
        pageable = PageRequest.of(0, 10);

        // Setup Customer
        customer = new Customer();
        customer.setCustomerId(customerId);

        // Setup Orders
        OrderItem item1 = new OrderItem();
        item1.setProduct("lápis");
        item1.setQuantity(100);
        item1.setPrice(new BigDecimal("1.10"));

        OrderItem item2 = new OrderItem();
        item2.setProduct("caderno");
        item2.setQuantity(10);
        item2.setPrice(new BigDecimal("1.00"));

        order1 = new Order();
        order1.setOrderId(1001L);
        order1.setCustomerId(customerId);
        order1.setItems(List.of(item1));
        order1.setTotalAmount(new BigDecimal("110.00"));
        order1.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30));

        order2 = new Order();
        order2.setOrderId(1002L);
        order2.setCustomerId(customerId);
        order2.setItems(List.of(item2));
        order2.setTotalAmount(new BigDecimal("10.00"));
        order2.setCreatedAt(LocalDateTime.of(2024, 1, 16, 14, 45));

        // Setup OrderSummaries
        orderSummary1 = new OrderSummary();
        orderSummary1.setOrderId(1001L);
        orderSummary1.setTotalAmount(new BigDecimal("110.00"));
        orderSummary1.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30));

        orderSummary2 = new OrderSummary();
        orderSummary2.setOrderId(1002L);
        orderSummary2.setTotalAmount(new BigDecimal("10.00"));
        orderSummary2.setCreatedAt(LocalDateTime.of(2024, 1, 16, 14, 45));
    }

    @Test
    @DisplayName("Should map customer ID and order count to CustomerOrderCountResponse")
    void shouldMapCustomerIdAndOrderCountToResponse() {
        // Given
        Long orderCount = 5L;

        // When
        CustomerOrderCountResponse result = customerMapper.toOrderCountResponse(customerId, orderCount);

        // Then
        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertEquals(orderCount, result.getOrderCount());
    }

    @Test
    @DisplayName("Should handle zero order count")
    void shouldHandleZeroOrderCount() {
        // Given
        long orderCount = 0L;

        // When
        CustomerOrderCountResponse result = customerMapper.toOrderCountResponse(customerId, orderCount);

        // Then
        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertEquals(0L, result.getOrderCount());
    }

    @Test
    @DisplayName("Should handle very large order count")
    void shouldHandleVeryLargeOrderCount() {
        // Given
        long orderCount = 1_000_000L;

        // When
        CustomerOrderCountResponse result = customerMapper.toOrderCountResponse(customerId, orderCount);

        // Then
        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertEquals(1_000_000L, result.getOrderCount());
    }

    @Test
    @DisplayName("Should handle null customer ID in order count response")
    void shouldHandleNullCustomerIdInOrderCountResponse() {
        // Given
        Long nullCustomerId = null;
        Long orderCount = 3L;

        // When
        CustomerOrderCountResponse result = customerMapper.toOrderCountResponse(nullCustomerId, orderCount);

        // Then
        assertNotNull(result);
        assertNull(result.getCustomerId());
        assertEquals(orderCount, result.getOrderCount());
    }

    @Test
    @DisplayName("Should map page with orders to CustomerOrdersResponse")
    void shouldMapPageWithOrdersToCustomerOrdersResponse() {
        // Given
        Page<Order> orderPage = new PageImpl<>(List.of(order1, order2), pageable, 2);

        when(orderMapper.toOrderSummary(order1)).thenReturn(orderSummary1);
        when(orderMapper.toOrderSummary(order2)).thenReturn(orderSummary2);

        // When
        CustomerOrdersResponse result = customerMapper.toCustomerOrdersResponse(orderPage);

        // Then
        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertEquals(2, result.getOrders().size());
        assertEquals(orderSummary1.getOrderId(), result.getOrders().get(0).getOrderId());
        assertEquals(orderSummary2.getOrderId(), result.getOrders().get(1).getOrderId());
        assertEquals(2L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getCurrentPage());
        assertEquals(10, result.getPageSize());

        verify(orderMapper, times(1)).toOrderSummary(order1);
        verify(orderMapper, times(1)).toOrderSummary(order2);
    }

    @Test
    @DisplayName("Should map empty page to CustomerOrdersResponse")
    void shouldMapEmptyPageToCustomerOrdersResponse() {
        // Given
        Page<Order> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        // When
        CustomerOrdersResponse result = customerMapper.toCustomerOrdersResponse(emptyPage);

        // Then
        assertNotNull(result);
        assertNull(result.getCustomerId()); // No orders, so no customer ID from first order
        assertTrue(result.getOrders().isEmpty());
        assertEquals(0L, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        assertEquals(0, result.getCurrentPage());
        assertEquals(10, result.getPageSize());

        verify(orderMapper, never()).toOrderSummary(any());
    }

    @Test
    @DisplayName("Should map single order page to CustomerOrdersResponse")
    void shouldMapSingleOrderPageToCustomerOrdersResponse() {
        // Given
        Page<Order> singleOrderPage = new PageImpl<>(List.of(order1), pageable, 1);

        when(orderMapper.toOrderSummary(order1)).thenReturn(orderSummary1);

        // When
        CustomerOrdersResponse result = customerMapper.toCustomerOrdersResponse(singleOrderPage);

        // Then
        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertEquals(1, result.getOrders().size());
        assertEquals(orderSummary1.getOrderId(), result.getOrders().getFirst().getOrderId());
        assertEquals(1L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getCurrentPage());
        assertEquals(10, result.getPageSize());

        verify(orderMapper, times(1)).toOrderSummary(order1);
    }

    @Test
    @DisplayName("Should map page with different page size to CustomerOrdersResponse")
    void shouldMapPageWithDifferentPageSizeToCustomerOrdersResponse() {
        // Given
        Pageable customPageable = PageRequest.of(1, 5);
        Page<Order> customPage = new PageImpl<>(List.of(order1), customPageable, 12);

        when(orderMapper.toOrderSummary(order1)).thenReturn(orderSummary1);

        // When
        CustomerOrdersResponse result = customerMapper.toCustomerOrdersResponse(customPage);

        // Then
        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertEquals(1, result.getOrders().size());
        assertEquals(12L, result.getTotalElements());
        assertEquals(3, result.getTotalPages()); // ceil(12/5)
        assertEquals(1, result.getCurrentPage());
        assertEquals(5, result.getPageSize());

        verify(orderMapper, times(1)).toOrderSummary(order1);
    }

    @Test
    @DisplayName("Should map customer and page to CustomerOrdersResponse")
    void shouldMapCustomerAndPageToCustomerOrdersResponse() {
        // Given
        Page<Order> orderPage = new PageImpl<>(List.of(order1, order2), pageable, 2);

        when(orderMapper.toOrderSummary(order1)).thenReturn(orderSummary1);
        when(orderMapper.toOrderSummary(order2)).thenReturn(orderSummary2);

        // When
        CustomerOrdersResponse result = customerMapper.toCustomerOrdersResponse(customer, orderPage);

        // Then
        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId()); // Should use customer ID from parameter
        assertEquals(2, result.getOrders().size());
        assertEquals(orderSummary1.getOrderId(), result.getOrders().get(0).getOrderId());
        assertEquals(orderSummary2.getOrderId(), result.getOrders().get(1).getOrderId());
        assertEquals(2L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getCurrentPage());
        assertEquals(10, result.getPageSize());

        verify(orderMapper, times(1)).toOrderSummary(order1);
        verify(orderMapper, times(1)).toOrderSummary(order2);
    }

    @Test
    @DisplayName("Should override customer ID when using customer parameter")
    void shouldOverrideCustomerIdWhenUsingCustomerParameter() {
        // Given
        Order orderWithDifferentCustomerId = new Order();
        orderWithDifferentCustomerId.setOrderId(1003L);
        orderWithDifferentCustomerId.setCustomerId(999L); // Different customer ID
        orderWithDifferentCustomerId.setTotalAmount(new BigDecimal("50.00"));

        Page<Order> orderPage = new PageImpl<>(List.of(orderWithDifferentCustomerId), pageable, 1);

        OrderSummary orderSummary = new OrderSummary();
        orderSummary.setOrderId(1003L);
        orderSummary.setTotalAmount(new BigDecimal("50.00"));

        when(orderMapper.toOrderSummary(orderWithDifferentCustomerId)).thenReturn(orderSummary);

        // When
        CustomerOrdersResponse result = customerMapper.toCustomerOrdersResponse(customer, orderPage);

        // Then
        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId()); // Should use customer parameter ID, not order's customer ID
        assertNotEquals(999L, result.getCustomerId());

        verify(orderMapper, times(1)).toOrderSummary(orderWithDifferentCustomerId);
    }

    @Test
    @DisplayName("Should handle null customer in customer and page mapping")
    void shouldHandleNullCustomerInCustomerAndPageMapping() {
        // Given
        Customer nullCustomer = null;
        Page<Order> orderPage = new PageImpl<>(List.of(order1), pageable, 1);

        when(orderMapper.toOrderSummary(order1)).thenReturn(orderSummary1);

        // When
        CustomerOrdersResponse result = customerMapper.toCustomerOrdersResponse(nullCustomer, orderPage);

        // Then
        assertNotNull(result);
        assertNull(result.getCustomerId()); // Should be null when customer parameter is null
        assertEquals(1, result.getOrders().size());

        verify(orderMapper, times(1)).toOrderSummary(order1);
    }

    @Test
    @DisplayName("Should handle null customer ID in customer parameter")
    void shouldHandleNullCustomerIdInCustomerParameter() {
        // Given
        Customer customerWithNullId = new Customer();
        customerWithNullId.setCustomerId(null);

        Page<Order> orderPage = new PageImpl<>(List.of(order1), pageable, 1);

        when(orderMapper.toOrderSummary(order1)).thenReturn(orderSummary1);

        // When
        CustomerOrdersResponse result = customerMapper.toCustomerOrdersResponse(customerWithNullId, orderPage);

        // Then
        assertNotNull(result);
        assertNull(result.getCustomerId());
        assertEquals(1, result.getOrders().size());

        verify(orderMapper, times(1)).toOrderSummary(order1);
    }

    @Test
    @DisplayName("Should propagate exception from OrderMapper")
    void shouldPropagateExceptionFromOrderMapper() {
        // Given
        Page<Order> orderPage = new PageImpl<>(List.of(order1), pageable, 1);
        RuntimeException expectedException = new RuntimeException("OrderMapper error");

        when(orderMapper.toOrderSummary(order1)).thenThrow(expectedException);

        // When & Then
        RuntimeException thrownException = assertThrows(
                RuntimeException.class,
                () -> customerMapper.toCustomerOrdersResponse(orderPage)
        );

        assertEquals("OrderMapper error", thrownException.getMessage());

        verify(orderMapper, times(1)).toOrderSummary(order1);
    }

    @Test
    @DisplayName("Should handle orders with null values gracefully")
    void shouldHandleOrdersWithNullValuesGracefully() {
        // Given
        Order orderWithNulls = new Order();
        orderWithNulls.setOrderId(null);
        orderWithNulls.setCustomerId(customerId);
        orderWithNulls.setTotalAmount(null);

        Page<Order> orderPage = new PageImpl<>(List.of(orderWithNulls), pageable, 1);

        OrderSummary summaryWithNulls = new OrderSummary();
        summaryWithNulls.setOrderId(null);
        summaryWithNulls.setTotalAmount(null);

        when(orderMapper.toOrderSummary(orderWithNulls)).thenReturn(summaryWithNulls);

        // When
        CustomerOrdersResponse result = customerMapper.toCustomerOrdersResponse(orderPage);

        // Then
        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertEquals(1, result.getOrders().size());
        assertNull(result.getOrders().getFirst().getOrderId());
        assertNull(result.getOrders().getFirst().getTotalAmount());

        verify(orderMapper, times(1)).toOrderSummary(orderWithNulls);
    }

    @Test
    @DisplayName("Should handle very large page numbers")
    void shouldHandleVeryLargePageNumbers() {
        // Given
        Pageable largePageNumber = PageRequest.of(1000, 10);
        Page<Order> emptyPage = new PageImpl<>(List.of(), largePageNumber, 5);

        // When
        CustomerOrdersResponse result = customerMapper.toCustomerOrdersResponse(emptyPage);

        // Then
        assertNotNull(result);
        assertNull(result.getCustomerId());
        assertTrue(result.getOrders().isEmpty());
        assertEquals(5L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(1000, result.getCurrentPage());
        assertEquals(10, result.getPageSize());
    }

    @Test
    @DisplayName("Should handle concurrent modification scenarios")
    void shouldHandleConcurrentModificationScenarios() {
        // Given
        Page<Order> orderPage = new PageImpl<>(List.of(order1), pageable, 1);

        when(orderMapper.toOrderSummary(order1)).thenReturn(orderSummary1);

        // When - simulate concurrent access
        CustomerOrdersResponse result1 = customerMapper.toCustomerOrdersResponse(orderPage);
        CustomerOrdersResponse result2 = customerMapper.toCustomerOrdersResponse(orderPage);

        // Then - both should work independently
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.getCustomerId(), result2.getCustomerId());
        assertEquals(result1.getOrders().size(), result2.getOrders().size());

        verify(orderMapper, times(2)).toOrderSummary(order1);
    }

    @Test
    @DisplayName("Should handle orders with mixed customer IDs gracefully")
    void shouldHandleOrdersWithMixedCustomerIdsGracefully() {
        // Given
        Order order1Different = new Order();
        order1Different.setOrderId(2001L);
        order1Different.setCustomerId(100L);

        Order order2Different = new Order();
        order2Different.setOrderId(2002L);
        order2Different.setCustomerId(200L); // Different customer ID

        Page<Order> mixedOrderPage = new PageImpl<>(List.of(order1Different, order2Different), pageable, 2);

        when(orderMapper.toOrderSummary(order1Different)).thenReturn(orderSummary1);
        when(orderMapper.toOrderSummary(order2Different)).thenReturn(orderSummary2);

        // When
        CustomerOrdersResponse result = customerMapper.toCustomerOrdersResponse(mixedOrderPage);

        // Then - should use first order's customer ID
        assertNotNull(result);
        assertEquals(100L, result.getCustomerId());
        assertEquals(2, result.getOrders().size());

        verify(orderMapper, times(1)).toOrderSummary(order1Different);
        verify(orderMapper, times(1)).toOrderSummary(order2Different);
    }

    @Test
    @DisplayName("Should handle page with empty orders list")
    void shouldHandlePageWithEmptyOrdersList() {
        // Given
        Page<Order> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> {
            CustomerOrdersResponse result = customerMapper.toCustomerOrdersResponse(emptyPage);
            assertNotNull(result);
            assertEquals(0L, result.getTotalElements());
            assertNull(result.getCustomerId());
            assertTrue(result.getOrders() == null || result.getOrders().isEmpty());
        });
    }

    @Test
    @DisplayName("Should maintain immutability of input parameters")
    void shouldMaintainImmutabilityOfInputParameters() {
        // Given
        Customer originalCustomer = new Customer();
        originalCustomer.setCustomerId(500L);
        Long originalCustomerId = originalCustomer.getCustomerId();

        Page<Order> orderPage = new PageImpl<>(List.of(order1), pageable, 1);
        when(orderMapper.toOrderSummary(order1)).thenReturn(orderSummary1);

        // When
        customerMapper.toCustomerOrdersResponse(originalCustomer, orderPage);

        // Then - original customer should not be modified
        assertEquals(originalCustomerId, originalCustomer.getCustomerId());

        verify(orderMapper, times(1)).toOrderSummary(order1);
    }
}