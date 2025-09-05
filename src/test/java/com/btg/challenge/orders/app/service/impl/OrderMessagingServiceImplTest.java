package com.btg.challenge.orders.app.service.impl;

import com.btg.challenge.orders.app.mapper.OrderMessageMapper;
import com.btg.challenge.orders.domain.entity.Order;
import com.btg.challenge.orders.domain.entity.OrderItem;
import com.btg.challenge.orders.domain.usecase.ProcessOrderUseCase;
import com.btg.challenge.orders.infra.mqprovider.OrderMessage;
import com.btg.challenge.orders.infra.mqprovider.OrderItemMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderMessagingServiceImpl Unit Tests")
class OrderMessagingServiceImplTest {

    @Mock
    private ProcessOrderUseCase processOrderUseCase;

    @Mock
    private OrderMessageMapper orderMessageMapper;

    @InjectMocks
    private OrderMessagingServiceImpl orderMessagingService;

    private OrderMessage orderMessage;
    private Order order;

    @BeforeEach
    void setUp() {
        // Setup OrderMessage
        OrderItemMessage item1 = new OrderItemMessage();
        item1.setProduto("lápis");
        item1.setQuantidade(100);
        item1.setPreco(new BigDecimal("1.10"));

        OrderItemMessage item2 = new OrderItemMessage();
        item2.setProduto("caderno");
        item2.setQuantidade(10);
        item2.setPreco(new BigDecimal("1.00"));

        orderMessage = new OrderMessage();
        orderMessage.setCodigoPedido(1001L);
        orderMessage.setCodigoCliente(1L);
        orderMessage.setItens(List.of(item1, item2));

        // Setup Order domain entity
        OrderItem orderItem1 = new OrderItem("lápis", 100, new BigDecimal("1.10"));
        OrderItem orderItem2 = new OrderItem("caderno", 10, new BigDecimal("1.00"));
        order = new Order(1001L, 1L, List.of(orderItem1, orderItem2));
    }

