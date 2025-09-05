package com.btg.challenge.orders.domain.usecase;

import com.btg.challenge.orders.domain.OrderDataProvider;
import com.btg.challenge.orders.domain.entity.Order;
import com.btg.challenge.orders.domain.entity.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetOrderTotalUseCase Unit Tests")
class GetOrderTotalUseCaseTest {

    @Mock
    private OrderDataProvider orderDataProvider;

    @InjectMocks
    private GetOrderTotalUseCase getOrderTotalUseCase;

    private Order orderWithItems;

    @BeforeEach
    void setUp() {
        OrderItem item1 = new OrderItem("Product1", 2, new BigDecimal("25.00"));
        OrderItem item2 = new OrderItem("Product2", 1, new BigDecimal("50.00"));
        orderWithItems = new Order(1L, 100L, List.of(item1, item2));
    }

    @Test
    @DisplayName("Should return order with updated totals for valid order ID")
    void shouldReturnOrderWithUpdatedTotalsForValidOrderId() {
        // Given
        Long orderId = 1L;
        when(orderDataProvider.findByIdWithItems(orderId)).thenReturn(Optional.of(orderWithItems));

        // When
        Optional<Order> result = getOrderTotalUseCase.execute(orderId);

        // Then
        assertTrue(result.isPresent());
        Order order = result.get();
        assertEquals(orderId, order.getOrderId());
        assertEquals(new BigDecimal("100.00"), order.getTotalAmount());
        assertEquals(2, order.getItemsCount());
        verify(orderDataProvider).findByIdWithItems(orderId);
    }

    @Test
    @DisplayName("Should return empty Optional for null order ID")
    void shouldReturnEmptyOptionalForNullOrderId() {
        // Given
        Long orderId = null;

        // When
        Optional<Order> result = getOrderTotalUseCase.execute(orderId);

        // Then
        assertTrue(result.isEmpty());
        verify(orderDataProvider, never()).findByIdWithItems(any());
    }

    @Test
    @DisplayName("Should return empty Optional for zero order ID")
    void shouldReturnEmptyOptionalForZeroOrderId() {
        // Given
        Long orderId = 0L;

        // When
        Optional<Order> result = getOrderTotalUseCase.execute(orderId);

        // Then
        assertTrue(result.isEmpty());
        verify(orderDataProvider, never()).findByIdWithItems(any());
    }

    @Test
    @DisplayName("Should return empty Optional for negative order ID")
    void shouldReturnEmptyOptionalForNegativeOrderId() {
        // Given
        Long orderId = -1L;

        // When
        Optional<Order> result = getOrderTotalUseCase.execute(orderId);

        // Then
        assertTrue(result.isEmpty());
        verify(orderDataProvider, never()).findByIdWithItems(any());
    }

    @Test
    @DisplayName("Should return empty Optional when order is not found")
    void shouldReturnEmptyOptionalWhenOrderIsNotFound() {
        // Given
        Long orderId = 999L;
        when(orderDataProvider.findByIdWithItems(orderId)).thenReturn(Optional.empty());

        // When
        Optional<Order> result = getOrderTotalUseCase.execute(orderId);

        // Then
        assertTrue(result.isEmpty());
        verify(orderDataProvider).findByIdWithItems(orderId);
    }

    @Test
    @DisplayName("Should update totals even if order already has calculated values")
    void shouldUpdateTotalsEvenIfOrderAlreadyHasCalculatedValues() {
        // Given
        Long orderId = 2L;
        OrderItem item = new OrderItem("Product", 3, new BigDecimal("20.00"));
        Order orderWithOutdatedTotals = new Order(orderId, 200L, List.of(item));
        // Set outdated values
        orderWithOutdatedTotals.setTotalAmount(new BigDecimal("50.00")); // Wrong value
        orderWithOutdatedTotals.setItemsCount(1); // Wrong value

        when(orderDataProvider.findByIdWithItems(orderId)).thenReturn(Optional.of(orderWithOutdatedTotals));

        // When
        Optional<Order> result = getOrderTotalUseCase.execute(orderId);

        // Then
        assertTrue(result.isPresent());
        Order order = result.get();
        assertEquals(new BigDecimal("60.00"), order.getTotalAmount()); // Corrected value
        assertEquals(1, order.getItemsCount()); // This remains 1 (correct count)
        verify(orderDataProvider).findByIdWithItems(orderId);
    }

    @Test
    @DisplayName("Should handle order with single item correctly")
    void shouldHandleOrderWithSingleItemCorrectly() {
        // Given
        Long orderId = 3L;
        OrderItem singleItem = new OrderItem("Single Product", 5, new BigDecimal("15.75"));
        Order singleItemOrder = new Order(orderId, 300L, List.of(singleItem));

        when(orderDataProvider.findByIdWithItems(orderId)).thenReturn(Optional.of(singleItemOrder));

        // When
        Optional<Order> result = getOrderTotalUseCase.execute(orderId);

        // Then
        assertTrue(result.isPresent());
        Order order = result.get();
        assertEquals(new BigDecimal("78.75"), order.getTotalAmount());
        assertEquals(1, order.getItemsCount());
        verify(orderDataProvider).findByIdWithItems(orderId);
    }

