package com.sba301.ecommerce.features.my.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response sau khi đặt hàng thành công.
 * FE đọc: response.data.orderCode và response.data.paymentUrl
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyOrderResponse {

    private Long id;
    private String orderCode;
    private String status;
    private String paymentStatus;
    private String channel;

    private BigDecimal subtotal;
    private BigDecimal shippingFee;
    private BigDecimal totalAmount;

    private String note;
    private List<OrderItemResponse> items;

    /**
     * URL redirect đến cổng thanh toán (VNPAY/MOMO).
     * null nếu là COD.
     */
    private String paymentUrl;
}
