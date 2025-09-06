package com.btg.challenge.orders.infra.repository.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderItemData Unit Tests")
class OrderItemDataTest {

    private OrderItemData orderItemData;
    private OrderData orderData;

    @BeforeEach
    void setUp() {
        orderItemData = new OrderItemData();
        orderData = new OrderData();
    }

    @Test
    @DisplayName("Should create OrderItemData with default constructor")
    void shouldCreateOrderItemDataWithDefaultConstructor() {
        // When
        OrderItemData orderItem = new OrderItemData();

        // Then
        assertNotNull(orderItem);
        assertNull(orderItem.getItemId());
        assertNull(orderItem.getOrder());
        assertNull(orderItem.getProduct());
        assertNull(orderItem.getQuantity());
        assertNull(orderItem.getPrice());
        assertNull(orderItem.getTotalPrice());
        assertNull(orderItem.getCreatedAt());
    }

    @Test
    @DisplayName("Should create OrderItemData with all args constructor")
    void shouldCreateOrderItemDataWithAllArgsConstructor() {
        // Given
        Long itemId = 1L;
        OrderData order = new OrderData();
        String product = "Test Product";
        Integer quantity = 2;
        BigDecimal price = BigDecimal.valueOf(25.50);
        BigDecimal totalPrice = BigDecimal.valueOf(51.00);
        LocalDateTime createdAt = LocalDateTime.now();

        // When
        OrderItemData orderItem = new OrderItemData(itemId, order, product, quantity, price, totalPrice, createdAt);

        // Then
        assertNotNull(orderItem);
        assertEquals(itemId, orderItem.getItemId());
        assertEquals(order, orderItem.getOrder());
        assertEquals(product, orderItem.getProduct());
        assertEquals(quantity, orderItem.getQuantity());
        assertEquals(price, orderItem.getPrice());
        assertEquals(totalPrice, orderItem.getTotalPrice());
        assertEquals(createdAt, orderItem.getCreatedAt());
    }

    @Test
    @DisplayName("Should create OrderItemData with builder")
    void shouldCreateOrderItemDataWithBuilder() {
        // Given
        Long itemId = 123L;
        OrderData order = OrderData.builder().orderId(456L).build();
        String product = "Builder Product";
        Integer quantity = 3;
        BigDecimal price = BigDecimal.valueOf(15.99);
        BigDecimal totalPrice = BigDecimal.valueOf(47.97);
        LocalDateTime createdAt = LocalDateTime.now();

        // When
        OrderItemData orderItem = OrderItemData.builder()
                .itemId(itemId)
                .order(order)
                .product(product)
                .quantity(quantity)
                .price(price)
                .totalPrice(totalPrice)
                .createdAt(createdAt)
                .build();

        // Then
        assertNotNull(orderItem);
        assertEquals(itemId, orderItem.getItemId());
        assertEquals(order, orderItem.getOrder());
        assertEquals(product, orderItem.getProduct());
        assertEquals(quantity, orderItem.getQuantity());
        assertEquals(price, orderItem.getPrice());
        assertEquals(totalPrice, orderItem.getTotalPrice());
        assertEquals(createdAt, orderItem.getCreatedAt());
    }

    @Test
    @DisplayName("Should set and get itemId")
    void shouldSetAndGetItemId() {
        // Given
        Long itemId = 789L;

        // When
        orderItemData.setItemId(itemId);

        // Then
        assertEquals(itemId, orderItemData.getItemId());
    }

    @Test
    @DisplayName("Should set and get order")
    void shouldSetAndGetOrder() {
        // Given
        orderData.setOrderId(100L);

        // When
        orderItemData.setOrder(orderData);

        // Then
        assertEquals(orderData, orderItemData.getOrder());
        assertEquals(100L, orderItemData.getOrder().getOrderId());
    }

    @Test
    @DisplayName("Should set and get product")
    void shouldSetAndGetProduct() {
        // Given
        String product = "Smartphone";

        // When
        orderItemData.setProduct(product);

        // Then
        assertEquals(product, orderItemData.getProduct());
    }

    @Test
    @DisplayName("Should set and get quantity")
    void shouldSetAndGetQuantity() {
        // Given
        Integer quantity = 5;

        // When
        orderItemData.setQuantity(quantity);

        // Then
        assertEquals(quantity, orderItemData.getQuantity());
    }

