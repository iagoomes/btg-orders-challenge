package com.btg.challenge.orders.app.resource;

import com.btg.challenge.orders.model.HealthResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HealthResource Unit Tests")
class HealthResourceTest {

    @InjectMocks
    private HealthResource healthResource;

    @Test
    @DisplayName("Should return UP status for health check")
    void shouldReturnUpStatusForHealthCheck() throws ExecutionException, InterruptedException {
        // When
        CompletableFuture<ResponseEntity<HealthResponse>> future = healthResource.healthCheck();
        ResponseEntity<HealthResponse> response = future.get();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().getStatus());
    }

    @Test
    @DisplayName("Should execute health check asynchronously")
    void shouldExecuteHealthCheckAsynchronously() {
        // When
        CompletableFuture<ResponseEntity<HealthResponse>> future = healthResource.healthCheck();

        // Then
        assertNotNull(future);
        assertFalse(future.isDone()); // Should be running asynchronously
        // Wait for completion
        assertDoesNotThrow(() -> future.get());
        assertTrue(future.isDone());
    }

    @Test
    @DisplayName("Should always return same status regardless of multiple calls")
    void shouldAlwaysReturnSameStatusRegardlessOfMultipleCalls() throws ExecutionException, InterruptedException {
        // When
        CompletableFuture<ResponseEntity<HealthResponse>> future1 = healthResource.healthCheck();
        CompletableFuture<ResponseEntity<HealthResponse>> future2 = healthResource.healthCheck();

        ResponseEntity<HealthResponse> response1 = future1.get();
        ResponseEntity<HealthResponse> response2 = future2.get();

        // Then
        assertNotNull(response1);
        assertNotNull(response2);
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals("UP", response1.getBody().getStatus());
        assertEquals("UP", response2.getBody().getStatus());
    }

    @Test
    @DisplayName("Should create new HealthResponse instance for each call")
    void shouldCreateNewHealthResponseInstanceForEachCall() throws ExecutionException, InterruptedException {
        // When
        CompletableFuture<ResponseEntity<HealthResponse>> future1 = healthResource.healthCheck();
        CompletableFuture<ResponseEntity<HealthResponse>> future2 = healthResource.healthCheck();

        ResponseEntity<HealthResponse> response1 = future1.get();
        ResponseEntity<HealthResponse> response2 = future2.get();

        // Then
        assertNotSame(response1.getBody(), response2.getBody());
        assertEquals(response1.getBody().getStatus(), response2.getBody().getStatus());
    }
}
