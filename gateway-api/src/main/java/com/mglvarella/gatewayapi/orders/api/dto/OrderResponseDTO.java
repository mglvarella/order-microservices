package com.mglvarella.gatewayapi.orders.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        Long id,
        String customerName,
        String customerEmail,
        LocalDateTime orderDate,
        String status,
        List<OrderItemDTO> items,
        BigDecimal totalAmount) {
}