    @Test
    @DisplayName("Should set and get price")
    void shouldSetAndGetPrice() {
        // Given
        BigDecimal price = BigDecimal.valueOf(99.99);

        // When
        orderItemData.setPrice(price);

        // Then
        assertEquals(price, orderItemData.getPrice());
        assertEquals(0, price.compareTo(orderItemData.getPrice()));
    }

    @Test
    @DisplayName("Should set and get totalPrice")
    void shouldSetAndGetTotalPrice() {
        // Given
        BigDecimal totalPrice = BigDecimal.valueOf(199.98);

        // When
        orderItemData.setTotalPrice(totalPrice);

        // Then
        assertEquals(totalPrice, orderItemData.getTotalPrice());
        assertEquals(0, totalPrice.compareTo(orderItemData.getTotalPrice()));
    }

    @Test
    @DisplayName("Should set and get createdAt")
    void shouldSetAndGetCreatedAt() {
        // Given
        LocalDateTime createdAt = LocalDateTime.now();

        // When
        orderItemData.setCreatedAt(createdAt);

        // Then
        assertEquals(createdAt, orderItemData.getCreatedAt());
    }

    @Test
    @DisplayName("Should handle null order")
    void shouldHandleNullOrder() {
        // When
        orderItemData.setOrder(null);

        // Then
        assertNull(orderItemData.getOrder());
    }

    @Test
    @DisplayName("Should handle null product")
    void shouldHandleNullProduct() {
        // When
        orderItemData.setProduct(null);

        // Then
        assertNull(orderItemData.getProduct());
    }

    @Test
    @DisplayName("Should handle empty product")
    void shouldHandleEmptyProduct() {
        // When
        orderItemData.setProduct("");

        // Then
        assertEquals("", orderItemData.getProduct());
        assertTrue(orderItemData.getProduct().isEmpty());
    }

    @Test
    @DisplayName("Should handle null quantity")
    void shouldHandleNullQuantity() {
        // When
        orderItemData.setQuantity(null);

        // Then
        assertNull(orderItemData.getQuantity());
    }

    @Test
    @DisplayName("Should handle zero quantity")
    void shouldHandleZeroQuantity() {
        // When
        orderItemData.setQuantity(0);

        // Then
        assertEquals(Integer.valueOf(0), orderItemData.getQuantity());
    }

    @Test
    @DisplayName("Should handle negative quantity")
    void shouldHandleNegativeQuantity() {
        // When
        orderItemData.setQuantity(-1);

        // Then
        assertEquals(Integer.valueOf(-1), orderItemData.getQuantity());
    }

    @Test
    @DisplayName("Should handle null price")
    void shouldHandleNullPrice() {
        // When
        orderItemData.setPrice(null);

        // Then
        assertNull(orderItemData.getPrice());
    }

