package com.btg.challenge.orders.app.resource;

import com.btg.challenge.orders.app.service.OrderService;
import com.btg.challenge.orders.model.OrderTotalResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrdersResource Unit Tests")
class OrdersResourceTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrdersResource ordersResource;

    private OrderTotalResponse orderTotalResponse;

    @BeforeEach
    void setUp() {
        orderTotalResponse = new OrderTotalResponse();
    }

    @Test
    @DisplayName("Should get order total successfully")
    void shouldGetOrderTotalSuccessfully() throws ExecutionException, InterruptedException {
        // Given
        Long orderId = 1L;
        when(orderService.getOrderTotal(orderId)).thenReturn(orderTotalResponse);

        // When
        CompletableFuture<ResponseEntity<OrderTotalResponse>> future =
            ordersResource.getOrderTotal(orderId);
        ResponseEntity<OrderTotalResponse> response = future.get();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderTotalResponse, response.getBody());
        verify(orderService, times(1)).getOrderTotal(orderId);
    }

    @Test
    @DisplayName("Should handle service exception in getOrderTotal")
    void shouldHandleServiceExceptionInGetOrderTotal() {
        // Given
        Long orderId = 1L;
        RuntimeException serviceException = new RuntimeException("Service error");
        when(orderService.getOrderTotal(orderId)).thenThrow(serviceException);

        // When
        CompletableFuture<ResponseEntity<OrderTotalResponse>> future =
            ordersResource.getOrderTotal(orderId);

        // Then
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertEquals(RuntimeException.class, exception.getCause().getClass());
        assertEquals("Service error", exception.getCause().getMessage());
        verify(orderService, times(1)).getOrderTotal(orderId);
    }

    @Test
    @DisplayName("Should execute asynchronously for getOrderTotal")
    void shouldExecuteAsynchronouslyForGetOrderTotal() {
        // Given
        Long orderId = 1L;
        when(orderService.getOrderTotal(orderId)).thenReturn(orderTotalResponse);

        // When
        CompletableFuture<ResponseEntity<OrderTotalResponse>> future =
            ordersResource.getOrderTotal(orderId);

        // Then
        assertNotNull(future);
        assertFalse(future.isDone()); // Should be running asynchronously
        // Wait for completion
        assertDoesNotThrow(() -> future.get());
        assertTrue(future.isDone());
    }

    @Test
    @DisplayName("Should handle null order ID gracefully")
    void shouldHandleNullOrderIdGracefully() throws ExecutionException, InterruptedException {
        // Given
        Long orderId = null;
        when(orderService.getOrderTotal(orderId)).thenReturn(orderTotalResponse);

        // When
        CompletableFuture<ResponseEntity<OrderTotalResponse>> future =
            ordersResource.getOrderTotal(orderId);
        ResponseEntity<OrderTotalResponse> response = future.get();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderTotalResponse, response.getBody());
        verify(orderService, times(1)).getOrderTotal(orderId);
    }

    @Test
    @DisplayName("Should handle different order IDs correctly")
    void shouldHandleDifferentOrderIdsCorrectly() throws ExecutionException, InterruptedException {
        // Given
        Long orderId1 = 1L;
        Long orderId2 = 2L;
        OrderTotalResponse response1 = new OrderTotalResponse();
        OrderTotalResponse response2 = new OrderTotalResponse();

        when(orderService.getOrderTotal(orderId1)).thenReturn(response1);
        when(orderService.getOrderTotal(orderId2)).thenReturn(response2);

        // When
        CompletableFuture<ResponseEntity<OrderTotalResponse>> future1 =
            ordersResource.getOrderTotal(orderId1);
        CompletableFuture<ResponseEntity<OrderTotalResponse>> future2 =
            ordersResource.getOrderTotal(orderId2);

        ResponseEntity<OrderTotalResponse> result1 = future1.get();
        ResponseEntity<OrderTotalResponse> result2 = future2.get();

        // Then
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(HttpStatus.OK, result1.getStatusCode());
        assertEquals(HttpStatus.OK, result2.getStatusCode());
        assertEquals(response1, result1.getBody());
        assertEquals(response2, result2.getBody());
        verify(orderService, times(1)).getOrderTotal(orderId1);
        verify(orderService, times(1)).getOrderTotal(orderId2);
    }
}
