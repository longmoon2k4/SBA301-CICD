package com.sba301.ecommerce.features.my.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Khớp chính xác với cấu trúc mà frontend cartMock.js và useCartItems.js dùng.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyCartItemResponse {

    private Long id;           // cartItemId — FE dùng để thao tác (update/delete)
    private Long cartItemId;   // alias cho id

    private Long variantId;
    private Long productId;
    private String productName;
    private String sku;
    private String size;
    private String color;

    private BigDecimal unitPrice;
    private Integer quantity;
    private Integer stockQuantity;
    private Boolean isActive;
    private String thumbnail;  // URL ảnh đại diện (ảnh primary của product)
}
