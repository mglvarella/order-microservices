package com.mglvarella.orderapi.orders.service;

import com.mglvarella.orderapi.orders.api.dto.OrderItemDTO;
import com.mglvarella.orderapi.orders.api.dto.OrderRequestDTO;
import com.mglvarella.orderapi.orders.api.dto.OrderResponseDTO;
import com.mglvarella.orderapi.orders.api.mapper.OrderMapper;
import com.mglvarella.orderapi.orders.domain.model.Order;
import com.mglvarella.orderapi.orders.domain.model.OrderItem;
import com.mglvarella.orderapi.orders.infrastructure.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
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

    public OrderResponseDTO getOrderById(Long orderId) {
        Order order = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find an Order with id: " + orderId));

        return orderMapper.toResponse(order);
    }

    public void deleteItemById(Long orderId) {
        Order order = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(("Failed to find an Order with id: " + orderId)));
        this.orderRepository.delete(order);
    }

    @Transactional
    public OrderResponseDTO addOrderItem(Long orderId, OrderItemDTO orderItemDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find an Order with id: " + orderId));

        OrderItem itemToAdd = orderMapper.toItemEntity(orderItemDTO);

        order.addItem(itemToAdd);

        return orderMapper.toResponse(order);
    }

    @Transactional
    public OrderResponseDTO updateOrder(Long orderId, OrderRequestDTO orderRequestDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find an Order with id: " + orderId));

        order.setCustomerName(orderRequestDTO.customerName());
        order.setCustomerEmail(orderRequestDTO.customerEmail());

        order.getItems().clear();

        if (orderRequestDTO.items() != null) {
            orderRequestDTO.items().stream()
                    .map(orderMapper::toItemEntity)
                    .forEach(order::addItem);
        }

        return orderMapper.toResponse(orderRepository.save(order));
    }
}
