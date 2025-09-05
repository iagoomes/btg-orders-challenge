package com.btg.challenge.orders.infra.exception;

import com.btg.challenge.orders.model.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    @Mock
    private WebRequest webRequest;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private ConstraintViolation<Object> constraintViolation;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private static final String TEST_PATH = "/api/orders/123";

    @BeforeEach
    void setUp() {
        when(webRequest.getDescription(false)).thenReturn("uri=" + TEST_PATH);
    }

    @Test
    @DisplayName("Should handle OrderNotFoundException correctly")
    void shouldHandleOrderNotFoundException() {
        // Given
        String errorMessage = "Order not found with id: 123";
        OrderNotFoundException exception = new OrderNotFoundException(errorMessage);

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleOrderNotFound(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals("Order Not Found", errorResponse.getError());
        assertEquals(errorMessage, errorResponse.getMessage());
        assertEquals(TEST_PATH, errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
        assertTrue(errorResponse.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Should handle CustomerNotFoundException correctly")
    void shouldHandleCustomerNotFoundException() {
        // Given
        String errorMessage = "Customer not found with id: 456";
        CustomerNotFoundException exception = new CustomerNotFoundException(errorMessage);

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleCustomerNotFound(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals("Customer Not Found", errorResponse.getError());
        assertEquals(errorMessage, errorResponse.getMessage());
        assertEquals(TEST_PATH, errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    @DisplayName("Should handle ConstraintViolationException correctly")
    void shouldHandleConstraintViolationException() {
        // Given
        String violationMessage = "Field value is invalid";
        when(constraintViolation.getMessage()).thenReturn(violationMessage);

        ConstraintViolationException exception = new ConstraintViolationException(
            "Validation failed", Set.of(constraintViolation)
        );

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleConstraintViolation(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("Invalid Request Parameters", errorResponse.getError());
        assertEquals(violationMessage, errorResponse.getMessage());
        assertEquals(TEST_PATH, errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException correctly")
    void shouldHandleMethodArgumentNotValidException() {
        // Given
        FieldError fieldError = new FieldError("order", "customerId", "Customer ID is required");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
            mock(org.springframework.core.MethodParameter.class), bindingResult
        );

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationErrors(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("Validation Failed", errorResponse.getError());
        assertEquals("Customer ID is required", errorResponse.getMessage());
        assertEquals(TEST_PATH, errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    @DisplayName("Should handle MethodArgumentTypeMismatchException correctly")
    void shouldHandleMethodArgumentTypeMismatchException() {
        // Given
        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
            "abc", Long.class, "orderId", mock(org.springframework.core.MethodParameter.class),
            new NumberFormatException("For input string: \"abc\"")
        );

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleTypeMismatch(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("Invalid Parameter Type", errorResponse.getError());
        assertTrue(errorResponse.getMessage().contains("Invalid value 'abc' for parameter 'orderId'"));
        assertTrue(errorResponse.getMessage().contains("Expected type: Long"));
        assertEquals(TEST_PATH, errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException correctly")
    void shouldHandleIllegalArgumentException() {
        // Given
        String errorMessage = "Invalid argument provided";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleIllegalArgument(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("Invalid Request", errorResponse.getError());
        assertEquals(errorMessage, errorResponse.getMessage());
        assertEquals(TEST_PATH, errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    @DisplayName("Should handle generic Exception correctly")
    void shouldHandleGenericException() {
        // Given
        RuntimeException exception = new RuntimeException("Unexpected error occurred");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception, webRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
        assertEquals("Internal Server Error", errorResponse.getError());
        assertEquals("An unexpected error occurred. Please try again later.", errorResponse.getMessage());
        assertEquals(TEST_PATH, errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
    }
}
