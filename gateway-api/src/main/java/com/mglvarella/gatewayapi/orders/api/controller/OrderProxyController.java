package com.mglvarella.gatewayapi.orders.api.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/orders")
public class OrderProxyController {

    private final RestClient orderApiRestClient;

    public OrderProxyController(RestClient orderApiRestClient) {
        this.orderApiRestClient = orderApiRestClient;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody String body) {
        String response = orderApiRestClient.post()
                .uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getOrderById(@PathVariable Long id) {
        String response = orderApiRestClient.get()
                .uri("/orders/{id}", id)
                .retrieve()
                .body(String.class);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderApiRestClient.delete()
                .uri("/orders/{id}", id)
                .retrieve()
                .toBodilessEntity();

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<String> addItem(@PathVariable Long id, @RequestBody String body) {
        String response = orderApiRestClient.post()
                .uri("/orders/{id}/items", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

        return ResponseEntity.ok(response);
    }
}
