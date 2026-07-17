package com.sba301.ecommerce.features.my.order.controller;

import com.sba301.ecommerce.features.my.order.dto.CreateMyOrderRequest;
import com.sba301.ecommerce.features.my.order.dto.MyOrderResponse;
import com.sba301.ecommerce.features.my.order.service.MyOrderService;
import com.sba301.ecommerce.security.user.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/my/orders")
@RequiredArgsConstructor
public class MyOrderController {

    private final MyOrderService orderService;

    /**
     * POST /api/v1/my/orders
     * Đặt hàng (checkout). Khớp với api.post('/my/orders', requestBody) trong CheckoutLayout.jsx.
     * Response: { orderCode, status, paymentStatus, ..., paymentUrl }
     */
    @PostMapping
    public ResponseEntity<MyOrderResponse> createOrder(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody CreateMyOrderRequest request,
            HttpServletRequest httpRequest) {
        MyOrderResponse response = orderService.createOrder(
                currentUser.getUser().getId(), request, httpRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/my/orders
     * Lấy danh sách đơn hàng của user hiện tại.
     */
    @GetMapping
    public ResponseEntity<List<MyOrderResponse>> getMyOrders(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<MyOrderResponse> orders = orderService.getMyOrders(currentUser.getUser().getId());
        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/v1/my/orders/{orderCode}
     * Lấy chi tiết 1 đơn hàng theo orderCode.
     * Dùng sau khi thanh toán để navigate('/order/{orderCode}').
     */
    @GetMapping("/{orderCode}")
    public ResponseEntity<MyOrderResponse> getOrderByCode(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable String orderCode) {
        MyOrderResponse order = orderService.getOrderByCode(
                currentUser.getUser().getId(), orderCode);
        return ResponseEntity.ok(order);
    }
}
