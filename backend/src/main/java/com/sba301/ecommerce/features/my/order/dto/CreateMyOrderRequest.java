package com.sba301.ecommerce.features.my.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Request body khi FE gọi POST /my/orders
 * Khớp với requestBody trong CheckoutLayout.jsx:
 * {
 *   items: [{ variantId, quantity }],
 *   shippingAddressId,
 *   shippingMethodId,
 *   paymentMethod,
 *   note,
 *   voucherCode
 * }
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateMyOrderRequest {

    @NotEmpty(message = "Phải có ít nhất 1 sản phẩm")
    @Valid
    private List<OrderItemRequest> items;

    private Long shippingAddressId;

    private Long shippingMethodId;

    private String paymentMethod;  // COD, VNPAY

    private String note;

    private String voucherCode;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class OrderItemRequest {

        @NotNull(message = "variantId không được để trống")
        private Long variantId;

        @NotNull(message = "quantity không được để trống")
        @Min(value = 1, message = "quantity phải >= 1")
        private Integer quantity;
    }
}
