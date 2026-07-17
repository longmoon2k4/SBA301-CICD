package com.sba301.ecommerce.features.my.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private String productName;
    private String variantInfo;   // "M / Trắng"
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
}
