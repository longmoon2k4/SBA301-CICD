package com.sba301.ecommerce.features.my.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddCartItemRequest {

    @NotNull(message = "variantId không được để trống")
    private Long variantId;

    @NotNull(message = "quantity không được để trống")
    @Min(value = 1, message = "quantity phải >= 1")
    private Integer quantity;
}
