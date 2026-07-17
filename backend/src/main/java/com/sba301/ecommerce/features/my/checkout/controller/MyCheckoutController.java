package com.sba301.ecommerce.features.my.checkout.controller;

import com.sba301.ecommerce.features.my.checkout.dto.*;
import com.sba301.ecommerce.features.my.checkout.service.MyCheckoutService;
import com.sba301.ecommerce.features.my.common.ApiResponse;
import com.sba301.ecommerce.security.user.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/my/checkout")
@RequiredArgsConstructor
public class MyCheckoutController {

    private final MyCheckoutService checkoutService;

    /**
     * GET /api/v1/my/checkout/addresses
     * Trả về danh sách địa chỉ giao hàng của user.
     * Khớp với getAddressesAPI() trong checkoutService.js
     */
    @GetMapping("/addresses")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<AddressResponse> addresses = checkoutService.getAddresses(currentUser.getUser().getId());
        return ResponseEntity.ok(ApiResponse.success(addresses));
    }

    /**
     * POST /api/v1/my/checkout/addresses
     * Thêm địa chỉ giao hàng mới.
     * Khớp với addAddressAPI() trong checkoutService.js
     */
    @PostMapping("/addresses")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody AddAddressRequest request) {
        AddressResponse address = checkoutService.addAddress(currentUser.getUser().getId(), request);
        return ResponseEntity.ok(ApiResponse.success(address));
    }

    /**
     * GET /api/v1/my/checkout/shipping-methods
     * Trả về danh sách phương thức vận chuyển.
     * Khớp với getShippingMethodsAPI() trong checkoutService.js
     */
    @GetMapping("/shipping-methods")
    public ResponseEntity<ApiResponse<List<ShippingMethodResponse>>> getShippingMethods() {
        List<ShippingMethodResponse> methods = checkoutService.getShippingMethods();
        return ResponseEntity.ok(ApiResponse.success(methods));
    }

    /**
     * POST /api/v1/my/checkout/vouchers/apply
     * Kiểm tra và áp dụng mã voucher.
     * Khớp với applyVoucherAPI() trong checkoutService.js
     */
    @PostMapping("/vouchers/apply")
    public ResponseEntity<ApiResponse<VoucherResponse>> applyVoucher(
            @Valid @RequestBody VoucherApplyRequest request) {
        VoucherResponse voucher = checkoutService.applyVoucher(request.getCode());
        return ResponseEntity.ok(ApiResponse.success(voucher));
    }
}
