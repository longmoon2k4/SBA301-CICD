package com.sba301.ecommerce.features.my.cart.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BatchDeleteCartItemsRequest {

    private List<Long> ids;
}
