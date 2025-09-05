package com.btg.challenge.orders.infra.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderMessageNullException Unit Tests")
class OrderMessageNullExceptionTest {

    @Test
    @DisplayName("Should create exception with default message using default constructor")
    void shouldCreateExceptionWithDefaultMessageUsingDefaultConstructor() {
        // When
        OrderMessageNullException exception = new OrderMessageNullException();

        // Then
        assertNotNull(exception);
        assertEquals("Order message cannot be null", exception.getMessage());
        assertTrue(exception instanceof IllegalArgumentException);
    }

    @Test
    @DisplayName("Should create exception with custom message using parameterized constructor")
    void shouldCreateExceptionWithCustomMessageUsingParameterizedConstructor() {
        // Given
        String customMessage = "Custom error message for null order";

        // When
        OrderMessageNullException exception = new OrderMessageNullException(customMessage);

        // Then
        assertNotNull(exception);
        assertEquals(customMessage, exception.getMessage());
        assertTrue(exception instanceof IllegalArgumentException);
    }

    @Test
    @DisplayName("Should handle null message in parameterized constructor")
    void shouldHandleNullMessageInParameterizedConstructor() {
        // When
        OrderMessageNullException exception = new OrderMessageNullException(null);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertTrue(exception instanceof IllegalArgumentException);
    }

    @Test
    @DisplayName("Should handle empty message in parameterized constructor")
    void shouldHandleEmptyMessageInParameterizedConstructor() {
        // Given
        String emptyMessage = "";

        // When
        OrderMessageNullException exception = new OrderMessageNullException(emptyMessage);

        // Then
        assertNotNull(exception);
        assertEquals(emptyMessage, exception.getMessage());
        assertTrue(exception instanceof IllegalArgumentException);
    }

    @Test
    @DisplayName("Should handle whitespace-only message in parameterized constructor")
    void shouldHandleWhitespaceOnlyMessageInParameterizedConstructor() {
        // Given
        String whitespaceMessage = "   ";

        // When
        OrderMessageNullException exception = new OrderMessageNullException(whitespaceMessage);

        // Then
        assertNotNull(exception);
        assertEquals(whitespaceMessage, exception.getMessage());
        assertTrue(exception instanceof IllegalArgumentException);
    }

    @Test
    @DisplayName("Should be throwable as IllegalArgumentException")
    void shouldBeThrowableAsIllegalArgumentException() {
        // Given
        String message = "Test exception message";

        // When & Then
        assertThrows(OrderMessageNullException.class, () -> {
            throw new OrderMessageNullException(message);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            throw new OrderMessageNullException(message);
        });
    }

    @Test
    @DisplayName("Should maintain exception hierarchy")
    void shouldMaintainExceptionHierarchy() {
        // When
        OrderMessageNullException exception = new OrderMessageNullException("Test");

        // Then
        assertTrue(exception instanceof IllegalArgumentException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    @DisplayName("Should preserve stack trace when thrown")
    void shouldPreserveStackTraceWhenThrown() {
        // When & Then
        try {
            throw new OrderMessageNullException("Stack trace test");
        } catch (OrderMessageNullException e) {
            assertNotNull(e.getStackTrace());
            assertTrue(e.getStackTrace().length > 0);
            assertEquals("Stack trace test", e.getMessage());
        }
    }
}
