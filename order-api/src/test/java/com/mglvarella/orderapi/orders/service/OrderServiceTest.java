package com.mglvarella.orderapi.orders.service;

import com.mglvarella.orderapi.orders.api.dto.OrderItemDTO;
import com.mglvarella.orderapi.orders.api.dto.OrderRequestDTO;
import com.mglvarella.orderapi.orders.api.dto.OrderResponseDTO;
import com.mglvarella.orderapi.orders.api.mapper.OrderMapper;
import com.mglvarella.orderapi.orders.domain.model.Order;
import com.mglvarella.orderapi.orders.domain.model.OrderItem;
import com.mglvarella.orderapi.orders.domain.model.OrderStatus;
import com.mglvarella.orderapi.orders.infrastructure.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private OrderRequestDTO requestDTO;
    private OrderResponseDTO responseDTO;
    private Order order;
    private OrderItemDTO itemDTO;

    @BeforeEach
    void setUp() {
        itemDTO = new OrderItemDTO("Product A", 2, BigDecimal.TEN);
        requestDTO = new OrderRequestDTO("John Doe", "john@example.com", List.of(itemDTO));
        responseDTO = new OrderResponseDTO(1L, "John Doe", "john@example.com",
                LocalDateTime.now(), OrderStatus.PENDING, List.of(itemDTO), BigDecimal.valueOf(20));

        order = new Order("John Doe", "john@example.com",
                new ArrayList<>(List.of(new OrderItem("Product A", 2, BigDecimal.TEN))));
    }

    @Test
    void createOrder_shouldSaveAndReturnResponse() {
        when(orderMapper.toEntity(requestDTO)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponse(order)).thenReturn(responseDTO);

        OrderResponseDTO result = orderService.createOrder(requestDTO);

        assertNotNull(result);
        assertEquals("John Doe", result.customerName());
        verify(orderRepository).save(order);
    }

    @Test
    void getOrderById_shouldReturnOrder_whenFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toResponse(order)).thenReturn(responseDTO);

        OrderResponseDTO result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void getOrderById_shouldThrow_whenNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.getOrderById(99L));
    }

    @Test
    void deleteItemById_shouldThrow_whenNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.deleteItemById(99L));
    }

    @Test
    void addOrderItem_shouldAddItemAndReturnResponse() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toItemEntity(itemDTO)).thenReturn(new OrderItem("Product A", 2, BigDecimal.TEN));
        when(orderMapper.toResponse(order)).thenReturn(responseDTO);

        OrderResponseDTO result = orderService.addOrderItem(1L, itemDTO);

        assertNotNull(result);
        verify(orderMapper).toItemEntity(itemDTO);
    }

    @Test
    void updateOrder_shouldUpdateFieldsAndReturnResponse() {
        OrderRequestDTO updateRequest = new OrderRequestDTO("Jane Doe", "jane@example.com", List.of(itemDTO));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toItemEntity(itemDTO)).thenReturn(new OrderItem("Product A", 2, BigDecimal.TEN));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponse(order)).thenReturn(responseDTO);

        OrderResponseDTO result = orderService.updateOrder(1L, updateRequest);

        assertNotNull(result);
        assertEquals("Jane Doe", order.getCustomerName());
        assertEquals("jane@example.com", order.getCustomerEmail());
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrder_shouldThrow_whenNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> orderService.updateOrder(99L, requestDTO));
    }
}
