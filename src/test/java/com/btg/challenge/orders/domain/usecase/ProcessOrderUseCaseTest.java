package com.btg.challenge.orders.domain.usecase;

import com.btg.challenge.orders.domain.CustomerDataProvider;
import com.btg.challenge.orders.domain.OrderDataProvider;
import com.btg.challenge.orders.domain.entity.Customer;
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
@DisplayName("ProcessOrderUseCase Unit Tests")
class ProcessOrderUseCaseTest {

    @Mock
    private OrderDataProvider orderDataProvider;

    @Mock
    private CustomerDataProvider customerDataProvider;

    @InjectMocks
    private ProcessOrderUseCase processOrderUseCase;

    private Order validOrder;
    private Customer existingCustomer;

    @BeforeEach
    void setUp() {
        OrderItem item = new OrderItem("Product", 2, new BigDecimal("50.00"));
        validOrder = new Order(1L, 100L, List.of(item));
        existingCustomer = new Customer(100L);
    }

    @Test
    @DisplayName("Should process valid order with existing customer successfully")
    void shouldProcessValidOrderWithExistingCustomerSuccessfully() {
        // Given
        when(orderDataProvider.existsById(validOrder.getOrderId())).thenReturn(false);
        when(customerDataProvider.findById(validOrder.getCustomerId())).thenReturn(Optional.of(existingCustomer));
        when(orderDataProvider.save(validOrder, existingCustomer)).thenReturn(validOrder);

        // When
        assertDoesNotThrow(() -> processOrderUseCase.execute(validOrder));

        // Then
        verify(orderDataProvider).existsById(validOrder.getOrderId());
        verify(customerDataProvider).findById(validOrder.getCustomerId());
        verify(orderDataProvider).save(validOrder, existingCustomer);
        verify(customerDataProvider, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should process valid order and create new customer when customer not exists")
    void shouldProcessValidOrderAndCreateNewCustomerWhenCustomerNotExists() {
        // Given
        Customer newCustomer = new Customer(validOrder.getCustomerId());
        when(orderDataProvider.existsById(validOrder.getOrderId())).thenReturn(false);
        when(customerDataProvider.findById(validOrder.getCustomerId())).thenReturn(Optional.empty());
        when(customerDataProvider.save(any(Customer.class))).thenReturn(newCustomer);
        when(orderDataProvider.save(validOrder, newCustomer)).thenReturn(validOrder);

        // When
        assertDoesNotThrow(() -> processOrderUseCase.execute(validOrder));

        // Then
        verify(orderDataProvider).existsById(validOrder.getOrderId());
        verify(customerDataProvider).findById(validOrder.getCustomerId());
        verify(customerDataProvider).save(any(Customer.class));
        verify(orderDataProvider).save(validOrder, newCustomer);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when order is invalid")
    void shouldThrowIllegalArgumentExceptionWhenOrderIsInvalid() {
        // Given
        Order invalidOrder = new Order(); // Invalid order (no data)

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> processOrderUseCase.execute(invalidOrder));

        assertEquals("Invalid order data", exception.getMessage());
        verify(orderDataProvider, never()).existsById(any());
        verify(customerDataProvider, never()).findById(any());
        verify(orderDataProvider, never()).save(any(), any());
    }

    @Test
    @DisplayName("Should return early when order already exists")
    void shouldReturnEarlyWhenOrderAlreadyExists() {
        // Given
        when(orderDataProvider.existsById(validOrder.getOrderId())).thenReturn(true);

        // When
        assertDoesNotThrow(() -> processOrderUseCase.execute(validOrder));

        // Then
        verify(orderDataProvider).existsById(validOrder.getOrderId());
        verify(customerDataProvider, never()).findById(any());
        verify(orderDataProvider, never()).save(any(), any());
    }

    @Test
    @DisplayName("Should handle order with null orderId gracefully")
    void shouldHandleOrderWithNullOrderIdGracefully() {
        // Given
        Order orderWithNullId = new Order(null, 100L, List.of(new OrderItem("Product", 1, BigDecimal.TEN)));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> processOrderUseCase.execute(orderWithNullId));

        assertEquals("Invalid order data", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle order with null customerId gracefully")
    void shouldHandleOrderWithNullCustomerIdGracefully() {
        // Given
        Order orderWithNullCustomerId = new Order(1L, null, List.of(new OrderItem("Product", 1, BigDecimal.TEN)));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> processOrderUseCase.execute(orderWithNullCustomerId));

        assertEquals("Invalid order data", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle order with empty items list")
    void shouldHandleOrderWithEmptyItemsList() {
        // Given
        Order orderWithEmptyItems = new Order(1L, 100L, List.of());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> processOrderUseCase.execute(orderWithEmptyItems));

        assertEquals("Invalid order data", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle data provider exceptions gracefully")
    void shouldHandleDataProviderExceptionsGracefully() {
        // Given
        when(orderDataProvider.existsById(validOrder.getOrderId())).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> processOrderUseCase.execute(validOrder));

        assertEquals("Database error", exception.getMessage());
    }

    @Test
    @DisplayName("Should verify customer creation with correct customerId")
    void shouldVerifyCustomerCreationWithCorrectCustomerId() {
        // Given
        Long customerId = 999L;
        OrderItem item = new OrderItem("Product", 1, new BigDecimal("25.00"));
        Order order = new Order(2L, customerId, List.of(item));
        Customer newCustomer = new Customer(customerId);

        when(orderDataProvider.existsById(order.getOrderId())).thenReturn(false);
        when(customerDataProvider.findById(customerId)).thenReturn(Optional.empty());
        when(customerDataProvider.save(any(Customer.class))).thenReturn(newCustomer);
        when(orderDataProvider.save(order, newCustomer)).thenReturn(order);

        // When
        processOrderUseCase.execute(order);

        // Then
        verify(customerDataProvider).save(argThat(customer ->
            customer.getCustomerId().equals(customerId)));
    }

    @Test
    @DisplayName("Should maintain order integrity throughout process")
    void shouldMaintainOrderIntegrityThroughoutProcess() {
        // Given
        OrderItem item1 = new OrderItem("Product1", 2, new BigDecimal("30.00"));
        OrderItem item2 = new OrderItem("Product2", 1, new BigDecimal("40.00"));
        Order complexOrder = new Order(3L, 200L, List.of(item1, item2));
        Customer customer = new Customer(200L);

        when(orderDataProvider.existsById(complexOrder.getOrderId())).thenReturn(false);
        when(customerDataProvider.findById(complexOrder.getCustomerId())).thenReturn(Optional.of(customer));
        when(orderDataProvider.save(complexOrder, customer)).thenReturn(complexOrder);

        // When
        processOrderUseCase.execute(complexOrder);

        // Then
        verify(orderDataProvider).save(argThat(order ->
            order.getOrderId().equals(3L) &&
            order.getCustomerId().equals(200L) &&
            order.getItems().size() == 2 &&
            order.getTotalAmount().equals(new BigDecimal("100.00"))
        ), eq(customer));
    }
}
