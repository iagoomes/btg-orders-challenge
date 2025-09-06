package com.btg.challenge.orders.app.resource;

import com.btg.challenge.orders.app.service.CustomerService;
import com.btg.challenge.orders.model.CustomerOrderCountResponse;
import com.btg.challenge.orders.model.CustomerOrdersResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomersResource Unit Tests")
class CustomersResourceTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomersResource customersResource;

    private CustomerOrderCountResponse orderCountResponse;
    private CustomerOrdersResponse ordersResponse;

    @BeforeEach
    void setUp() {
        orderCountResponse = new CustomerOrderCountResponse();
        ordersResponse = new CustomerOrdersResponse();
    }

    @Test
    @DisplayName("Should get customer order count successfully")
    void shouldGetCustomerOrderCountSuccessfully() throws ExecutionException, InterruptedException {
        // Given
        Long customerId = 1L;
        when(customerService.getOrderCount(customerId)).thenReturn(orderCountResponse);

        // When
        CompletableFuture<ResponseEntity<CustomerOrderCountResponse>> future =
            customersResource.getCustomerOrderCount(customerId);
        ResponseEntity<CustomerOrderCountResponse> response = future.get();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderCountResponse, response.getBody());
        verify(customerService, times(1)).getOrderCount(customerId);
    }

    @Test
    @DisplayName("Should get customer orders successfully")
    void shouldGetCustomerOrdersSuccessfully() throws ExecutionException, InterruptedException {
        // Given
        Long customerId = 1L;
        Integer page = 0;
        Integer size = 10;
        Pageable pageable = PageRequest.of(page, size);
        when(customerService.getCustomerOrders(customerId, pageable)).thenReturn(ordersResponse);

        // When
        CompletableFuture<ResponseEntity<CustomerOrdersResponse>> future =
            customersResource.getCustomerOrders(customerId, page, size, pageable);
        ResponseEntity<CustomerOrdersResponse> response = future.get();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ordersResponse, response.getBody());
        verify(customerService, times(1)).getCustomerOrders(customerId, pageable);
    }

    @Test
    @DisplayName("Should handle service exception in getCustomerOrderCount")
    void shouldHandleServiceExceptionInGetCustomerOrderCount() {
        // Given
        Long customerId = 1L;
        RuntimeException serviceException = new RuntimeException("Service error");
        when(customerService.getOrderCount(customerId)).thenThrow(serviceException);

        // When
        CompletableFuture<ResponseEntity<CustomerOrderCountResponse>> future =
            customersResource.getCustomerOrderCount(customerId);

        // Then
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertEquals(RuntimeException.class, exception.getCause().getClass());
        assertEquals("Service error", exception.getCause().getMessage());
        verify(customerService, times(1)).getOrderCount(customerId);
    }

    @Test
    @DisplayName("Should handle service exception in getCustomerOrders")
    void shouldHandleServiceExceptionInGetCustomerOrders() {
        // Given
        Long customerId = 1L;
        Integer page = 0;
        Integer size = 10;
        Pageable pageable = PageRequest.of(page, size);
        RuntimeException serviceException = new RuntimeException("Service error");
        when(customerService.getCustomerOrders(customerId, pageable)).thenThrow(serviceException);

        // When
        CompletableFuture<ResponseEntity<CustomerOrdersResponse>> future =
            customersResource.getCustomerOrders(customerId, page, size, pageable);

        // Then
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertEquals(RuntimeException.class, exception.getCause().getClass());
        assertEquals("Service error", exception.getCause().getMessage());
        verify(customerService, times(1)).getCustomerOrders(customerId, pageable);
    }

    @Test
    @DisplayName("Should execute asynchronously for getCustomerOrderCount")
    void shouldExecuteAsynchronouslyForGetCustomerOrderCount() {
        // Given
        Long customerId = 1L;
        when(customerService.getOrderCount(customerId)).thenReturn(orderCountResponse);

        // When
        CompletableFuture<ResponseEntity<CustomerOrderCountResponse>> future =
            customersResource.getCustomerOrderCount(customerId);

        // Then
        assertNotNull(future);
        assertFalse(future.isDone()); // Should be running asynchronously
        // Wait for completion
        assertDoesNotThrow(() -> future.get());
        assertTrue(future.isDone());
    }

    @Test
    @DisplayName("Should execute asynchronously for getCustomerOrders")
    void shouldExecuteAsynchronouslyForGetCustomerOrders() {
        // Given
        Long customerId = 1L;
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        when(customerService.getCustomerOrders(customerId, pageable)).thenReturn(ordersResponse);

        // When
        CompletableFuture<ResponseEntity<CustomerOrdersResponse>> future =
            customersResource.getCustomerOrders(customerId, page, size, pageable);

        // Then
        assertNotNull(future);
        assertFalse(future.isDone()); // Should be running asynchronously
        // Wait for completion
        assertDoesNotThrow(() -> future.get());
        assertTrue(future.isDone());
    }
}