    @Test
    @DisplayName("Should process order message successfully")
    void shouldProcessOrderMessageSuccessfully() {
        // Given
        when(orderMessageMapper.toDomain(orderMessage)).thenReturn(order);
        doNothing().when(processOrderUseCase).execute(order);

        // When
        assertDoesNotThrow(() -> orderMessagingService.processOrderMessage(orderMessage));

        // Then
        verify(orderMessageMapper, times(1)).toDomain(orderMessage);
        verify(processOrderUseCase, times(1)).execute(order);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when order message is invalid")
    void shouldThrowIllegalArgumentExceptionWhenOrderMessageIsInvalid() {
        // Given
        IllegalArgumentException expectedException = new IllegalArgumentException("Invalid order data");
        when(orderMessageMapper.toDomain(orderMessage)).thenThrow(expectedException);

        // When & Then
        IllegalArgumentException thrownException = assertThrows(
                IllegalArgumentException.class,
                () -> orderMessagingService.processOrderMessage(orderMessage)
        );

        assertEquals("Invalid order data", thrownException.getMessage());

        verify(orderMessageMapper, times(1)).toDomain(orderMessage);
        verify(processOrderUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when use case validates and rejects order")
    void shouldThrowIllegalArgumentExceptionWhenUseCaseValidatesAndRejectsOrder() {
        // Given
        when(orderMessageMapper.toDomain(orderMessage)).thenReturn(order);
        IllegalArgumentException expectedException = new IllegalArgumentException("Order validation failed");
        doThrow(expectedException).when(processOrderUseCase).execute(order);

        // When & Then
        IllegalArgumentException thrownException = assertThrows(
                IllegalArgumentException.class,
                () -> orderMessagingService.processOrderMessage(orderMessage)
        );

        assertEquals("Order validation failed", thrownException.getMessage());

        verify(orderMessageMapper, times(1)).toDomain(orderMessage);
        verify(processOrderUseCase, times(1)).execute(order);
    }

    @Test
    @DisplayName("Should throw RuntimeException when unexpected error occurs")
    void shouldThrowRuntimeExceptionWhenUnexpectedErrorOccurs() {
        // Given
        when(orderMessageMapper.toDomain(orderMessage)).thenReturn(order);
        RuntimeException expectedException = new RuntimeException("Database connection error");
        doThrow(expectedException).when(processOrderUseCase).execute(order);

        // When & Then
        RuntimeException thrownException = assertThrows(
                RuntimeException.class,
                () -> orderMessagingService.processOrderMessage(orderMessage)
        );

        assertEquals("Database connection error", thrownException.getMessage());

        verify(orderMessageMapper, times(1)).toDomain(orderMessage);
        verify(processOrderUseCase, times(1)).execute(order);
    }

    @Test
    @DisplayName("Should handle null order message gracefully")
    void shouldHandleNullOrderMessageGracefully() {
        // Given
        OrderMessage nullMessage = null;

        // When & Then
        com.btg.challenge.orders.infra.exception.OrderMessageNullException thrownException = assertThrows(
                com.btg.challenge.orders.infra.exception.OrderMessageNullException.class,
                () -> orderMessagingService.processOrderMessage(nullMessage)
        );

        assertEquals("Order message cannot be null", thrownException.getMessage());
    }

    @Test
    @DisplayName("Should handle mapper returning null order")
    void shouldHandleMapperReturningNullOrder() {
        // Given
        when(orderMessageMapper.toDomain(orderMessage)).thenReturn(null);
        doThrow(new IllegalArgumentException("Order cannot be null")).when(processOrderUseCase).execute(null);

        // When & Then
        IllegalArgumentException thrownException = assertThrows(
                IllegalArgumentException.class,
                () -> orderMessagingService.processOrderMessage(orderMessage)
        );

        assertEquals("Order cannot be null", thrownException.getMessage());

        verify(orderMessageMapper, times(1)).toDomain(orderMessage);
        verify(processOrderUseCase, times(1)).execute(null);
    }

    @Test
    @DisplayName("Should process order with empty items list")
    void shouldProcessOrderWithEmptyItemsList() {
        // Given
        OrderMessage emptyItemsMessage = new OrderMessage();
        emptyItemsMessage.setCodigoPedido(1001L);
        emptyItemsMessage.setCodigoCliente(1L);
        emptyItemsMessage.setItens(List.of());

        Order emptyItemsOrder = new Order(1001L, 1L, List.of());

        when(orderMessageMapper.toDomain(emptyItemsMessage)).thenReturn(emptyItemsOrder);
        doNothing().when(processOrderUseCase).execute(emptyItemsOrder);

        // When
        assertDoesNotThrow(() -> orderMessagingService.processOrderMessage(emptyItemsMessage));

        // Then
        verify(orderMessageMapper, times(1)).toDomain(emptyItemsMessage);
        verify(processOrderUseCase, times(1)).execute(emptyItemsOrder);
    }

    @Test
    @DisplayName("Should process order with single item")
    void shouldProcessOrderWithSingleItem() {
        // Given
        OrderItemMessage singleItem = new OrderItemMessage();
        singleItem.setProduto("notebook");
        singleItem.setQuantidade(1);
        singleItem.setPreco(new BigDecimal("2500.00"));

        OrderMessage singleItemMessage = new OrderMessage();
        singleItemMessage.setCodigoPedido(2001L);
        singleItemMessage.setCodigoCliente(2L);
        singleItemMessage.setItens(List.of(singleItem));

        OrderItem singleOrderItem = new OrderItem("notebook", 1, new BigDecimal("2500.00"));
        Order singleItemOrder = new Order(2001L, 2L, List.of(singleOrderItem));

        when(orderMessageMapper.toDomain(singleItemMessage)).thenReturn(singleItemOrder);
        doNothing().when(processOrderUseCase).execute(singleItemOrder);

        // When
        assertDoesNotThrow(() -> orderMessagingService.processOrderMessage(singleItemMessage));

        // Then
        verify(orderMessageMapper, times(1)).toDomain(singleItemMessage);
        verify(processOrderUseCase, times(1)).execute(singleItemOrder);
    }
}