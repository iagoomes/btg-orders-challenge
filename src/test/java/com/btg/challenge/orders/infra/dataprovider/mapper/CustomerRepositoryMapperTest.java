package com.btg.challenge.orders.infra.dataprovider.mapper;

import com.btg.challenge.orders.domain.entity.Customer;
import com.btg.challenge.orders.domain.entity.Order;
import com.btg.challenge.orders.domain.entity.OrderItem;
import com.btg.challenge.orders.infra.repository.model.CustomerData;
import com.btg.challenge.orders.infra.repository.model.OrderData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerRepositoryMapper Unit Tests")
class CustomerRepositoryMapperTest {

    @Mock
    private OrderRepositoryMapper orderRepositoryMapper;

    @InjectMocks
    private CustomerRepositoryMapper customerRepositoryMapper;

    private Customer customer;
    private CustomerData customerData;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.now();
        customer = new Customer(100L);
        customer.setCreatedAt(testDateTime);

        customerData = CustomerData.builder()
                .customerId(100L)
                .createdAt(testDateTime)
                .build();
    }

    @Test
    @DisplayName("Should map Customer to CustomerData successfully")
    void shouldMapCustomerToCustomerDataSuccessfully() {
        // When
        CustomerData result = customerRepositoryMapper.toData(customer);

        // Then
        assertNotNull(result);
        assertEquals(100L, result.getCustomerId());
    }

    @Test
    @DisplayName("Should return null when Customer is null in toData")
    void shouldReturnNullWhenCustomerIsNullInToData() {
        // Given
        Customer nullCustomer = null;

        // When
        CustomerData result = customerRepositoryMapper.toData(nullCustomer);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should map CustomerData to Customer successfully without orders")
    void shouldMapCustomerDataToCustomerSuccessfullyWithoutOrders() {
        // When
        Customer result = customerRepositoryMapper.toDomain(customerData);

        // Then
        assertNotNull(result);
        assertEquals(100L, result.getCustomerId());
        assertEquals(testDateTime, result.getCreatedAt());
        assertNull(result.getOrders());
    }

    @Test
    @DisplayName("Should map CustomerData to Customer successfully with orders")
    void shouldMapCustomerDataToCustomerSuccessfullyWithOrders() {
        // Given
        OrderData orderData1 = OrderData.builder()
                .orderId(1L)
                .customer(customerData)
                .build();
        OrderData orderData2 = OrderData.builder()
                .orderId(2L)
                .customer(customerData)
                .build();

        customerData.setOrders(List.of(orderData1, orderData2));

        Order order1 = new Order(1L, 100L, List.of());
        Order order2 = new Order(2L, 100L, List.of());

        when(orderRepositoryMapper.toDomain(orderData1)).thenReturn(order1);
        when(orderRepositoryMapper.toDomain(orderData2)).thenReturn(order2);

        // When
        Customer result = customerRepositoryMapper.toDomain(customerData);

        // Then
        assertNotNull(result);
        assertEquals(100L, result.getCustomerId());
        assertEquals(testDateTime, result.getCreatedAt());
        assertNotNull(result.getOrders());
        assertEquals(2, result.getOrders().size());
        assertTrue(result.getOrders().contains(order1));
        assertTrue(result.getOrders().contains(order2));
        verify(orderRepositoryMapper).toDomain(orderData1);
        verify(orderRepositoryMapper).toDomain(orderData2);
    }

    @Test
    @DisplayName("Should return null when CustomerData is null in toDomain")
    void shouldReturnNullWhenCustomerDataIsNullInToDomain() {
        // Given
        CustomerData nullCustomerData = null;

        // When
        Customer result = customerRepositoryMapper.toDomain(nullCustomerData);

        // Then
        assertNull(result);
        verify(orderRepositoryMapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should handle CustomerData with empty orders list")
    void shouldHandleCustomerDataWithEmptyOrdersList() {
        // Given
        customerData.setOrders(List.of());

        // When
        Customer result = customerRepositoryMapper.toDomain(customerData);

        // Then
        assertNotNull(result);
        assertEquals(100L, result.getCustomerId());
        assertEquals(testDateTime, result.getCreatedAt());
        assertNotNull(result.getOrders());
        assertTrue(result.getOrders().isEmpty());
        verify(orderRepositoryMapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should handle Customer with null customerId in toData")
    void shouldHandleCustomerWithNullCustomerIdInToData() {
        // Given
        Customer customerWithNullId = new Customer();
        customerWithNullId.setCustomerId(null);

        // When
        CustomerData result = customerRepositoryMapper.toData(customerWithNullId);

        // Then
        assertNotNull(result);
        assertNull(result.getCustomerId());
    }

    @Test
    @DisplayName("Should handle CustomerData with null fields")
    void shouldHandleCustomerDataWithNullFields() {
        // Given
        CustomerData customerDataWithNulls = CustomerData.builder()
                .customerId(null)
                .createdAt(null)
                .orders(null)
                .build();

        // When
        Customer result = customerRepositoryMapper.toDomain(customerDataWithNulls);

        // Then
        assertNotNull(result);
        assertNull(result.getCustomerId());
        assertNull(result.getCreatedAt());
        assertNull(result.getOrders());
    }

    @Test
    @DisplayName("Should handle mapping with single order")
    void shouldHandleMappingWithSingleOrder() {
        // Given
        OrderData singleOrderData = OrderData.builder()
                .orderId(999L)
                .customer(customerData)
                .build();

        customerData.setOrders(List.of(singleOrderData));

        Order singleOrder = new Order(999L, 100L, List.of());
        when(orderRepositoryMapper.toDomain(singleOrderData)).thenReturn(singleOrder);

        // When
        Customer result = customerRepositoryMapper.toDomain(customerData);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getOrders().size());
        assertEquals(singleOrder, result.getOrders().getFirst());
        verify(orderRepositoryMapper, times(1)).toDomain(singleOrderData);
    }

    @Test
    @DisplayName("Should preserve Customer properties without orders in toData")
    void shouldPreserveCustomerPropertiesWithoutOrdersInToData() {
        // Given
        Customer customerWithoutOrders = new Customer(555L);
        customerWithoutOrders.setCreatedAt(testDateTime);

        // When
        CustomerData result = customerRepositoryMapper.toData(customerWithoutOrders);

        // Then
        assertNotNull(result);
        assertEquals(555L, result.getCustomerId());
        // Note: toData doesn't map createdAt and orders (they come from database)
    }

    @Test
    @DisplayName("Should handle round trip mapping for customer without orders")
    void shouldHandleRoundTripMappingForCustomerWithoutOrders() {
        // Given
        Customer originalCustomer = new Customer(777L);

        // When - map to data and back to domain
        CustomerData mappedData = customerRepositoryMapper.toData(originalCustomer);
        mappedData.setCreatedAt(testDateTime); // Simulate database setting createdAt
        Customer mappedCustomer = customerRepositoryMapper.toDomain(mappedData);

        // Then
        assertEquals(originalCustomer.getCustomerId(), mappedCustomer.getCustomerId());
        assertEquals(testDateTime, mappedCustomer.getCreatedAt());
        assertNull(mappedCustomer.getOrders());
    }

    @Test
    @DisplayName("Should handle multiple orders with complex data")
    void shouldHandleMultipleOrdersWithComplexData() {
        // Given
        OrderData orderData1 = OrderData.builder()
                .orderId(1L)
                .customer(customerData)
                .totalAmount(new BigDecimal("100.00"))
                .itemsCount(2)
                .build();

        OrderData orderData2 = OrderData.builder()
                .orderId(2L)
                .customer(customerData)
                .totalAmount(new BigDecimal("250.00"))
                .itemsCount(1)
                .build();

        customerData.setOrders(List.of(orderData1, orderData2));

        OrderItem item1 = new OrderItem("Product1", 2, new BigDecimal("50.00"));
        OrderItem item2 = new OrderItem("Product2", 1, new BigDecimal("250.00"));

        Order order1 = new Order(1L, 100L, List.of(item1));
        Order order2 = new Order(2L, 100L, List.of(item2));

        when(orderRepositoryMapper.toDomain(orderData1)).thenReturn(order1);
        when(orderRepositoryMapper.toDomain(orderData2)).thenReturn(order2);

        // When
        Customer result = customerRepositoryMapper.toDomain(customerData);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getOrders().size());
        assertEquals(new BigDecimal("100.00"), result.getOrders().get(0).getTotalAmount());
        assertEquals(new BigDecimal("250.00"), result.getOrders().get(1).getTotalAmount());
        verify(orderRepositoryMapper).toDomain(orderData1);
        verify(orderRepositoryMapper).toDomain(orderData2);
    }
}
