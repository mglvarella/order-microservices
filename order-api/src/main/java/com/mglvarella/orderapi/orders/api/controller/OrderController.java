package com.mglvarella.orderapi.orders.api.controller;

import com.mglvarella.orderapi.orders.api.dto.OrderItemDTO;
import com.mglvarella.orderapi.orders.api.dto.OrderRequestDTO;
import com.mglvarella.orderapi.orders.api.dto.OrderResponseDTO;
import com.mglvarella.orderapi.orders.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> create(@RequestBody OrderRequestDTO data) {
        return ResponseEntity.ok(this.orderService.createOrder(data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(this.orderService.getOrderById(id));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<OrderResponseDTO> addItem(@PathVariable Long id, @RequestBody OrderItemDTO data) {
        return ResponseEntity.ok(this.orderService.addOrderItem(id, data));
    }

}
