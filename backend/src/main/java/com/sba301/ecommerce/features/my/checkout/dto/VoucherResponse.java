package com.sba301.ecommerce.features.my.checkout.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Khớp với voucherCatalog của frontend: { code, type, amount, maxDiscount, description }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherResponse {
    private String code;
    private String type;
    private BigDecimal amount;
    private BigDecimal maxDiscount;
    private String description;
}
