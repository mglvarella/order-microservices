package com.mglvarella.orderapi.orders.service;

import com.mglvarella.orderapi.orders.api.dto.OrderItemDTO;
import com.mglvarella.orderapi.orders.api.dto.OrderRequestDTO;
import com.mglvarella.orderapi.orders.api.dto.OrderResponseDTO;
import com.mglvarella.orderapi.orders.api.mapper.OrderMapper;
import com.mglvarella.orderapi.orders.domain.model.Order;
import com.mglvarella.orderapi.orders.domain.model.OrderItem;
import com.mglvarella.orderapi.orders.infrastructure.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        Order order = orderMapper.toEntity(orderRequestDTO);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponse(savedOrder);
    }

    @Transactional
    public OrderResponseDTO addOrderItem(Long orderId, OrderItemDTO orderItemDTO){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new RuntimeException());

        OrderItem itemToAdd = orderMapper.toItemEntity(orderItemDTO);

        order.addItem(itemToAdd);

        return orderMapper.toResponse(order);
    }
}
