package com.btg.challenge.orders.infra.mqprovider.consumer;

import com.btg.challenge.orders.app.service.OrderMessagingService;
import com.btg.challenge.orders.infra.mqprovider.OrderItemMessage;
import com.btg.challenge.orders.infra.mqprovider.OrderMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderConsumer Unit Tests")
class OrderConsumerTest {

    @Mock
    private OrderMessagingService orderMessagingService;

    @InjectMocks
    private OrderConsumer orderConsumer;

    private OrderMessage orderMessage;

    @BeforeEach
    void setUp() {
        OrderItemMessage orderItemMessage = new OrderItemMessage("Product A", 2, BigDecimal.valueOf(15.50));

        orderMessage = new OrderMessage();
        orderMessage.setCodigoPedido(123L);
        orderMessage.setCodigoCliente(456L);
        orderMessage.setItens(List.of(orderItemMessage));
    }

    @Test
    @DisplayName("Should process order message successfully")
    void shouldProcessOrderMessageSuccessfully() {
        // Given
        doNothing().when(orderMessagingService).processOrderMessage(orderMessage);

        // When
        orderConsumer.processOrder(orderMessage);

        // Then
        verify(orderMessagingService).processOrderMessage(orderMessage);
    }

    @Test
    @DisplayName("Should process order message with multiple items")
    void shouldProcessOrderMessageWithMultipleItems() {
        // Given
        OrderItemMessage item1 = new OrderItemMessage("Product A", 2, BigDecimal.valueOf(15.50));
        OrderItemMessage item2 = new OrderItemMessage("Product B", 1, BigDecimal.valueOf(25.00));
        OrderItemMessage item3 = new OrderItemMessage("Product C", 3, BigDecimal.valueOf(10.00));

        OrderMessage multiItemMessage = new OrderMessage();
        multiItemMessage.setCodigoPedido(789L);
        multiItemMessage.setCodigoCliente(101L);
        multiItemMessage.setItens(List.of(item1, item2, item3));

        doNothing().when(orderMessagingService).processOrderMessage(multiItemMessage);

        // When
        orderConsumer.processOrder(multiItemMessage);

        // Then
        verify(orderMessagingService).processOrderMessage(multiItemMessage);
    }

    @Test
    @DisplayName("Should process order message with empty items list")
    void shouldProcessOrderMessageWithEmptyItems() {
        // Given
        OrderMessage emptyItemsMessage = new OrderMessage();
        emptyItemsMessage.setCodigoPedido(999L);
        emptyItemsMessage.setCodigoCliente(888L);
        emptyItemsMessage.setItens(List.of());

        doNothing().when(orderMessagingService).processOrderMessage(emptyItemsMessage);

        // When
        orderConsumer.processOrder(emptyItemsMessage);

        // Then
        verify(orderMessagingService).processOrderMessage(emptyItemsMessage);
    }

    @Test
    @DisplayName("Should process order message with null items")
    void shouldProcessOrderMessageWithNullItems() {
        // Given
        OrderMessage nullItemsMessage = new OrderMessage();
        nullItemsMessage.setCodigoPedido(777L);
        nullItemsMessage.setCodigoCliente(555L);
        nullItemsMessage.setItens(null);

        doNothing().when(orderMessagingService).processOrderMessage(nullItemsMessage);

        // When
        orderConsumer.processOrder(nullItemsMessage);

        // Then
        verify(orderMessagingService).processOrderMessage(nullItemsMessage);
    }

    @Test
    @DisplayName("Should handle service exception gracefully")
    void shouldHandleServiceExceptionGracefully() {
        // Given
        RuntimeException serviceException = new RuntimeException("Service processing failed");
        doThrow(serviceException).when(orderMessagingService).processOrderMessage(orderMessage);

        // When & Then
        try {
            orderConsumer.processOrder(orderMessage);
        } catch (RuntimeException e) {
            // Exception should propagate to RabbitMQ for proper error handling
            verify(orderMessagingService).processOrderMessage(orderMessage);
        }
    }

    @Test
    @DisplayName("Should process order with large amounts")
    void shouldProcessOrderWithLargeAmounts() {
        // Given
        OrderItemMessage expensiveItem = new OrderItemMessage("Expensive Product", 1, BigDecimal.valueOf(9999.99));

        OrderMessage expensiveOrderMessage = new OrderMessage();
        expensiveOrderMessage.setCodigoPedido(111L);
        expensiveOrderMessage.setCodigoCliente(222L);
        expensiveOrderMessage.setItens(List.of(expensiveItem));

        doNothing().when(orderMessagingService).processOrderMessage(expensiveOrderMessage);

        // When
        orderConsumer.processOrder(expensiveOrderMessage);

        // Then
        verify(orderMessagingService).processOrderMessage(expensiveOrderMessage);
    }

    @Test
    @DisplayName("Should process order with zero quantity items")
    void shouldProcessOrderWithZeroQuantityItems() {
        // Given
        OrderItemMessage zeroQuantityItem = new OrderItemMessage("Zero Quantity Product", 0, BigDecimal.valueOf(10.00));

        OrderMessage zeroQuantityMessage = new OrderMessage();
        zeroQuantityMessage.setCodigoPedido(333L);
        zeroQuantityMessage.setCodigoCliente(444L);
        zeroQuantityMessage.setItens(List.of(zeroQuantityItem));

        doNothing().when(orderMessagingService).processOrderMessage(zeroQuantityMessage);

        // When
        orderConsumer.processOrder(zeroQuantityMessage);

        // Then
        verify(orderMessagingService).processOrderMessage(zeroQuantityMessage);
    }

    @Test
    @DisplayName("Should process order with zero price items")
    void shouldProcessOrderWithZeroPriceItems() {
        // Given
        OrderItemMessage freePriceItem = new OrderItemMessage("Free Product", 1, BigDecimal.ZERO);

        OrderMessage freePriceMessage = new OrderMessage();
        freePriceMessage.setCodigoPedido(555L);
        freePriceMessage.setCodigoCliente(666L);
        freePriceMessage.setItens(List.of(freePriceItem));

        doNothing().when(orderMessagingService).processOrderMessage(freePriceMessage);

        // When
        orderConsumer.processOrder(freePriceMessage);

        // Then
        verify(orderMessagingService).processOrderMessage(freePriceMessage);
    }

    @Test
    @DisplayName("Should verify interaction with OrderMessagingService only once per call")
    void shouldVerifyInteractionWithOrderMessagingServiceOnlyOnce() {
        // Given
        doNothing().when(orderMessagingService).processOrderMessage(orderMessage);

        // When
        orderConsumer.processOrder(orderMessage);

        // Then
        verify(orderMessagingService, times(1)).processOrderMessage(orderMessage);
        verifyNoMoreInteractions(orderMessagingService);
    }
}
