package com.btg.challenge.orders.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderItem Unit Tests")
class OrderItemTest {

    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        orderItem = new OrderItem();
    }

    @Test
    @DisplayName("Should create OrderItem with default constructor")
    void shouldCreateOrderItemWithDefaultConstructor() {
        // Then
        assertNotNull(orderItem);
        assertNull(orderItem.getProduct());
        assertNull(orderItem.getQuantity());
        assertNull(orderItem.getPrice());
        assertEquals(BigDecimal.ZERO, orderItem.getTotalPrice());
    }

    @Test
    @DisplayName("Should create OrderItem with parametrized constructor and calculate total price")
    void shouldCreateOrderItemWithParametrizedConstructorAndCalculateTotalPrice() {
        // Given
        String product = "Notebook";
        Integer quantity = 2;
        BigDecimal price = new BigDecimal("1500.00");

        // When
        OrderItem item = new OrderItem(product, quantity, price);

        // Then
        assertNotNull(item);
        assertEquals(product, item.getProduct());
        assertEquals(quantity, item.getQuantity());
        assertEquals(price, item.getPrice());
        assertEquals(new BigDecimal("3000.00"), item.getTotalPrice());
    }

    @Test
    @DisplayName("Should update total price when price or quantity changes")
    void shouldUpdateTotalPriceWhenPriceOrQuantityChanges() {
        // Given
        orderItem.setProduct("Mouse");
        orderItem.setQuantity(3);
        orderItem.setPrice(new BigDecimal("50.00"));

        // Then
        assertEquals(new BigDecimal("150.00"), orderItem.getTotalPrice());

        // When - change quantity
        orderItem.setQuantity(5);

        // Then
        assertEquals(new BigDecimal("250.00"), orderItem.getTotalPrice());

        // When - change price
        orderItem.setPrice(new BigDecimal("60.00"));

        // Then
        assertEquals(new BigDecimal("300.00"), orderItem.getTotalPrice());
    }

    @Test
    @DisplayName("Should return zero total price when price or quantity is null")
    void shouldReturnZeroTotalPriceWhenPriceOrQuantityIsNull() {
        // Given
        orderItem.setProduct("Keyboard");
        orderItem.setPrice(new BigDecimal("100.00"));
        orderItem.setQuantity(null);

        // When
        orderItem.updateTotalPrice();

        // Then
        assertEquals(BigDecimal.ZERO, orderItem.getTotalPrice());

        // Given
        orderItem.setQuantity(2);
        orderItem.setPrice(null);

        // When
        orderItem.updateTotalPrice();

        // Then
        assertEquals(BigDecimal.ZERO, orderItem.getTotalPrice());
    }

    @Test
    @DisplayName("Should validate OrderItem correctly")
    void shouldValidateOrderItemCorrectly() {
        // Given - invalid cases
        assertFalse(orderItem.isValid()); // all null

        orderItem.setProduct("");
        assertFalse(orderItem.isValid()); // empty product

        orderItem.setProduct("   ");
        assertFalse(orderItem.isValid()); // blank product

        orderItem.setProduct("ValidProduct");
        orderItem.setQuantity(0);
        assertFalse(orderItem.isValid()); // zero quantity

        orderItem.setQuantity(-1);
        assertFalse(orderItem.isValid()); // negative quantity

        orderItem.setQuantity(1);
        orderItem.setPrice(new BigDecimal("-10.00"));
        assertFalse(orderItem.isValid()); // negative price

        // Given - valid case
        orderItem.setProduct("ValidProduct");
        orderItem.setQuantity(1);
        orderItem.setPrice(new BigDecimal("10.00"));

        // Then
        assertTrue(orderItem.isValid());

        // Given - valid case with zero price
        orderItem.setPrice(BigDecimal.ZERO);

        // Then
        assertTrue(orderItem.isValid()); // zero price is valid
    }

    @Test
    @DisplayName("Should validate individual null cases for isValid method")
    void shouldValidateIndividualNullCasesForIsValidMethod() {
        // Test when only product is null
        orderItem.setProduct(null);
        orderItem.setQuantity(1);
        orderItem.setPrice(new BigDecimal("10.00"));
        assertFalse(orderItem.isValid());

        // Test when only quantity is null
        orderItem.setProduct("ValidProduct");
        orderItem.setQuantity(null);
        orderItem.setPrice(new BigDecimal("10.00"));
        assertFalse(orderItem.isValid());

        // Test when only price is null
        orderItem.setProduct("ValidProduct");
        orderItem.setQuantity(1);
        orderItem.setPrice(null);
        assertFalse(orderItem.isValid());

        // Test when product and quantity are null
        orderItem.setProduct(null);
        orderItem.setQuantity(null);
        orderItem.setPrice(new BigDecimal("10.00"));
        assertFalse(orderItem.isValid());

        // Test when product and price are null
        orderItem.setProduct(null);
        orderItem.setQuantity(1);
        orderItem.setPrice(null);
        assertFalse(orderItem.isValid());

        // Test when quantity and price are null
        orderItem.setProduct("ValidProduct");
        orderItem.setQuantity(null);
        orderItem.setPrice(null);
        assertFalse(orderItem.isValid());
    }

    @Test
    @DisplayName("Should validate edge cases for product validation")
    void shouldValidateEdgeCasesForProductValidation() {
        // Test with whitespace-only product
        orderItem.setProduct("\t\n\r ");
        orderItem.setQuantity(1);
        orderItem.setPrice(new BigDecimal("10.00"));
        assertFalse(orderItem.isValid());

        // Test with mixed whitespace product
        orderItem.setProduct("  \t  ");
        assertFalse(orderItem.isValid());

        // Test with single space
        orderItem.setProduct(" ");
        assertFalse(orderItem.isValid());

        // Test with valid product that has leading/trailing spaces
        orderItem.setProduct("  ValidProduct  ");
        assertTrue(orderItem.isValid()); // Should be valid as trim() removes spaces
    }

    @Test
    @DisplayName("Should validate edge cases for quantity validation")
    void shouldValidateEdgeCasesForQuantityValidation() {
        orderItem.setProduct("ValidProduct");
        orderItem.setPrice(new BigDecimal("10.00"));

        // Test with exactly zero quantity
        orderItem.setQuantity(0);
        assertFalse(orderItem.isValid());

        // Test with exactly one quantity (boundary)
        orderItem.setQuantity(1);
        assertTrue(orderItem.isValid());

        // Test with large positive quantity
        orderItem.setQuantity(Integer.MAX_VALUE);
        assertTrue(orderItem.isValid());

        // Test with minimum negative quantity
        orderItem.setQuantity(Integer.MIN_VALUE);
        assertFalse(orderItem.isValid());
    }

    @Test
    @DisplayName("Should validate edge cases for price validation")
    void shouldValidateEdgeCasesForPriceValidation() {
        orderItem.setProduct("ValidProduct");
        orderItem.setQuantity(1);

        // Test with exactly zero price (boundary)
        orderItem.setPrice(BigDecimal.ZERO);
        assertTrue(orderItem.isValid());

        // Test with positive price
        orderItem.setPrice(new BigDecimal("0.01"));
        assertTrue(orderItem.isValid());

        // Test with large positive price
        orderItem.setPrice(new BigDecimal("999999999999.99"));
        assertTrue(orderItem.isValid());

        // Test with slightly negative price
        orderItem.setPrice(new BigDecimal("-0.01"));
        assertFalse(orderItem.isValid());

        // Test with very large negative price
        orderItem.setPrice(new BigDecimal("-999999999999.99"));
        assertFalse(orderItem.isValid());
    }

    @Test
    @DisplayName("Should handle decimal quantities in price calculation")
    void shouldHandleDecimalQuantitiesInPriceCalculation() {
        // Given
        orderItem.setProduct("Product");
        orderItem.setQuantity(3);
        orderItem.setPrice(new BigDecimal("33.33"));

        // When
        orderItem.updateTotalPrice();

        // Then
        assertEquals(new BigDecimal("99.99"), orderItem.getTotalPrice());
    }

    @Test
    @DisplayName("Should handle large numbers correctly")
    void shouldHandleLargeNumbersCorrectly() {
        // Given
        orderItem.setProduct("Expensive Item");
        orderItem.setQuantity(1000);
        orderItem.setPrice(new BigDecimal("999999.99"));

        // When
        orderItem.updateTotalPrice();

        // Then
        assertEquals(new BigDecimal("999999990.00"), orderItem.getTotalPrice());
    }

    @Test
    @DisplayName("Should maintain total price consistency after multiple updates")
    void shouldMaintainTotalPriceConsistencyAfterMultipleUpdates() {
        // Given
        orderItem.setProduct("Test Product");
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("10.00"));

        // Initial state
        assertEquals(new BigDecimal("20.00"), orderItem.getTotalPrice());

        // When - multiple updates
        orderItem.setQuantity(3);
        orderItem.setPrice(new BigDecimal("15.00"));
        orderItem.setQuantity(1);

        // Then
        assertEquals(new BigDecimal("15.00"), orderItem.getTotalPrice());
    }

    @Test
    @DisplayName("Should handle getTotalPrice when totalPrice is null")
    void shouldHandleGetTotalPriceWhenTotalPriceIsNull() {
        // Given
        orderItem.setProduct("Product");
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("10.00"));
        orderItem.setTotalPrice(null); // Force null

        // When
        BigDecimal totalPrice = orderItem.getTotalPrice();

        // Then
        assertEquals(new BigDecimal("20.00"), totalPrice);
        assertNotNull(orderItem.getTotalPrice()); // Should be calculated and stored
    }

    @Test
    @DisplayName("Should set and get all properties correctly")
    void shouldSetAndGetAllPropertiesCorrectly() {
        // Given
        Long itemId = 123L;
        String product = "Test Product";
        Integer quantity = 5;
        BigDecimal price = new BigDecimal("25.50");
        BigDecimal totalPrice = new BigDecimal("127.50");

        // When
        orderItem.setItemId(itemId);
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(price);
        orderItem.setTotalPrice(totalPrice);

        // Then
        assertEquals(itemId, orderItem.getItemId());
        assertEquals(product, orderItem.getProduct());
        assertEquals(quantity, orderItem.getQuantity());
        assertEquals(price, orderItem.getPrice());
        assertEquals(totalPrice, orderItem.getTotalPrice());
    }
}
