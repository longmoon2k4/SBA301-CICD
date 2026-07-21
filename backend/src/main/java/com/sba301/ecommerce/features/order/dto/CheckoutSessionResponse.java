package com.sba301.ecommerce.features.order.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class CheckoutSessionResponse {
    private String sessionId;
    private OffsetDateTime expiresAt;
    private List<CheckoutSessionItemResponse> items;
}
