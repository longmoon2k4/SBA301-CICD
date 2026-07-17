package com.sba301.ecommerce.features.my.order.service;

import com.sba301.ecommerce.features.my.order.dto.CreateMyOrderRequest;
import com.sba301.ecommerce.features.my.order.dto.MyOrderResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface MyOrderService {

    MyOrderResponse createOrder(Long userId, CreateMyOrderRequest request, HttpServletRequest httpRequest);

    List<MyOrderResponse> getMyOrders(Long userId);

    MyOrderResponse getOrderByCode(Long userId, String orderCode);
}
