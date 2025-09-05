package com.btg.challenge.orders.app.service.impl;

import com.btg.challenge.orders.app.mapper.CustomerMapper;
import com.btg.challenge.orders.domain.entity.Order;
import com.btg.challenge.orders.domain.entity.OrderItem;
import com.btg.challenge.orders.domain.usecase.GetCustomerOrderCountUseCase;
import com.btg.challenge.orders.domain.usecase.GetCustomerOrdersUseCase;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerServiceImpl Unit Tests")
class CustomerServiceImplTest {

    @Mock
    private GetCustomerOrderCountUseCase getOrderCountUseCase;

    @Mock
    private GetCustomerOrdersUseCase getCustomerOrdersUseCase;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Long customerId;
    private Pageable pageable;
    private Page<Order> customerOrdersPage;
    private CustomerOrderCountResponse expectedCountResponse;
    private CustomerOrdersResponse expectedOrdersResponse;

    @BeforeEach
    void setUp() {
        customerId = 1L;
        pageable = PageRequest.of(0, 10);

        // Setup Order entities
        OrderItem item1 = new OrderItem("lápis", 100, new BigDecimal("1.10"));
        OrderItem item2 = new OrderItem("caderno", 10, new BigDecimal("1.00"));
        Order order1 = new Order(1001L, customerId, List.of(item1));
        Order order2 = new Order(1002L, customerId, List.of(item2));
        customerOrdersPage = new PageImpl<>(List.of(order1, order2), pageable, 2L);

        // Setup expected responses
        expectedCountResponse = CustomerOrderCountResponse.builder()
                .customerId(customerId)
                .orderCount(2L)
                .build();

        OrderSummary orderSummary1 = new OrderSummary();
        orderSummary1.setOrderId(1001L);
        orderSummary1.setCustomerId(customerId);
        orderSummary1.setTotalAmount(new BigDecimal("110.00"));

        OrderSummary orderSummary2 = new OrderSummary();
        orderSummary2.setOrderId(1002L);
        orderSummary2.setCustomerId(customerId);
        orderSummary2.setTotalAmount(new BigDecimal("10.00"));

        expectedOrdersResponse = new CustomerOrdersResponse();
        expectedOrdersResponse.setCustomerId(customerId);
        expectedOrdersResponse.setOrders(List.of(orderSummary1, orderSummary2));
        expectedOrdersResponse.setTotalElements(2L);
        expectedOrdersResponse.setTotalPages(1);
        expectedOrdersResponse.setCurrentPage(0);
        expectedOrdersResponse.setPageSize(10);
    }

    @Test
    @DisplayName("Should return customer order count when customer has orders")
    void shouldReturnCustomerOrderCountWhenCustomerHasOrders() {
        // Given
        long orderCount = 2L;
        when(getOrderCountUseCase.execute(customerId)).thenReturn(orderCount);
        when(customerMapper.toOrderCountResponse(customerId, orderCount)).thenReturn(expectedCountResponse);

        // When
        CustomerOrderCountResponse result = customerService.getOrderCount(customerId);

        // Then
        assertNotNull(result);
        assertEquals(expectedCountResponse.getCustomerId(), result.getCustomerId());
        assertEquals(expectedCountResponse.getOrderCount(), result.getOrderCount());

        verify(getOrderCountUseCase, times(1)).execute(customerId);
        verify(customerMapper, times(1)).toOrderCountResponse(customerId, orderCount);
    }

    @Test
    @DisplayName("Should return zero order count when customer has no orders")
    void shouldReturnZeroOrderCountWhenCustomerHasNoOrders() {
        // Given
        long orderCount = 0L;
        CustomerOrderCountResponse zeroCountResponse = CustomerOrderCountResponse.builder()
                .customerId(customerId)
                .orderCount(0L)
                .build();

        when(getOrderCountUseCase.execute(customerId)).thenReturn(orderCount);
        when(customerMapper.toOrderCountResponse(customerId, orderCount)).thenReturn(zeroCountResponse);

        // When
        CustomerOrderCountResponse result = customerService.getOrderCount(customerId);

        // Then
        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertEquals(0L, result.getOrderCount());

        verify(getOrderCountUseCase, times(1)).execute(customerId);
        verify(customerMapper, times(1)).toOrderCountResponse(customerId, orderCount);
    }

