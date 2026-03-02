package com.mglvarella.orderapi.orders.api.mapper;

import com.mglvarella.orderapi.orders.api.dto.OrderItemDTO;
import com.mglvarella.orderapi.orders.api.dto.OrderRequestDTO;
import com.mglvarella.orderapi.orders.api.dto.OrderResponseDTO;
import com.mglvarella.orderapi.orders.domain.model.Order;
import com.mglvarella.orderapi.orders.domain.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public Order toEntity(OrderRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        List<OrderItem> items = dto.items() != null ? dto.items().stream()
                .map(this::toItemEntity)
                .collect(Collectors.toList()) : null;

        return new Order(
                dto.customerName(),
                dto.customerEmail(),
                items);
    }

    public OrderResponseDTO toResponse(Order entity) {
        if (entity == null) {
            return null;
        }

        List<OrderItemDTO> itemDTOs = entity.getItems() != null ? entity.getItems().stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList()) : null;

        return new OrderResponseDTO(
                entity.getId(),
                entity.getCustomerName(),
                entity.getCustomerEmail(),
                entity.getOrderDate(),
                entity.getStatus(),
                itemDTOs,
                entity.calculateTotalAmount());
    }

    private OrderItem toItemEntity(OrderItemDTO dto) {
        if (dto == null) {
            return null;
        }
        return new OrderItem(
                dto.productName(),
                dto.quantity(),
                dto.unitPrice());
    }

    private OrderItemDTO toItemDTO(OrderItem entity) {
        if (entity == null) {
            return null;
        }
        return new OrderItemDTO(
                entity.getProductName(),
                entity.getQuantity(),
                entity.getUnitPrice());
    }
}
