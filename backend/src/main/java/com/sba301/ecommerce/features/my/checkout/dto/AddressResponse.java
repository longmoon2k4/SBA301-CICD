package com.sba301.ecommerce.features.my.checkout.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Khớp với cấu trúc addressesMock.js: { id, recipientName, phone, province, district, ward, street, isDefault }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private Long id;
    private String recipientName;
    private String phone;
    private String province;
    private String district;
    private String ward;
    private String street;
    private Boolean isDefault;
}
