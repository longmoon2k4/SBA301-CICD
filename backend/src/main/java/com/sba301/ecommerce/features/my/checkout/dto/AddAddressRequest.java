package com.sba301.ecommerce.features.my.checkout.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddAddressRequest {

    @NotBlank(message = "Tên người nhận không được để trống")
    @Size(max = 150)
    private String recipientName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(max = 20)
    private String phone;

    @NotBlank(message = "Tỉnh/thành không được để trống")
    @Size(max = 100)
    private String province;

    @NotBlank(message = "Quận/huyện không được để trống")
    @Size(max = 100)
    private String district;

    @Size(max = 100)
    private String ward;

    @NotBlank(message = "Địa chỉ đường không được để trống")
    @Size(max = 255)
    private String street;

    private Boolean isDefault = false;
}
