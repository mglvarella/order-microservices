package com.mglvarella.orderapi.orders.api.mapper;

import com.mglvarella.orderapi.orders.api.dto.OrderItemDTO;
import com.mglvarella.orderapi.orders.api.dto.OrderRequestDTO;
import com.mglvarella.orderapi.orders.api.dto.OrderResponseDTO;
import com.mglvarella.orderapi.orders.domain.model.Order;
import com.mglvarella.orderapi.orders.domain.model.OrderItem;
import com.mglvarella.orderapi.orders.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private final OrderMapper mapper = new OrderMapper();

    @Test
    void toEntity_shouldMapDtoToOrder() {
        OrderItemDTO itemDTO = new OrderItemDTO("Product A", 2, BigDecimal.TEN);
        OrderRequestDTO requestDTO = new OrderRequestDTO("John", "john@test.com", List.of(itemDTO));

        Order order = mapper.toEntity(requestDTO);

        assertNotNull(order);
        assertEquals("John", order.getCustomerName());
        assertEquals("john@test.com", order.getCustomerEmail());
        assertEquals(1, order.getItems().size());
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    void toEntity_shouldReturnNull_whenDtoIsNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void toResponse_shouldMapOrderToDto() {
        Order order = new Order("John", "john@test.com",
                List.of(new OrderItem("Product A", 2, BigDecimal.TEN)));

        OrderResponseDTO response = mapper.toResponse(order);

        assertNotNull(response);
        assertEquals("John", response.customerName());
        assertEquals("john@test.com", response.customerEmail());
        assertEquals(1, response.items().size());
        assertEquals(BigDecimal.valueOf(20), response.totalAmount());
    }

    @Test
    void toResponse_shouldReturnNull_whenEntityIsNull() {
        assertNull(mapper.toResponse(null));
    }

    @Test
    void toItemEntity_shouldMapDtoToOrderItem() {
        OrderItemDTO dto = new OrderItemDTO("Product B", 3, BigDecimal.valueOf(5));

        OrderItem item = mapper.toItemEntity(dto);

        assertNotNull(item);
        assertEquals("Product B", item.getProductName());
        assertEquals(3, item.getQuantity());
        assertEquals(BigDecimal.valueOf(5), item.getUnitPrice());
        assertEquals(BigDecimal.valueOf(15), item.getSubTotal());
    }

    @Test
    void toItemEntity_shouldReturnNull_whenDtoIsNull() {
        assertNull(mapper.toItemEntity(null));
    }

    @Test
    void toItemDTO_shouldMapEntityToDto() {
        OrderItem item = new OrderItem("Product C", 1, BigDecimal.valueOf(50));

        OrderItemDTO dto = mapper.toItemDTO(item);

        assertNotNull(dto);
        assertEquals("Product C", dto.productName());
        assertEquals(1, dto.quantity());
        assertEquals(BigDecimal.valueOf(50), dto.unitPrice());
    }

    @Test
    void toItemDTO_shouldReturnNull_whenEntityIsNull() {
        assertNull(mapper.toItemDTO(null));
    }
}
