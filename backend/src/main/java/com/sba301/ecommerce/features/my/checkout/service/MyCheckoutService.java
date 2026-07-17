package com.sba301.ecommerce.features.my.checkout.service;

import com.sba301.ecommerce.features.my.checkout.dto.*;

import java.util.List;

public interface MyCheckoutService {

    List<AddressResponse> getAddresses(Long userId);

    AddressResponse addAddress(Long userId, AddAddressRequest request);

    List<ShippingMethodResponse> getShippingMethods();

    VoucherResponse applyVoucher(String code);
}
