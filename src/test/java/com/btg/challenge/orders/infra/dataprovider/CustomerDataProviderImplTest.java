package com.btg.challenge.orders.infra.dataprovider;

import com.btg.challenge.orders.domain.entity.Customer;
import com.btg.challenge.orders.infra.dataprovider.mapper.CustomerRepositoryMapper;
import com.btg.challenge.orders.infra.repository.CustomerDataRepository;
import com.btg.challenge.orders.infra.repository.model.CustomerData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerDataProviderImpl Unit Tests")
class CustomerDataProviderImplTest {

    @Mock
    private CustomerDataRepository customerRepository;

    @Mock
    private CustomerRepositoryMapper customerMapper;

    @InjectMocks
    private CustomerDataProviderImpl customerDataProvider;

    private Customer customer;
    private CustomerData customerData;

    @BeforeEach
    void setUp() {
        customer = new Customer(100L);
        customer.setCreatedAt(LocalDateTime.now());

        customerData = CustomerData.builder()
                .customerId(100L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should save customer successfully")
    void shouldSaveCustomerSuccessfully() {
        // Given
        CustomerData savedCustomerData = CustomerData.builder()
                .customerId(100L)
                .createdAt(LocalDateTime.now())
                .build();
        Customer savedCustomer = new Customer(100L);

        when(customerMapper.toData(customer)).thenReturn(customerData);
        when(customerRepository.save(customerData)).thenReturn(savedCustomerData);
        when(customerMapper.toDomain(savedCustomerData)).thenReturn(savedCustomer);

        // When
        Customer result = customerDataProvider.save(customer);

        // Then
        assertNotNull(result);
        assertEquals(savedCustomer, result);
        verify(customerMapper).toData(customer);
        verify(customerRepository).save(customerData);
        verify(customerMapper).toDomain(savedCustomerData);
    }

    @Test
    @DisplayName("Should handle null customer in save")
    void shouldHandleNullCustomerInSave() {
        // Given
        Customer nullCustomer = null;
        when(customerMapper.toData(nullCustomer)).thenReturn(null);
        when(customerRepository.save(null)).thenReturn(customerData);
        when(customerMapper.toDomain(customerData)).thenReturn(customer);

        // When
        Customer result = customerDataProvider.save(nullCustomer);

        // Then
        assertNotNull(result);
        verify(customerMapper).toData(nullCustomer);
        verify(customerRepository).save(null);
        verify(customerMapper).toDomain(customerData);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 100L, 999L})
    @DisplayName("Should find customer by ID successfully")
    void shouldFindCustomerByIdSuccessfully(Long customerId) {
        // Given
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customerData));
        when(customerMapper.toDomain(customerData)).thenReturn(customer);

        // When
        Optional<Customer> result = customerDataProvider.findById(customerId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(customer, result.get());
        verify(customerRepository).findById(customerId);
        verify(customerMapper).toDomain(customerData);
    }

    @Test
    @DisplayName("Should return empty when customer not found by ID")
    void shouldReturnEmptyWhenCustomerNotFoundById() {
        // Given
        Long customerId = 999L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // When
        Optional<Customer> result = customerDataProvider.findById(customerId);

        // Then
        assertFalse(result.isPresent());
        verify(customerRepository).findById(customerId);
        verify(customerMapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should find customer by ID with orders successfully")
    void shouldFindCustomerByIdWithOrdersSuccessfully() {
        // Given
        Long customerId = 100L;
        when(customerRepository.findByIdWithOrders(customerId)).thenReturn(Optional.of(customerData));
        when(customerMapper.toDomain(customerData)).thenReturn(customer);

        // When
        Optional<Customer> result = customerDataProvider.findByIdWithOrders(customerId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(customer, result.get());
        verify(customerRepository).findByIdWithOrders(customerId);
        verify(customerMapper).toDomain(customerData);
    }

    @Test
    @DisplayName("Should return empty when customer with orders not found")
    void shouldReturnEmptyWhenCustomerWithOrdersNotFound() {
        // Given
        Long customerId = 999L;
        when(customerRepository.findByIdWithOrders(customerId)).thenReturn(Optional.empty());

        // When
        Optional<Customer> result = customerDataProvider.findByIdWithOrders(customerId);

        // Then
        assertFalse(result.isPresent());
        verify(customerRepository).findByIdWithOrders(customerId);
        verify(customerMapper, never()).toDomain(any());
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 5L, 100L, 1000L})
    @DisplayName("Should count orders by customer ID correctly")
    void shouldCountOrdersByCustomerIdCorrectly(long expectedCount) {
        // Given
        Long customerId = 100L;
        when(customerRepository.countOrdersByCustomerId(customerId)).thenReturn(expectedCount);

        // When
        long result = customerDataProvider.countOrdersByCustomerId(customerId);

        // Then
        assertEquals(expectedCount, result);
        verify(customerRepository).countOrdersByCustomerId(customerId);
    }

    @Test
    @DisplayName("Should handle repository exception in save")
    void shouldHandleRepositoryExceptionInSave() {
        // Given
        when(customerMapper.toData(customer)).thenReturn(customerData);
        when(customerRepository.save(customerData)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerDataProvider.save(customer));

        assertEquals("Database error", exception.getMessage());
        verify(customerMapper).toData(customer);
        verify(customerRepository).save(customerData);
        verify(customerMapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should handle mapper exception in save")
    void shouldHandleMapperExceptionInSave() {
        // Given
        when(customerMapper.toData(customer)).thenThrow(new RuntimeException("Mapper error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerDataProvider.save(customer));

        assertEquals("Mapper error", exception.getMessage());
        verify(customerMapper).toData(customer);
        verify(customerRepository, never()).save(any());
        verify(customerMapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should handle repository exception in findById")
    void shouldHandleRepositoryExceptionInFindById() {
        // Given
        Long customerId = 100L;
        when(customerRepository.findById(customerId)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerDataProvider.findById(customerId));

        assertEquals("Database error", exception.getMessage());
        verify(customerRepository).findById(customerId);
        verify(customerMapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should handle repository exception in countOrdersByCustomerId")
    void shouldHandleRepositoryExceptionInCountOrdersByCustomerId() {
        // Given
        Long customerId = 100L;
        when(customerRepository.countOrdersByCustomerId(customerId))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerDataProvider.countOrdersByCustomerId(customerId));

        assertEquals("Database error", exception.getMessage());
        verify(customerRepository).countOrdersByCustomerId(customerId);
    }

    @Test
    @DisplayName("Should handle null customer ID in findById")
    void shouldHandleNullCustomerIdInFindById() {
        // Given
        Long nullCustomerId = null;
        when(customerRepository.findById(nullCustomerId)).thenReturn(Optional.empty());

        // When
        Optional<Customer> result = customerDataProvider.findById(nullCustomerId);

        // Then
        assertFalse(result.isPresent());
        verify(customerRepository).findById(nullCustomerId);
        verify(customerMapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should verify no side effects in failed operations")
    void shouldVerifyNoSideEffectsInFailedOperations() {
        // Given
        when(customerMapper.toData(customer)).thenReturn(customerData);
        when(customerRepository.save(customerData)).thenThrow(new RuntimeException("Save failed"));

        // When & Then
        assertThrows(RuntimeException.class, () -> customerDataProvider.save(customer));

        // Verify no additional interactions
        verifyNoMoreInteractions(customerMapper);
        verifyNoMoreInteractions(customerRepository);
    }
}
