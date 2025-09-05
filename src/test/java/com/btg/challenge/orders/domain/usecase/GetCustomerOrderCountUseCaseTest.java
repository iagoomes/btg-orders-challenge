package com.btg.challenge.orders.domain.usecase;

import com.btg.challenge.orders.domain.CustomerDataProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetCustomerOrderCountUseCase Unit Tests")
class GetCustomerOrderCountUseCaseTest {

    @Mock
    private CustomerDataProvider customerDataProvider;

    @InjectMocks
    private GetCustomerOrderCountUseCase getCustomerOrderCountUseCase;

    @ParameterizedTest
    @MethodSource("validCustomerIdAndCountProvider")
    @DisplayName("Should return order count for valid customer IDs")
    void shouldReturnOrderCountForValidCustomerIds(Long customerId, long expectedCount) {
        // Given
        when(customerDataProvider.countOrdersByCustomerId(customerId)).thenReturn(expectedCount);

        // When
        long result = getCustomerOrderCountUseCase.execute(customerId);

        // Then
        assertEquals(expectedCount, result);
        verify(customerDataProvider).countOrdersByCustomerId(customerId);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, -1L})
    @DisplayName("Should return zero for invalid customer IDs")
    void shouldReturnZeroForInvalidCustomerIds(Long customerId) {
        // When
        long result = getCustomerOrderCountUseCase.execute(customerId);

        // Then
        assertEquals(0L, result);
        verify(customerDataProvider, never()).countOrdersByCustomerId(any());
    }

    @Test
    @DisplayName("Should handle data provider exceptions")
    void shouldHandleDataProviderExceptions() {
        // Given
        Long customerId = 400L;
        when(customerDataProvider.countOrdersByCustomerId(customerId))
                .thenThrow(new RuntimeException("Database connection error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> getCustomerOrderCountUseCase.execute(customerId));

        assertEquals("Database connection error", exception.getMessage());
        verify(customerDataProvider).countOrdersByCustomerId(customerId);
    }

    @Test
    @DisplayName("Should call data provider exactly once for valid customer ID")
    void shouldCallDataProviderExactlyOnceForValidCustomerId() {
        // Given
        Long customerId = 500L;
        when(customerDataProvider.countOrdersByCustomerId(customerId)).thenReturn(3L);

        // When
        getCustomerOrderCountUseCase.execute(customerId);

        // Then
        verify(customerDataProvider, times(1)).countOrdersByCustomerId(customerId);
        verifyNoMoreInteractions(customerDataProvider);
    }

    @Test
    @DisplayName("Should handle multiple consecutive calls correctly")
    void shouldHandleMultipleConsecutiveCallsCorrectly() {
        // Given
        Long customerId1 = 100L;
        Long customerId2 = 200L;
        when(customerDataProvider.countOrdersByCustomerId(customerId1)).thenReturn(5L);
        when(customerDataProvider.countOrdersByCustomerId(customerId2)).thenReturn(3L);

        // When
        long result1 = getCustomerOrderCountUseCase.execute(customerId1);
        long result2 = getCustomerOrderCountUseCase.execute(customerId2);

        // Then
        assertEquals(5L, result1);
        assertEquals(3L, result2);
        verify(customerDataProvider).countOrdersByCustomerId(customerId1);
        verify(customerDataProvider).countOrdersByCustomerId(customerId2);
    }

    private static Stream<Arguments> validCustomerIdAndCountProvider() {
        return Stream.of(
                Arguments.of(100L, 5L),           // Should return order count for valid customer ID
                Arguments.of(200L, 0L),           // Should return zero when customer has no orders
                Arguments.of(300L, 1000L),        // Should return large count for customer with many orders
                Arguments.of(1L, 2L)              // Should handle edge case with minimum valid customer ID
        );
    }
}
