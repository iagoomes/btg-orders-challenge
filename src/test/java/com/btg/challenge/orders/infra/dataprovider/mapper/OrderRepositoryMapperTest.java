package com.btg.challenge.orders.infra.dataprovider.mapper;

import com.btg.challenge.orders.domain.entity.Order;
import com.btg.challenge.orders.domain.entity.OrderItem;
import com.btg.challenge.orders.infra.repository.model.CustomerData;
import com.btg.challenge.orders.infra.repository.model.OrderData;
import com.btg.challenge.orders.infra.repository.model.OrderItemData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderRepositoryMapper Unit Tests")
class OrderRepositoryMapperTest {

    @Mock
    private OrderItemRepositoryMapper orderItemMapper;

    @InjectMocks
    private OrderRepositoryMapper orderRepositoryMapper;

    private Order order;
    private CustomerData customerData;
    private OrderData orderData;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.now();

        customerData = CustomerData.builder()
                .customerId(100L)
                .build();

        OrderItem item1 = new OrderItem("Product1", 2, new BigDecimal("25.00"));
        OrderItem item2 = new OrderItem("Product2", 1, new BigDecimal("50.00"));

        order = new Order(1L, 100L, List.of(item1, item2));
        order.setCreatedAt(testDateTime);

        OrderItemData itemData1 = OrderItemData.builder()
                .itemId(1L)
                .product("Product1")
                .quantity(2)
                .price(new BigDecimal("25.00"))
                .totalPrice(new BigDecimal("50.00"))
                .build();

        OrderItemData itemData2 = OrderItemData.builder()
                .itemId(2L)
                .product("Product2")
                .quantity(1)
                .price(new BigDecimal("50.00"))
                .totalPrice(new BigDecimal("50.00"))
                .build();

