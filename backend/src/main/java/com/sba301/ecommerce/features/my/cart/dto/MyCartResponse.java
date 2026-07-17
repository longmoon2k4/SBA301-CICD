package com.sba301.ecommerce.features.my.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Khớp với cấu trúc cartMock.js: { cartId, userId, channel, items }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyCartResponse {

    private Long cartId;
    private Long userId;
    private String channel;   // luôn là "ONLINE"
    private List<MyCartItemResponse> items;
}
