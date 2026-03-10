package com.mglvarella.gatewayapi.orders.api.dto;

import java.util.List;

public record OrderRequestDTO(
        String customerName,
        String customerEmail,
        List<OrderItemDTO> items) {
}
