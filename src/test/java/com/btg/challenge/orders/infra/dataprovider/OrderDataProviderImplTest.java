package com.btg.challenge.orders.infra.dataprovider;

import com.btg.challenge.orders.domain.entity.Customer;
import com.btg.challenge.orders.domain.entity.Order;
import com.btg.challenge.orders.domain.entity.OrderItem;
import com.btg.challenge.orders.infra.dataprovider.mapper.CustomerRepositoryMapper;
import com.btg.challenge.orders.infra.dataprovider.mapper.OrderRepositoryMapper;
import com.btg.challenge.orders.infra.repository.OrderDataRepository;
import com.btg.challenge.orders.infra.repository.model.CustomerData;
import com.btg.challenge.orders.infra.repository.model.OrderData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderDataProviderImpl Unit Tests")
class OrderDataProviderImplTest {

    @Mock
    private OrderDataRepository orderRepository;

    @Mock
    private OrderRepositoryMapper orderMapper;

    @Mock
    private CustomerRepositoryMapper customerMapper;

    @InjectMocks
    private OrderDataProviderImpl orderDataProvider;

    private Order order;
    private Customer customer;
    private OrderData orderData;
    private CustomerData customerData;

    @BeforeEach
    void setUp() {
        customer = new Customer(1L);
        customer.setCreatedAt(LocalDateTime.now());

        customerData = CustomerData.builder()
                .customerId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        OrderItem orderItem = new OrderItem("Product 1", 2, BigDecimal.valueOf(10.00));

        order = new Order(1L, 1L, List.of(orderItem));
        order.setCreatedAt(LocalDateTime.now());

        orderData = OrderData.builder()
                .orderId(1L)
                .customer(customerData)
                .totalAmount(BigDecimal.valueOf(20.00))
                .itemsCount(1)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should save order successfully")
    void shouldSaveOrderSuccessfully() {
        // Given
        when(customerMapper.toData(customer)).thenReturn(customerData);
        when(orderMapper.toData(order, customerData)).thenReturn(orderData);
        when(orderRepository.save(orderData)).thenReturn(orderData);
        when(orderMapper.toDomain(orderData)).thenReturn(order);

        // When
        Order result = orderDataProvider.save(order, customer);

        // Then
        assertNotNull(result);
        assertEquals(order.getOrderId(), result.getOrderId());
        assertEquals(order.getCustomerId(), result.getCustomerId());

        verify(customerMapper).toData(customer);
        verify(orderMapper).toData(order, customerData);
        verify(orderRepository).save(orderData);
        verify(orderMapper).toDomain(orderData);
    }

    @Test
    @DisplayName("Should find order by id successfully")
    void shouldFindOrderByIdSuccessfully() {
        // Given
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderData));
        when(orderMapper.toDomain(orderData)).thenReturn(order);

        // When
        Optional<Order> result = orderDataProvider.findById(orderId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(order.getOrderId(), result.get().getOrderId());

        verify(orderRepository).findById(orderId);
        verify(orderMapper).toDomain(orderData);
    }

    @Test
    @DisplayName("Should return empty when order not found by id")
    void shouldReturnEmptyWhenOrderNotFoundById() {
        // Given
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When
        Optional<Order> result = orderDataProvider.findById(orderId);

        // Then
        assertTrue(result.isEmpty());

        verify(orderRepository).findById(orderId);
        verify(orderMapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should find order by id with items successfully")
    void shouldFindOrderByIdWithItemsSuccessfully() {
        // Given
        Long orderId = 1L;
        when(orderRepository.findByIdWithItems(orderId)).thenReturn(Optional.of(orderData));
        when(orderMapper.toDomain(orderData)).thenReturn(order);

        // When
        Optional<Order> result = orderDataProvider.findByIdWithItems(orderId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(order.getOrderId(), result.get().getOrderId());

        verify(orderRepository).findByIdWithItems(orderId);
        verify(orderMapper).toDomain(orderData);
    }

    @Test
    @DisplayName("Should return empty when order not found by id with items")
    void shouldReturnEmptyWhenOrderNotFoundByIdWithItems() {
        // Given
        Long orderId = 999L;
        when(orderRepository.findByIdWithItems(orderId)).thenReturn(Optional.empty());

        // When
        Optional<Order> result = orderDataProvider.findByIdWithItems(orderId);

        // Then
        assertTrue(result.isEmpty());

        verify(orderRepository).findByIdWithItems(orderId);
        verify(orderMapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should find orders by customer id successfully")
    void shouldFindOrdersByCustomerIdSuccessfully() {
        // Given
        Long customerId = 1L;
        Pageable pageable = Pageable.ofSize(10);
        Page<OrderData> orderDataPage = new PageImpl<>(List.of(orderData));

        when(orderRepository.findByCustomerId(customerId, pageable)).thenReturn(orderDataPage);
        when(orderMapper.toDomain(orderData)).thenReturn(order);

        // When
        Page<Order> result = orderDataProvider.findByCustomerId(customerId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(order.getOrderId(), result.getContent().getFirst().getOrderId());

        verify(orderRepository).findByCustomerId(customerId, pageable);
        verify(orderMapper).toDomain(orderData);
    }

    @Test
    @DisplayName("Should count orders by customer id successfully")
    void shouldCountOrdersByCustomerIdSuccessfully() {
        // Given
        Long customerId = 1L;
        long expectedCount = 5L;
        when(orderRepository.countByCustomerId(customerId)).thenReturn(expectedCount);

        // When
        long result = orderDataProvider.countByCustomerId(customerId);

        // Then
        assertEquals(expectedCount, result);

        verify(orderRepository).countByCustomerId(customerId);
    }

    @Test
    @DisplayName("Should return true when order exists by id")
    void shouldReturnTrueWhenOrderExistsById() {
        // Given
        Long orderId = 1L;
        when(orderRepository.existsById(orderId)).thenReturn(true);

        // When
        boolean result = orderDataProvider.existsById(orderId);

        // Then
        assertTrue(result);

        verify(orderRepository).existsById(orderId);
    }

    @Test
    @DisplayName("Should return false when order does not exist by id")
    void shouldReturnFalseWhenOrderDoesNotExistById() {
        // Given
        Long orderId = 999L;
        when(orderRepository.existsById(orderId)).thenReturn(false);

        // When
        boolean result = orderDataProvider.existsById(orderId);

        // Then
        assertFalse(result);

        verify(orderRepository).existsById(orderId);
    }
}