        orderData = OrderData.builder()
                .orderId(1L)
                .customer(customerData)
                .totalAmount(new BigDecimal("100.00"))
                .itemsCount(2)
                .createdAt(testDateTime)
                .items(List.of(itemData1, itemData2))
                .build();
    }

    @Test
    @DisplayName("Should map Order to OrderData successfully")
    void shouldMapOrderToOrderDataSuccessfully() {
        // Given
        OrderItemData mockItemData1 = OrderItemData.builder().build();
        OrderItemData mockItemData2 = OrderItemData.builder().build();

        when(orderItemMapper.toOrderItemData(eq(order.getItems().get(0)), any(OrderData.class)))
                .thenReturn(mockItemData1);
        when(orderItemMapper.toOrderItemData(eq(order.getItems().get(1)), any(OrderData.class)))
                .thenReturn(mockItemData2);

        // When
        OrderData result = orderRepositoryMapper.toData(order, customerData);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertEquals(customerData, result.getCustomer());
        assertEquals(new BigDecimal("100.00"), result.getTotalAmount());
        assertEquals(2, result.getItemsCount());
        assertNotNull(result.getItems());
        assertEquals(2, result.getItems().size());

        verify(orderItemMapper, times(2)).toOrderItemData(any(OrderItem.class), eq(result));
    }

    @Test
    @DisplayName("Should return null when Order is null in toData")
    void shouldReturnNullWhenOrderIsNullInToData() {
        // Given
        Order nullOrder = null;

        // When
        OrderData result = orderRepositoryMapper.toData(nullOrder, customerData);

        // Then
        assertNull(result);
        verify(orderItemMapper, never()).toOrderItemData(any(), any());
    }

    @Test
    @DisplayName("Should map OrderData to Order successfully")
    void shouldMapOrderDataToOrderSuccessfully() {
        // Given
        OrderItem mockItem1 = new OrderItem("Product1", 2, new BigDecimal("25.00"));
        OrderItem mockItem2 = new OrderItem("Product2", 1, new BigDecimal("50.00"));

        when(orderItemMapper.toOrderItemDomain(orderData.getItems().get(0))).thenReturn(mockItem1);
        when(orderItemMapper.toOrderItemDomain(orderData.getItems().get(1))).thenReturn(mockItem2);

        // When
        Order result = orderRepositoryMapper.toDomain(orderData);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertEquals(100L, result.getCustomerId());
        assertEquals(new BigDecimal("100.00"), result.getTotalAmount());
        assertEquals(2, result.getItemsCount());
        assertEquals(testDateTime, result.getCreatedAt());
        assertNotNull(result.getItems());
        assertEquals(2, result.getItems().size());

        verify(orderItemMapper).toOrderItemDomain(orderData.getItems().get(0));
        verify(orderItemMapper).toOrderItemDomain(orderData.getItems().get(1));
    }

    @Test
    @DisplayName("Should return null when OrderData is null in toDomain")
    void shouldReturnNullWhenOrderDataIsNullInToDomain() {
        // Given
        OrderData nullOrderData = null;

        // When
        Order result = orderRepositoryMapper.toDomain(nullOrderData);

        // Then
        assertNull(result);
        verify(orderItemMapper, never()).toOrderItemDomain(any());
    }

    @Test
    @DisplayName("Should handle Order with empty items list in toData")
    void shouldHandleOrderWithEmptyItemsListInToData() {
        // Given
        Order emptyOrder = new Order(2L, 200L, new ArrayList<>());

        // When
        OrderData result = orderRepositoryMapper.toData(emptyOrder, customerData);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getOrderId());
        assertEquals(customerData, result.getCustomer());
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
        assertEquals(0, result.getItemsCount());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());

        verify(orderItemMapper, never()).toOrderItemData(any(), any());
    }

    @Test
    @DisplayName("Should handle OrderData with empty items list in toDomain")
    void shouldHandleOrderDataWithEmptyItemsListInToDomain() {
        // Given
        OrderData emptyOrderData = OrderData.builder()
                .orderId(3L)
                .customer(customerData)
                .totalAmount(BigDecimal.ZERO)
                .itemsCount(0)
                .createdAt(testDateTime)
                .items(new ArrayList<>())
                .build();

        // When
        Order result = orderRepositoryMapper.toDomain(emptyOrderData);

        // Then
        assertNotNull(result);
        assertEquals(3L, result.getOrderId());
        assertEquals(100L, result.getCustomerId());
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
        assertEquals(0, result.getItemsCount());
        assertEquals(testDateTime, result.getCreatedAt());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());

        verify(orderItemMapper, never()).toOrderItemDomain(any());
    }

    @Test
    @DisplayName("Should handle Order with null customer in toData")
    void shouldHandleOrderWithNullCustomerInToData() {
        // Given
        CustomerData nullCustomer = null;

        // When
        OrderData result = orderRepositoryMapper.toData(order, nullCustomer);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertNull(result.getCustomer());
        assertEquals(new BigDecimal("100.00"), result.getTotalAmount());
        assertEquals(2, result.getItemsCount());
    }

    @Test
    @DisplayName("Should handle OrderData with null customer in toDomain")
    void shouldHandleOrderDataWithNullCustomerInToDomain() {
        // Given
        OrderData orderDataWithNullCustomer = OrderData.builder()
                .orderId(4L)
                .customer(null)
                .totalAmount(new BigDecimal("50.00"))
                .itemsCount(1)
                .createdAt(testDateTime)
                .items(List.of())
                .build();

        // When & Then
        assertThrows(NullPointerException.class, () -> orderRepositoryMapper.toDomain(orderDataWithNullCustomer));
    }

    @Test
    @DisplayName("Should handle single item order mapping")
    void shouldHandleSingleItemOrderMapping() {
        // Given
        OrderItem singleItem = new OrderItem("Single Product", 1, new BigDecimal("99.99"));
        Order singleItemOrder = new Order(5L, 300L, List.of(singleItem));

        OrderItemData mockItemData = OrderItemData.builder().build();
        when(orderItemMapper.toOrderItemData(eq(singleItem), any(OrderData.class)))
                .thenReturn(mockItemData);

        // When
        OrderData result = orderRepositoryMapper.toData(singleItemOrder, customerData);

        // Then
        assertNotNull(result);
        assertEquals(5L, result.getOrderId());
        assertEquals(new BigDecimal("99.99"), result.getTotalAmount());
        assertEquals(1, result.getItemsCount());
        assertEquals(1, result.getItems().size());

        verify(orderItemMapper, times(1)).toOrderItemData(singleItem, result);
    }

    @Test
    @DisplayName("Should preserve item order in mapping")
    void shouldPreserveItemOrderInMapping() {
        // Given
        OrderItem item1 = new OrderItem("First", 1, new BigDecimal("10.00"));
        OrderItem item2 = new OrderItem("Second", 1, new BigDecimal("20.00"));
        OrderItem item3 = new OrderItem("Third", 1, new BigDecimal("30.00"));

        Order orderedItemsOrder = new Order(6L, 400L, List.of(item1, item2, item3));

        OrderItemData itemData1 = OrderItemData.builder().product("First").build();
        OrderItemData itemData2 = OrderItemData.builder().product("Second").build();
        OrderItemData itemData3 = OrderItemData.builder().product("Third").build();

        when(orderItemMapper.toOrderItemData(eq(item1), any(OrderData.class))).thenReturn(itemData1);
        when(orderItemMapper.toOrderItemData(eq(item2), any(OrderData.class))).thenReturn(itemData2);
        when(orderItemMapper.toOrderItemData(eq(item3), any(OrderData.class))).thenReturn(itemData3);

        // When
        OrderData result = orderRepositoryMapper.toData(orderedItemsOrder, customerData);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getItems().size());
        assertEquals("First", result.getItems().get(0).getProduct());
        assertEquals("Second", result.getItems().get(1).getProduct());
        assertEquals("Third", result.getItems().get(2).getProduct());
    }

    @Test
    @DisplayName("Should handle large orders with many items")
    void shouldHandleLargeOrdersWithManyItems() {
        // Given
        List<OrderItem> manyItems = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            manyItems.add(new OrderItem("Product" + i, 1, new BigDecimal("10.00")));
        }

        Order largeOrder = new Order(7L, 500L, manyItems);

        // Mock all item mappings
        for (int i = 0; i < 10; i++) {
            when(orderItemMapper.toOrderItemData(eq(manyItems.get(i)), any(OrderData.class)))
                    .thenReturn(OrderItemData.builder().build());
        }

        // When
        OrderData result = orderRepositoryMapper.toData(largeOrder, customerData);

        // Then
        assertNotNull(result);
        assertEquals(7L, result.getOrderId());
        assertEquals(new BigDecimal("100.00"), result.getTotalAmount());
        assertEquals(10, result.getItemsCount());
        assertEquals(10, result.getItems().size());

        verify(orderItemMapper, times(10)).toOrderItemData(any(OrderItem.class), eq(result));
    }

    @Test
    @DisplayName("Should handle round trip mapping correctly")
    void shouldHandleRoundTripMappingCorrectly() {
        // Given
        Long originalCustomerId = 600L;
        OrderItem originalItem = new OrderItem("Round Trip Product", 2, new BigDecimal("15.50"));
        Order originalOrder = new Order(8L, originalCustomerId, List.of(originalItem));
        originalOrder.setCreatedAt(testDateTime);

        // Create customerData with the same customerId as the originalOrder
        CustomerData originalCustomerData = CustomerData.builder()
                .customerId(originalCustomerId)
                .build();

        OrderItemData mockItemData = OrderItemData.builder()
                .product("Round Trip Product")
                .quantity(2)
                .price(new BigDecimal("15.50"))
                .totalPrice(new BigDecimal("31.00"))
                .build();

        OrderItem mappedItem = new OrderItem("Round Trip Product", 2, new BigDecimal("15.50"));

        when(orderItemMapper.toOrderItemData(eq(originalItem), any(OrderData.class))).thenReturn(mockItemData);
        when(orderItemMapper.toOrderItemDomain(mockItemData)).thenReturn(mappedItem);

        // When - map to data and back to domain
        OrderData orderDataResult = orderRepositoryMapper.toData(originalOrder, originalCustomerData);
        orderDataResult.setCreatedAt(testDateTime); // Simulate database setting timestamp
        Order mappedOrder = orderRepositoryMapper.toDomain(orderDataResult);

        // Then
        assertEquals(originalOrder.getOrderId(), mappedOrder.getOrderId());
        assertEquals(originalOrder.getCustomerId(), mappedOrder.getCustomerId());
        assertEquals(originalOrder.getTotalAmount(), mappedOrder.getTotalAmount());
        assertEquals(originalOrder.getItemsCount(), mappedOrder.getItemsCount());
        assertEquals(testDateTime, mappedOrder.getCreatedAt());
        assertEquals(1, mappedOrder.getItems().size());
    }

    @Test
    @DisplayName("Should handle complex order with decimal calculations")
    void shouldHandleComplexOrderWithDecimalCalculations() {
        // Given
        OrderItem item1 = new OrderItem("Item1", 3, new BigDecimal("33.33"));
        OrderItem item2 = new OrderItem("Item2", 2, new BigDecimal("66.67"));
        Order complexOrder = new Order(9L, 700L, List.of(item1, item2));

        when(orderItemMapper.toOrderItemData(eq(item1), any(OrderData.class)))
                .thenReturn(OrderItemData.builder().build());
        when(orderItemMapper.toOrderItemData(eq(item2), any(OrderData.class)))
                .thenReturn(OrderItemData.builder().build());

        // When
        OrderData result = orderRepositoryMapper.toData(complexOrder, customerData);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("233.33"), result.getTotalAmount());
        assertEquals(2, result.getItemsCount());
    }
}