    @Test
    @DisplayName("Should return customer orders when customer has orders")
    void shouldReturnCustomerOrdersWhenCustomerHasOrders() {
        // Given
        when(getCustomerOrdersUseCase.execute(customerId, pageable)).thenReturn(customerOrdersPage);
        when(customerMapper.toCustomerOrdersResponse(customerOrdersPage)).thenReturn(expectedOrdersResponse);

        // When
        CustomerOrdersResponse result = customerService.getCustomerOrders(customerId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(expectedOrdersResponse.getCustomerId(), result.getCustomerId());
        assertEquals(expectedOrdersResponse.getOrders().size(), result.getOrders().size());
        assertEquals(expectedOrdersResponse.getTotalElements(), result.getTotalElements());
        assertEquals(expectedOrdersResponse.getTotalPages(), result.getTotalPages());
        assertEquals(expectedOrdersResponse.getCurrentPage(), result.getCurrentPage());
        assertEquals(expectedOrdersResponse.getPageSize(), result.getPageSize());

        verify(getCustomerOrdersUseCase, times(1)).execute(customerId, pageable);
        verify(customerMapper, times(1)).toCustomerOrdersResponse(customerOrdersPage);
    }

    @Test
    @DisplayName("Should return empty customer orders when customer has no orders")
    void shouldReturnEmptyCustomerOrdersWhenCustomerHasNoOrders() {
        // Given
        Page<Order> emptyPage = new PageImpl<>(List.of(), pageable, 0L);
        CustomerOrdersResponse emptyResponse = new CustomerOrdersResponse();
        emptyResponse.setCustomerId(customerId);
        emptyResponse.setOrders(List.of());
        emptyResponse.setTotalElements(0L);
        emptyResponse.setTotalPages(0);
        emptyResponse.setCurrentPage(0);
        emptyResponse.setPageSize(10);

        when(getCustomerOrdersUseCase.execute(customerId, pageable)).thenReturn(emptyPage);
        when(customerMapper.toCustomerOrdersResponse(emptyPage)).thenReturn(emptyResponse);

        // When
        CustomerOrdersResponse result = customerService.getCustomerOrders(customerId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(customerId, result.getCustomerId());
        assertTrue(result.getOrders().isEmpty());
        assertEquals(0L, result.getTotalElements());
        assertEquals(0, result.getTotalPages());

        verify(getCustomerOrdersUseCase, times(1)).execute(customerId, pageable);
        verify(customerMapper, times(1)).toCustomerOrdersResponse(emptyPage);
    }

    @Test
    @DisplayName("Should handle null customerId gracefully in getOrderCount")
    void shouldHandleNullCustomerIdGracefullyInGetOrderCount() {
        // Given
        Long nullCustomerId = null;
        CustomerOrderCountResponse expectedResponse = CustomerOrderCountResponse.builder()
                .customerId(null)
                .orderCount(0L)
                .build();
        when(getOrderCountUseCase.execute(nullCustomerId)).thenReturn(0L);
        when(customerMapper.toOrderCountResponse(nullCustomerId, 0L)).thenReturn(expectedResponse);

        // When
        CustomerOrderCountResponse result = customerService.getOrderCount(nullCustomerId);

        // Then
        assertNotNull(result);
        assertNull(result.getCustomerId());
        assertEquals(0L, result.getOrderCount());
        verify(getOrderCountUseCase, times(1)).execute(nullCustomerId);
        verify(customerMapper, times(1)).toOrderCountResponse(nullCustomerId, 0L);
    }

    @Test
    @DisplayName("Should handle null customerId gracefully in getCustomerOrders")
    void shouldHandleNullCustomerIdGracefullyInGetCustomerOrders() {
        // Given
        Long nullCustomerId = null;
        when(getCustomerOrdersUseCase.execute(nullCustomerId, pageable))
                .thenThrow(new IllegalArgumentException("Customer ID cannot be null"));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> customerService.getCustomerOrders(nullCustomerId, pageable)
        );

        assertEquals("Customer ID cannot be null", exception.getMessage());

        verify(getCustomerOrdersUseCase, times(1)).execute(nullCustomerId, pageable);
        verify(customerMapper, never()).toCustomerOrdersResponse(any());
    }

    @Test
    @DisplayName("Should propagate RuntimeException from getOrderCount use case")
    void shouldPropagateRuntimeExceptionFromGetOrderCountUseCase() {
        // Given
        RuntimeException expectedException = new RuntimeException("Database connection error");
        when(getOrderCountUseCase.execute(customerId)).thenThrow(expectedException);

        // When & Then
        RuntimeException thrownException = assertThrows(
                RuntimeException.class,
                () -> customerService.getOrderCount(customerId)
        );

        assertEquals("Database connection error", thrownException.getMessage());

        verifyNoInteractions(customerMapper);
        verify(getOrderCountUseCase, times(1)).execute(customerId);
    }

    @Test
    @DisplayName("Should propagate RuntimeException from getCustomerOrders use case")
    void shouldPropagateRuntimeExceptionFromGetCustomerOrdersUseCase() {
        // Given
        RuntimeException expectedException = new RuntimeException("Database connection error");
        when(getCustomerOrdersUseCase.execute(customerId, pageable)).thenThrow(expectedException);

        // When & Then
        RuntimeException thrownException = assertThrows(
                RuntimeException.class,
                () -> customerService.getCustomerOrders(customerId, pageable)
        );

        assertEquals("Database connection error", thrownException.getMessage());

        verify(getCustomerOrdersUseCase, times(1)).execute(customerId, pageable);
        verify(customerMapper, never()).toCustomerOrdersResponse(any());
    }

    @Test
    @DisplayName("Should handle different page sizes correctly")
    void shouldHandleDifferentPageSizesCorrectly() {
        // Given
        Pageable customPageable = PageRequest.of(1, 5);
        Page<Order> customPage = new PageImpl<>(List.of(), customPageable, 12L);
        CustomerOrdersResponse customResponse = new CustomerOrdersResponse();
        customResponse.setCustomerId(customerId);
        customResponse.setOrders(List.of());
        customResponse.setTotalElements(12L);
        customResponse.setTotalPages(3);
        customResponse.setCurrentPage(1);
        customResponse.setPageSize(5);

        when(getCustomerOrdersUseCase.execute(customerId, customPageable)).thenReturn(customPage);
        when(customerMapper.toCustomerOrdersResponse(customPage)).thenReturn(customResponse);

        // When
        CustomerOrdersResponse result = customerService.getCustomerOrders(customerId, customPageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getCurrentPage());
        assertEquals(5, result.getPageSize());
        assertEquals(3, result.getTotalPages());
        assertEquals(12L, result.getTotalElements());

        verify(getCustomerOrdersUseCase, times(1)).execute(customerId, customPageable);
        verify(customerMapper, times(1)).toCustomerOrdersResponse(customPage);
    }

    @Test
    @DisplayName("Should propagate mapper exception from order count response")
    void shouldPropagateMapperExceptionFromOrderCountResponse() {
        // Given
        long orderCount = 5L;
        when(getOrderCountUseCase.execute(customerId)).thenReturn(orderCount);
        RuntimeException expectedException = new RuntimeException("Mapping error");
        when(customerMapper.toOrderCountResponse(customerId, orderCount)).thenThrow(expectedException);

        // When & Then
        RuntimeException thrownException = assertThrows(
                RuntimeException.class,
                () -> customerService.getOrderCount(customerId)
        );

        assertEquals("Mapping error", thrownException.getMessage());

        verify(getOrderCountUseCase, times(1)).execute(customerId);
        verify(customerMapper, times(1)).toOrderCountResponse(customerId, orderCount);
    }

    @Test
    @DisplayName("Should propagate mapper exception from customer orders response")
    void shouldPropagateMapperExceptionFromCustomerOrdersResponse() {
        // Given
        when(getCustomerOrdersUseCase.execute(customerId, pageable)).thenReturn(customerOrdersPage);
        RuntimeException expectedException = new RuntimeException("Mapping error");
        when(customerMapper.toCustomerOrdersResponse(customerOrdersPage)).thenThrow(expectedException);

        // When & Then
        RuntimeException thrownException = assertThrows(
                RuntimeException.class,
                () -> customerService.getCustomerOrders(customerId, pageable)
        );

        assertEquals("Mapping error", thrownException.getMessage());
        verify(getCustomerOrdersUseCase, times(1)).execute(customerId, pageable);
        verify(customerMapper, times(1)).toCustomerOrdersResponse(customerOrdersPage);
    }
}