    @Test
    @DisplayName("Should handle order with no items gracefully")
    void shouldHandleOrderWithNoItemsGracefully() {
        // Given
        Long orderId = 4L;
        Order emptyOrder = new Order(orderId, 400L, List.of());

        when(orderDataProvider.findByIdWithItems(orderId)).thenReturn(Optional.of(emptyOrder));

        // When
        Optional<Order> result = getOrderTotalUseCase.execute(orderId);

        // Then
        assertTrue(result.isPresent());
        Order order = result.get();
        assertEquals(BigDecimal.ZERO, order.getTotalAmount());
        assertEquals(0, order.getItemsCount());
        verify(orderDataProvider).findByIdWithItems(orderId);
    }

    @Test
    @DisplayName("Should handle order with high-value items correctly")
    void shouldHandleOrderWithHighValueItemsCorrectly() {
        // Given
        Long orderId = 5L;
        OrderItem expensiveItem1 = new OrderItem("Luxury Item 1", 1, new BigDecimal("999.99"));
        OrderItem expensiveItem2 = new OrderItem("Luxury Item 2", 2, new BigDecimal("1500.50"));
        Order expensiveOrder = new Order(orderId, 500L, List.of(expensiveItem1, expensiveItem2));

        when(orderDataProvider.findByIdWithItems(orderId)).thenReturn(Optional.of(expensiveOrder));

        // When
        Optional<Order> result = getOrderTotalUseCase.execute(orderId);

        // Then
        assertTrue(result.isPresent());
        Order order = result.get();
        assertEquals(new BigDecimal("4000.99"), order.getTotalAmount());
        assertEquals(2, order.getItemsCount());
        verify(orderDataProvider).findByIdWithItems(orderId);
    }

    @Test
    @DisplayName("Should handle data provider exceptions gracefully")
    void shouldHandleDataProviderExceptionsGracefully() {
        // Given
        Long orderId = 6L;
        when(orderDataProvider.findByIdWithItems(orderId))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> getOrderTotalUseCase.execute(orderId));

        assertEquals("Database connection failed", exception.getMessage());
        verify(orderDataProvider).findByIdWithItems(orderId);
    }

    @Test
    @DisplayName("Should call findByIdWithItems exactly once for valid order ID")
    void shouldCallFindByIdWithItemsExactlyOnceForValidOrderId() {
        // Given
        Long orderId = 7L;
        when(orderDataProvider.findByIdWithItems(orderId)).thenReturn(Optional.of(orderWithItems));

        // When
        getOrderTotalUseCase.execute(orderId);

        // Then
        verify(orderDataProvider, times(1)).findByIdWithItems(orderId);
        verifyNoMoreInteractions(orderDataProvider);
    }

    @Test
    @DisplayName("Should preserve order properties while updating totals")
    void shouldPreserveOrderPropertiesWhileUpdatingTotals() {
        // Given
        Long orderId = 8L;
        Long customerId = 800L;
        OrderItem item = new OrderItem("Test Product", 2, new BigDecimal("30.00"));
        Order originalOrder = new Order(orderId, customerId, List.of(item));

        when(orderDataProvider.findByIdWithItems(orderId)).thenReturn(Optional.of(originalOrder));

        // When
        Optional<Order> result = getOrderTotalUseCase.execute(orderId);

        // Then
        assertTrue(result.isPresent());
        Order order = result.get();
        assertEquals(orderId, order.getOrderId());
        assertEquals(customerId, order.getCustomerId());
        assertEquals(1, order.getItems().size());
        assertEquals("Test Product", order.getItems().getFirst().getProduct());
        assertEquals(new BigDecimal("60.00"), order.getTotalAmount());
        assertEquals(1, order.getItemsCount());
    }

    @Test
    @DisplayName("Should handle multiple consecutive calls correctly")
    void shouldHandleMultipleConsecutiveCallsCorrectly() {
        // Given
        Long orderId1 = 10L;
        Long orderId2 = 20L;

        OrderItem item1 = new OrderItem("Product1", 1, new BigDecimal("10.00"));
        OrderItem item2 = new OrderItem("Product2", 2, new BigDecimal("15.00"));

        Order order1 = new Order(orderId1, 100L, List.of(item1));
        Order order2 = new Order(orderId2, 200L, List.of(item2));

        when(orderDataProvider.findByIdWithItems(orderId1)).thenReturn(Optional.of(order1));
        when(orderDataProvider.findByIdWithItems(orderId2)).thenReturn(Optional.of(order2));

        // When
        Optional<Order> result1 = getOrderTotalUseCase.execute(orderId1);
        Optional<Order> result2 = getOrderTotalUseCase.execute(orderId2);

        // Then
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(new BigDecimal("10.00"), result1.get().getTotalAmount());
        assertEquals(new BigDecimal("30.00"), result2.get().getTotalAmount());
        verify(orderDataProvider).findByIdWithItems(orderId1);
        verify(orderDataProvider).findByIdWithItems(orderId2);
    }
}
