package com.mglvarella.gatewayapi.orders.api.controller;

import com.mglvarella.gatewayapi.orders.api.dto.OrderItemDTO;
import com.mglvarella.gatewayapi.orders.api.dto.OrderRequestDTO;
import com.mglvarella.gatewayapi.orders.api.dto.OrderResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order management (proxied to order-api)")
public class OrderProxyController {

    private final RestClient orderApiRestClient;

    public OrderProxyController(RestClient orderApiRestClient) {
        this.orderApiRestClient = orderApiRestClient;
    }

    @PostMapping
    @Operation(summary = "Create a new order")
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO body) {
        OrderResponseDTO response = orderApiRestClient.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(OrderResponseDTO.class);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        OrderResponseDTO response = orderApiRestClient.get()
                .uri("/orders/{id}", id)
                .retrieve()
                .body(OrderResponseDTO.class);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order (ADMIN only)")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderApiRestClient.delete()
                .uri("/orders/{id}", id)
                .retrieve()
                .toBodilessEntity();

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "Add item to an existing order")
    public ResponseEntity<OrderResponseDTO> addItem(@PathVariable Long id, @RequestBody OrderItemDTO body) {
        OrderResponseDTO response = orderApiRestClient.post()
                .uri("/orders/{id}/items", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(OrderResponseDTO.class);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing order")
    public ResponseEntity<OrderResponseDTO> updateOrder(@PathVariable Long id, @RequestBody OrderRequestDTO body) {
        OrderResponseDTO response = orderApiRestClient.put()
                .uri("/orders/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(OrderResponseDTO.class);

        return ResponseEntity.ok(response);
    }
}
