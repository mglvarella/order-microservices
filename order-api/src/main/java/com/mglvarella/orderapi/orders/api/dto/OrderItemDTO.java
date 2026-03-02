package com.mglvarella.orderapi.orders.api.dto;

import java.math.BigDecimal;

public record OrderItemDTO(
        String productName,
        Integer quantity,
        BigDecimal unitPrice
) {
}
