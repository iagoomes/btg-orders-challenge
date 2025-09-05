package com.btg.challenge.orders.app.service.impl;

import com.btg.challenge.orders.app.mapper.OrderMapper;
import com.btg.challenge.orders.domain.entity.Order;
import com.btg.challenge.orders.domain.usecase.GetOrderTotalUseCase;
import com.btg.challenge.orders.infra.exception.OrderNotFoundException;
import com.btg.challenge.orders.model.OrderTotalResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderServiceImpl Unit Tests")
class OrderServiceImplTest {

    @Mock
    private GetOrderTotalUseCase getOrderTotalUseCase;

    @Mock
    private OrderMapper orderResponseMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Long orderId;
    private Order order;
    private OrderTotalResponse expectedResponse;

    @BeforeEach
    void setUp() {
        orderId = 1001L;
        order = new Order();
        order.setOrderId(orderId);
        order.setCustomerId(1L);
        order.setTotalAmount(new BigDecimal("121.00"));

        expectedResponse = new OrderTotalResponse();
        expectedResponse.setOrderId(orderId);
        expectedResponse.setTotal(121.00);
        expectedResponse.setCurrency("BRL");
    }

    @Test
    @DisplayName("Should return order total when order exists")
    void shouldReturnOrderTotalWhenOrderExists() {
        // Given
        when(getOrderTotalUseCase.execute(orderId)).thenReturn(Optional.of(order));
        when(orderResponseMapper.toOrderTotalResponse(order)).thenReturn(expectedResponse);

        // When
        OrderTotalResponse result = orderService.getOrderTotal(orderId);

        // Then
        assertNotNull(result);
        assertEquals(expectedResponse.getOrderId(), result.getOrderId());
        assertEquals(expectedResponse.getTotal(), result.getTotal());
        assertEquals(expectedResponse.getCurrency(), result.getCurrency());

        verify(getOrderTotalUseCase, times(1)).execute(orderId);
        verify(orderResponseMapper, times(1)).toOrderTotalResponse(order);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when order does not exist")
    void shouldThrowOrderNotFoundExceptionWhenOrderDoesNotExist() {
        // Given
        when(getOrderTotalUseCase.execute(orderId)).thenReturn(Optional.empty());

        // When & Then
        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> orderService.getOrderTotal(orderId)
        );

        assertNotNull(exception);
        assertTrue(exception.getMessage().contains(orderId.toString()));

        verify(getOrderTotalUseCase, times(1)).execute(orderId);
        verify(orderResponseMapper, never()).toOrderTotalResponse(any());
    }

    @Test
    @DisplayName("Should handle null orderId gracefully")
    void shouldHandleNullOrderIdGracefully() {
        // Given
        Long nullOrderId = null;
        when(getOrderTotalUseCase.execute(nullOrderId)).thenReturn(Optional.empty());

        // When & Then
        OrderNotFoundException exception = assertThrows(
                OrderNotFoundException.class,
                () -> orderService.getOrderTotal(nullOrderId)
        );

        assertNotNull(exception);

        verify(getOrderTotalUseCase, times(1)).execute(nullOrderId);
        verify(orderResponseMapper, never()).toOrderTotalResponse(any());
    }

    @Test
    @DisplayName("Should propagate RuntimeException from use case")
    void shouldPropagateRuntimeExceptionFromUseCase() {
        // Given
        RuntimeException expectedException = new RuntimeException("Database connection error");
        when(getOrderTotalUseCase.execute(orderId)).thenThrow(expectedException);

        // When & Then
        RuntimeException thrownException = assertThrows(
                RuntimeException.class,
                () -> orderService.getOrderTotal(orderId)
        );

        assertEquals("Database connection error", thrownException.getMessage());

        verify(getOrderTotalUseCase, times(1)).execute(orderId);
        verify(orderResponseMapper, never()).toOrderTotalResponse(any());
    }

    @Test
    @DisplayName("Should propagate RuntimeException from mapper")
    void shouldPropagateRuntimeExceptionFromMapper() {
        // Given
        when(getOrderTotalUseCase.execute(orderId)).thenReturn(Optional.of(order));
        RuntimeException expectedException = new RuntimeException("Mapping error");
        when(orderResponseMapper.toOrderTotalResponse(order)).thenThrow(expectedException);

        // When & Then
        RuntimeException thrownException = assertThrows(
                RuntimeException.class,
                () -> orderService.getOrderTotal(orderId)
        );

        assertEquals("Mapping error", thrownException.getMessage());

        verify(getOrderTotalUseCase, times(1)).execute(orderId);
        verify(orderResponseMapper, times(1)).toOrderTotalResponse(order);
    }
}