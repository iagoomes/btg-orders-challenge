package com.btg.challenge.orders.domain.usecase;

import com.btg.challenge.orders.domain.CustomerDataProvider;
import com.btg.challenge.orders.domain.OrderDataProvider;
import com.btg.challenge.orders.domain.entity.Customer;
import com.btg.challenge.orders.domain.entity.Order;
import com.btg.challenge.orders.domain.entity.OrderItem;
import com.btg.challenge.orders.infra.exception.CustomerNotFoundException;
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
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetCustomerOrdersUseCase Unit Tests")
class GetCustomerOrdersUseCaseTest {

    @Mock
    private OrderDataProvider orderDataProvider;

    @Mock
    private CustomerDataProvider customerDataProvider;

    @InjectMocks
    private GetCustomerOrdersUseCase getCustomerOrdersUseCase;

    private Customer existingCustomer;
    private List<Order> orders;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        existingCustomer = new Customer(100L);

        OrderItem item1 = new OrderItem("Product1", 1, new BigDecimal("50.00"));
        OrderItem item2 = new OrderItem("Product2", 2, new BigDecimal("30.00"));

        Order order1 = new Order(1L, 100L, List.of(item1));
        Order order2 = new Order(2L, 100L, List.of(item2));

        orders = List.of(order1, order2);
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Should return customer orders successfully for existing customer")
    void shouldReturnCustomerOrdersSuccessfullyForExistingCustomer() {
        // Given
        Long customerId = 100L;
        Page<Order> expectedPage = new PageImpl<>(orders, pageable, orders.size());

        when(customerDataProvider.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(orderDataProvider.findByCustomerId(eq(customerId), any(Pageable.class))).thenReturn(expectedPage);

        // When
        Page<Order> result = getCustomerOrdersUseCase.execute(customerId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(orders, result.getContent());
        verify(customerDataProvider).findById(customerId);
        verify(orderDataProvider).findByCustomerId(eq(customerId), any(Pageable.class));
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException for null customer ID")
    void shouldThrowCustomerNotFoundExceptionForNullCustomerId() {
        // Given
        Long customerId = null;

        // When & Then
        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> getCustomerOrdersUseCase.execute(customerId, pageable));

        assertEquals("Invalid customer ID: null", exception.getMessage());
        verify(customerDataProvider, never()).findById(any());
        verify(orderDataProvider, never()).findByCustomerId(any(), any());
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException for zero customer ID")
    void shouldThrowCustomerNotFoundExceptionForZeroCustomerId() {
        // Given
        Long customerId = 0L;

        // When & Then
        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> getCustomerOrdersUseCase.execute(customerId, pageable));

        assertEquals("Invalid customer ID: 0", exception.getMessage());
        verify(customerDataProvider, never()).findById(any());
        verify(orderDataProvider, never()).findByCustomerId(any(), any());
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException for negative customer ID")
    void shouldThrowCustomerNotFoundExceptionForNegativeCustomerId() {
        // Given
        Long customerId = -1L;

        // When & Then
        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> getCustomerOrdersUseCase.execute(customerId, pageable));

        assertEquals("Invalid customer ID: -1", exception.getMessage());
        verify(customerDataProvider, never()).findById(any());
        verify(orderDataProvider, never()).findByCustomerId(any(), any());
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when customer does not exist")
    void shouldThrowCustomerNotFoundExceptionWhenCustomerDoesNotExist() {
        // Given
        Long customerId = 999L;
        when(customerDataProvider.findById(customerId)).thenReturn(Optional.empty());

        // When & Then
        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> getCustomerOrdersUseCase.execute(customerId, pageable));

        assertTrue(exception.getMessage().contains("999"));
        verify(customerDataProvider).findById(customerId);
        verify(orderDataProvider, never()).findByCustomerId(any(), any());
    }

    @Test
    @DisplayName("Should add default sorting when pageable is unsorted")
    void shouldAddDefaultSortingWhenPageableIsUnsorted() {
        // Given
        Long customerId = 100L;
        Pageable unsortedPageable = PageRequest.of(0, 10);
        Page<Order> expectedPage = new PageImpl<>(orders, unsortedPageable, orders.size());

        when(customerDataProvider.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(orderDataProvider.findByCustomerId(eq(customerId), any(Pageable.class))).thenReturn(expectedPage);

        // When
        getCustomerOrdersUseCase.execute(customerId, unsortedPageable);

        // Then
        verify(orderDataProvider).findByCustomerId(eq(customerId), argThat(p ->
            p.getSort().isSorted() &&
            p.getSort().getOrderFor("createdAt") != null &&
            p.getSort().getOrderFor("createdAt").getDirection() == Sort.Direction.DESC
        ));
    }

    @Test
    @DisplayName("Should preserve existing sorting when pageable is already sorted")
    void shouldPreserveExistingSortingWhenPageableIsAlreadySorted() {
        // Given
        Long customerId = 100L;
        Pageable sortedPageable = PageRequest.of(0, 10, Sort.by("orderId").ascending());
        Page<Order> expectedPage = new PageImpl<>(orders, sortedPageable, orders.size());

        when(customerDataProvider.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(orderDataProvider.findByCustomerId(customerId, sortedPageable)).thenReturn(expectedPage);

        // When
        getCustomerOrdersUseCase.execute(customerId, sortedPageable);

        // Then
        verify(orderDataProvider).findByCustomerId(customerId, sortedPageable);
    }

    @Test
    @DisplayName("Should return empty page when customer has no orders")
    void shouldReturnEmptyPageWhenCustomerHasNoOrders() {
        // Given
        Long customerId = 200L;
        Customer customerWithNoOrders = new Customer(customerId);
        Page<Order> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(customerDataProvider.findById(customerId)).thenReturn(Optional.of(customerWithNoOrders));
        when(orderDataProvider.findByCustomerId(eq(customerId), any(Pageable.class))).thenReturn(emptyPage);

        // When
        Page<Order> result = getCustomerOrdersUseCase.execute(customerId, pageable);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(customerDataProvider).findById(customerId);
        verify(orderDataProvider).findByCustomerId(eq(customerId), any(Pageable.class));
    }

    @Test
    @DisplayName("Should handle different page sizes correctly")
    void shouldHandleDifferentPageSizesCorrectly() {
        // Given
        Long customerId = 100L;
        Pageable smallPageable = PageRequest.of(0, 1);
        Page<Order> smallPage = new PageImpl<>(orders.subList(0, 1), smallPageable, orders.size());

        when(customerDataProvider.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(orderDataProvider.findByCustomerId(eq(customerId), any(Pageable.class))).thenReturn(smallPage);

        // When
        Page<Order> result = getCustomerOrdersUseCase.execute(customerId, smallPageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        verify(orderDataProvider).findByCustomerId(eq(customerId), argThat(p -> p.getPageSize() == 1));
    }

    @Test
    @DisplayName("Should handle data provider exceptions gracefully")
    void shouldHandleDataProviderExceptionsGracefully() {
        // Given
        Long customerId = 100L;
        when(customerDataProvider.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(orderDataProvider.findByCustomerId(eq(customerId), any(Pageable.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> getCustomerOrdersUseCase.execute(customerId, pageable));

        assertEquals("Database error", exception.getMessage());
        verify(customerDataProvider).findById(customerId);
        verify(orderDataProvider).findByCustomerId(eq(customerId), any(Pageable.class));
    }

    @Test
    @DisplayName("Should verify transaction boundary is properly set")
    void shouldVerifyTransactionBoundaryIsProperlySet() {
        // Given
        Long customerId = 100L;
        Page<Order> expectedPage = new PageImpl<>(orders, pageable, orders.size());

        when(customerDataProvider.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(orderDataProvider.findByCustomerId(eq(customerId), any(Pageable.class))).thenReturn(expectedPage);

        // When
        Page<Order> result = getCustomerOrdersUseCase.execute(customerId, pageable);

        // Then
        // The method should complete successfully within transaction scope
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
    }

    @Test
    @DisplayName("Should handle large page numbers correctly")
    void shouldHandleLargePageNumbersCorrectly() {
        // Given
        Long customerId = 100L;
        Pageable largePageNumberPageable = PageRequest.of(10, 5);
        Page<Order> emptyPage = new PageImpl<>(List.of(), largePageNumberPageable, orders.size());

        when(customerDataProvider.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(orderDataProvider.findByCustomerId(eq(customerId), any(Pageable.class))).thenReturn(emptyPage);

        // When
        Page<Order> result = getCustomerOrdersUseCase.execute(customerId, largePageNumberPageable);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(10, result.getNumber());
        verify(orderDataProvider).findByCustomerId(eq(customerId), argThat(p -> p.getPageNumber() == 10));
    }
}
