package com.mglvarella.orderapi.orders.api.dto;

import com.mglvarella.orderapi.orders.domain.model.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        Long id,
        String customerName,
        String customerEmail,
        LocalDateTime orderDate,
        OrderStatus status,
        List<OrderItemDTO> items,
        BigDecimal totalAmount) {
}
