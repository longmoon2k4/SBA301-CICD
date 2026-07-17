package com.sba301.ecommerce.features.my.checkout.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Khớp với shippingMethodsMock.js: { id, name, eta, fee, description }
 * Lưu ý: id là Long thay vì String để phù hợp với DB
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingMethodResponse {
    private Long id;
    private String name;
    private String eta;
    private BigDecimal fee;
    private String description;
}
