package com.sba301.ecommerce.features.my.checkout.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VoucherApplyRequest {

    @NotBlank(message = "Mã voucher không được để trống")
    private String code;
}
