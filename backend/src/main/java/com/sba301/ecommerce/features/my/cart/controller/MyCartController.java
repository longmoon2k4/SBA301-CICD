package com.sba301.ecommerce.features.my.cart.controller;

import com.sba301.ecommerce.features.my.cart.dto.*;
import com.sba301.ecommerce.features.my.cart.service.MyCartService;
import com.sba301.ecommerce.features.my.common.ApiResponse;
import com.sba301.ecommerce.security.user.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/my/cart")
@RequiredArgsConstructor
public class MyCartController {

    private final MyCartService cartService;

    /**
     * GET /api/v1/my/cart
     * Trả về toàn bộ giỏ hàng của user hiện tại.
     * Nếu chưa có giỏ thì tự tạo mới (rỗng).
     */
    @GetMapping
    public ResponseEntity<MyCartResponse> getMyCart(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        MyCartResponse cart = cartService.getMyCart(currentUser.getUser().getId());
        return ResponseEntity.ok(cart);
    }

    /**
     * POST /api/v1/my/cart/items
     * Thêm sản phẩm (theo variantId) vào giỏ hàng.
     * Nếu đã có thì tăng số lượng.
     */
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<MyCartItemResponse>> addItem(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody AddCartItemRequest request) {
        MyCartItemResponse item = cartService.addItem(currentUser.getUser().getId(), request);
        return ResponseEntity.ok(ApiResponse.success(item));
    }

    /**
     * PUT /api/v1/my/cart/items/{id}
     * Cập nhật số lượng sản phẩm trong giỏ.
     */
    @PutMapping("/items/{id}")
    public ResponseEntity<ApiResponse<MyCartItemResponse>> updateItem(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long id,
            @Valid @RequestBody UpdateCartItemRequest request) {
        MyCartItemResponse item = cartService.updateItemQuantity(currentUser.getUser().getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success(item));
    }

    /**
     * DELETE /api/v1/my/cart/items/{id}
     * Xóa 1 sản phẩm khỏi giỏ hàng.
     */
    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> removeItem(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long id) {
        cartService.removeItem(currentUser.getUser().getId(), id);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/v1/my/cart/items/unavailable
     * Xóa tất cả sản phẩm hết hàng hoặc ngừng kinh doanh.
     */
    @DeleteMapping("/items/unavailable")
    public ResponseEntity<ApiResponse<Void>> removeUnavailableItems(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        cartService.removeUnavailableItems(currentUser.getUser().getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * DELETE /api/v1/my/cart/items/batch
     * Xóa danh sách items theo ids (gọi sau khi đặt hàng thành công).
     * Body: { "ids": [1001, 1002] }
     */
    @DeleteMapping("/items/batch")
    public ResponseEntity<ApiResponse<Void>> removeItemsByIds(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestBody BatchDeleteCartItemsRequest request) {
        cartService.removeItemsByIds(currentUser.getUser().getId(), request.getIds());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
