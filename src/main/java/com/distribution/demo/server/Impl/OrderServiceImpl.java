package com.distribution.demo.server.Impl;

import com.distribution.demo.server.OrderService;

public class OrderServiceImpl implements OrderService {
    @Override
    public String getOrderNumber(String orderId) {
        return "this order number=" + (orderId + 16);
    }
}