    @Test
    @DisplayName("Should handle zero price")
    void shouldHandleZeroPrice() {
        // Given
        BigDecimal zeroPrice = BigDecimal.ZERO;

        // When
        orderItemData.setPrice(zeroPrice);

        // Then
        assertEquals(BigDecimal.ZERO, orderItemData.getPrice());
        assertEquals(0, orderItemData.getPrice().compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Should handle negative price")
    void shouldHandleNegativePrice() {
        // Given
        BigDecimal negativePrice = BigDecimal.valueOf(-10.00);

        // When
        orderItemData.setPrice(negativePrice);

        // Then
        assertEquals(negativePrice, orderItemData.getPrice());
        assertTrue(orderItemData.getPrice().compareTo(BigDecimal.ZERO) < 0);
    }

    @Test
    @DisplayName("Should handle null totalPrice")
    void shouldHandleNullTotalPrice() {
        // When
        orderItemData.setTotalPrice(null);

        // Then
        assertNull(orderItemData.getTotalPrice());
    }

    @Test
    @DisplayName("Should handle zero totalPrice")
    void shouldHandleZeroTotalPrice() {
        // Given
        BigDecimal zeroTotalPrice = BigDecimal.ZERO;

        // When
        orderItemData.setTotalPrice(zeroTotalPrice);

        // Then
        assertEquals(BigDecimal.ZERO, orderItemData.getTotalPrice());
        assertEquals(0, orderItemData.getTotalPrice().compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Should implement equals correctly based on itemId")
    void shouldImplementEqualsCorrectlyBasedOnItemId() {
        // Given
        Long itemId = 999L;
        OrderItemData item1 = OrderItemData.builder()
                .itemId(itemId)
                .product("Product A")
                .build();

        OrderItemData item2 = OrderItemData.builder()
                .itemId(itemId)
                .product("Product B")
                .build();

        OrderItemData item3 = OrderItemData.builder()
                .itemId(888L)
                .product("Product A")
                .build();

        // Then
        assertEquals(item1, item2); // Same itemId
        assertNotEquals(item1, item3); // Different itemId
        assertNotNull(item1);
        assertNotEquals("not an order item", item1);
    }

    @Test
    @DisplayName("Should implement hashCode correctly based on itemId")
    void shouldImplementHashCodeCorrectlyBasedOnItemId() {
        // Given
        Long itemId = 777L;
        OrderItemData item1 = OrderItemData.builder()
                .itemId(itemId)
                .build();

        OrderItemData item2 = OrderItemData.builder()
                .itemId(itemId)
                .build();

        // Then
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    @DisplayName("Should implement toString correctly")
    void shouldImplementToStringCorrectly() {
        // Given
        Long itemId = 555L;
        String product = "Test Product";
        Integer quantity = 2;
        BigDecimal price = BigDecimal.valueOf(50.00);

        OrderItemData orderItem = OrderItemData.builder()
                .itemId(itemId)
                .product(product)
                .quantity(quantity)
                .price(price)
                .build();

        // When
        String toString = orderItem.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("OrderItemData"));
        assertTrue(toString.contains("itemId=" + itemId));
        assertTrue(toString.contains("product=" + product));
        assertTrue(toString.contains("quantity=" + quantity));
        assertTrue(toString.contains("price=" + price));
    }

    @Test
    @DisplayName("Should handle null itemId in equals")
    void shouldHandleNullItemIdInEquals() {
        // Given
        OrderItemData item1 = new OrderItemData();
        OrderItemData item2 = new OrderItemData();
        OrderItemData item3 = OrderItemData.builder().itemId(1L).build();

        // Then
        assertEquals(item1, item2); // Both have null itemId
        assertNotEquals(item1, item3); // One null, one not null
        assertNotEquals(item3, item1); // One not null, one null
    }

    @Test
    @DisplayName("Should handle long product names")
    void shouldHandleLongProductNames() {
        // Given
        String longProductName = "This is a very long product name that could potentially cause issues if not handled properly in the database or application";

        // When
        orderItemData.setProduct(longProductName);

        // Then
        assertEquals(longProductName, orderItemData.getProduct());
        assertEquals(longProductName.length(), orderItemData.getProduct().length());
    }

    @Test
    @DisplayName("Should handle large quantity values")
    void shouldHandleLargeQuantityValues() {
        // Given
        Integer largeQuantity = Integer.MAX_VALUE;

        // When
        orderItemData.setQuantity(largeQuantity);

        // Then
        assertEquals(largeQuantity, orderItemData.getQuantity());
    }

    @Test
    @DisplayName("Should handle large price values")
    void shouldHandleLargePriceValues() {
        // Given
        BigDecimal largePrice = new BigDecimal("999999999999999.99");

        // When
        orderItemData.setPrice(largePrice);

        // Then
        assertEquals(largePrice, orderItemData.getPrice());
        assertEquals(0, largePrice.compareTo(orderItemData.getPrice()));
    }

    @Test
    @DisplayName("Should handle large totalPrice values")
    void shouldHandleLargeTotalPriceValues() {
        // Given
        BigDecimal largeTotalPrice = new BigDecimal("999999999999999.99");

        // When
        orderItemData.setTotalPrice(largeTotalPrice);

        // Then
        assertEquals(largeTotalPrice, orderItemData.getTotalPrice());
        assertEquals(0, largeTotalPrice.compareTo(orderItemData.getTotalPrice()));
    }

    @Test
    @DisplayName("Should handle special characters in product name")
    void shouldHandleSpecialCharactersInProductName() {
        // Given
        String productWithSpecialChars = "Product@#$%^&*()_+-=[]{}|;':\",./<>?";

        // When
        orderItemData.setProduct(productWithSpecialChars);

        // Then
        assertEquals(productWithSpecialChars, orderItemData.getProduct());
    }

    @Test
    @DisplayName("Should handle unicode characters in product name")
    void shouldHandleUnicodeCharactersInProductName() {
        // Given
        String unicodeProduct = "Produto Açaí 🍇 café ñoño";

        // When
        orderItemData.setProduct(unicodeProduct);

        // Then
        assertEquals(unicodeProduct, orderItemData.getProduct());
    }
}
