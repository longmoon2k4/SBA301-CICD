package com.sba301.ecommerce.features.my.cart.service;

import com.sba301.ecommerce.features.my.cart.dto.*;

import java.util.List;

public interface MyCartService {

    /** Lấy toàn bộ giỏ hàng của user hiện tại (get-or-create) */
    MyCartResponse getMyCart(Long userId);

    /** Thêm sản phẩm vào giỏ; nếu đã có thì tăng số lượng */
    MyCartItemResponse addItem(Long userId, AddCartItemRequest request);

    /** Cập nhật số lượng của 1 item (có kiểm tra stock) */
    MyCartItemResponse updateItemQuantity(Long userId, Long itemId, UpdateCartItemRequest request);

    /** Xóa 1 item khỏi giỏ */
    void removeItem(Long userId, Long itemId);

    /** Xóa tất cả items hết hàng hoặc ngừng kinh doanh */
    void removeUnavailableItems(Long userId);

    /** Xóa danh sách items theo ids (sau khi đặt hàng thành công) */
    void removeItemsByIds(Long userId, List<Long> itemIds);
}
