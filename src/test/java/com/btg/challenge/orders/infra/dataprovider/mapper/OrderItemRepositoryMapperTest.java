package com.btg.challenge.orders.infra.dataprovider.mapper;

import com.btg.challenge.orders.domain.entity.OrderItem;
import com.btg.challenge.orders.infra.repository.model.OrderData;
import com.btg.challenge.orders.infra.repository.model.OrderItemData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderItemRepositoryMapper Unit Tests")
class OrderItemRepositoryMapperTest {

    private OrderItemRepositoryMapper mapper;
    private OrderData orderData;

    @BeforeEach
    void setUp() {
        mapper = new OrderItemRepositoryMapper();
        orderData = OrderData.builder()
                .orderId(1L)
                .build();
    }

    @Test
    @DisplayName("Should map OrderItem to OrderItemData successfully")
    void shouldMapOrderItemToOrderItemDataSuccessfully() {
        // Given
        OrderItem orderItem = new OrderItem("Notebook", 2, new BigDecimal("1500.00"));

        // When
        OrderItemData result = mapper.toOrderItemData(orderItem, orderData);

        // Then
        assertNotNull(result);
        assertEquals(orderData, result.getOrder());
        assertEquals("Notebook", result.getProduct());
        assertEquals(2, result.getQuantity());
        assertEquals(new BigDecimal("1500.00"), result.getPrice());
        assertEquals(new BigDecimal("3000.00"), result.getTotalPrice());
    }

    @Test
    @DisplayName("Should map OrderItemData to OrderItem successfully")
    void shouldMapOrderItemDataToOrderItemSuccessfully() {
        // Given
        OrderItemData orderItemData = OrderItemData.builder()
                .itemId(123L)
                .order(orderData)
                .product("Mouse")
                .quantity(3)
                .price(new BigDecimal("25.50"))
                .totalPrice(new BigDecimal("76.50"))
                .build();

        // When
        OrderItem result = mapper.toOrderItemDomain(orderItemData);

        // Then
        assertNotNull(result);
        assertEquals(123L, result.getItemId());
        assertEquals("Mouse", result.getProduct());
        assertEquals(3, result.getQuantity());
        assertEquals(new BigDecimal("25.50"), result.getPrice());
        assertEquals(new BigDecimal("76.50"), result.getTotalPrice());
    }

    @Test
    @DisplayName("Should handle null OrderItem in toOrderItemData")
    void shouldHandleNullOrderItemInToOrderItemData() {
        // Given
        OrderItem orderItem = null;

        // When & Then
        assertThrows(NullPointerException.class, () -> mapper.toOrderItemData(orderItem, orderData));
    }

    @Test
    @DisplayName("Should handle null OrderItemData in toOrderItemDomain")
    void shouldHandleNullOrderItemDataInToOrderItemDomain() {
        // Given
        OrderItemData orderItemData = null;

        // When & Then
        assertThrows(NullPointerException.class, () -> mapper.toOrderItemDomain(orderItemData));
    }

    @Test
    @DisplayName("Should handle null OrderData parameter")
    void shouldHandleNullOrderDataParameter() {
        // Given
        OrderItem orderItem = new OrderItem("Product", 1, new BigDecimal("10.00"));
        OrderData nullOrderData = null;

        // When
        OrderItemData result = mapper.toOrderItemData(orderItem, nullOrderData);

        // Then
        assertNotNull(result);
        assertNull(result.getOrder());
        assertEquals("Product", result.getProduct());
        assertEquals(1, result.getQuantity());
        assertEquals(new BigDecimal("10.00"), result.getPrice());
        assertEquals(new BigDecimal("10.00"), result.getTotalPrice());
    }

    @Test
    @DisplayName("Should map OrderItem with zero price correctly")
    void shouldMapOrderItemWithZeroPriceCorrectly() {
        // Given
        OrderItem orderItem = new OrderItem("Free Item", 5, BigDecimal.ZERO);

        // When
        OrderItemData result = mapper.toOrderItemData(orderItem, orderData);

        // Then
        assertNotNull(result);
        assertEquals("Free Item", result.getProduct());
        assertEquals(5, result.getQuantity());
        assertEquals(BigDecimal.ZERO, result.getPrice());
        assertEquals(BigDecimal.ZERO, result.getTotalPrice());
    }

    @Test
    @DisplayName("Should map OrderItemData with null fields correctly")
    void shouldMapOrderItemDataWithNullFieldsCorrectly() {
        // Given
        OrderItemData orderItemData = OrderItemData.builder()
                .itemId(null)
                .order(orderData)
                .product(null)
                .quantity(null)
                .price(null)
                .totalPrice(null)
                .build();

        // When
        OrderItem result = mapper.toOrderItemDomain(orderItemData);

        // Then
        assertNotNull(result);
        assertNull(result.getItemId());
        assertNull(result.getProduct());
        assertNull(result.getQuantity());
        assertNull(result.getPrice());
        // totalPrice will be ZERO when quantity and price are null due to updateTotalPrice() logic
        assertEquals(BigDecimal.ZERO, result.getTotalPrice());
    }

    @Test
    @DisplayName("Should handle large quantities and prices correctly")
    void shouldHandleLargeQuantitiesAndPricesCorrectly() {
        // Given
        OrderItem orderItem = new OrderItem("Expensive Item", 1000, new BigDecimal("999999.99"));

        // When
        OrderItemData result = mapper.toOrderItemData(orderItem, orderData);

        // Then
        assertNotNull(result);
        assertEquals("Expensive Item", result.getProduct());
        assertEquals(1000, result.getQuantity());
        assertEquals(new BigDecimal("999999.99"), result.getPrice());
        assertEquals(new BigDecimal("999999990.00"), result.getTotalPrice());
    }

    @Test
    @DisplayName("Should preserve decimal precision in price mapping")
    void shouldPreserveDecimalPrecisionInPriceMapping() {
        // Given
        OrderItem orderItem = new OrderItem("Precision Item", 3, new BigDecimal("33.333"));

        // When
        OrderItemData result = mapper.toOrderItemData(orderItem, orderData);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("33.333"), result.getPrice());
        assertEquals(new BigDecimal("99.999"), result.getTotalPrice());
    }

    @Test
    @DisplayName("Should handle round trip mapping correctly")
    void shouldHandleRoundTripMappingCorrectly() {
        // Given
        OrderItem originalItem = new OrderItem("Round Trip Product", 4, new BigDecimal("12.75"));
        originalItem.setItemId(456L);

        // When - map to data and back to domain
        OrderItemData itemData = mapper.toOrderItemData(originalItem, orderData);
        itemData.setItemId(456L); // Simulate database ID assignment
        OrderItem mappedItem = mapper.toOrderItemDomain(itemData);

        // Then
        assertEquals(originalItem.getItemId(), mappedItem.getItemId());
        assertEquals(originalItem.getProduct(), mappedItem.getProduct());
        assertEquals(originalItem.getQuantity(), mappedItem.getQuantity());
        assertEquals(originalItem.getPrice(), mappedItem.getPrice());
        assertEquals(originalItem.getTotalPrice(), mappedItem.getTotalPrice());
    }
}
