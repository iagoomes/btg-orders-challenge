package com.btg.challenge.orders.app.service;

import com.btg.challenge.orders.infra.mqprovider.OrderMessage;

public interface OrderMessagingService {
    void processOrderMessage(OrderMessage orderMessage);
}
