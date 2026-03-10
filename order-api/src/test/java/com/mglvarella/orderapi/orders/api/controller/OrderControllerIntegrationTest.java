package com.mglvarella.orderapi.orders.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mglvarella.orderapi.orders.api.dto.OrderItemDTO;
import com.mglvarella.orderapi.orders.api.dto.OrderRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderRequestDTO createSampleRequest() {
        return new OrderRequestDTO(
                "John Doe",
                "john@example.com",
                List.of(new OrderItemDTO("Product A", 2, BigDecimal.TEN)));
    }

    private String createOrderAndGetId() throws Exception {
        MvcResult result = mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createSampleRequest())))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    @Test
    void createOrder_shouldReturn200WithOrderResponse() throws Exception {
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createSampleRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerName").value("John Doe"))
                .andExpect(jsonPath("$.customerEmail").value("john@example.com"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productName").value("Product A"))
                .andExpect(jsonPath("$.totalAmount").value(20));
    }

    @Test
    void getOrderById_shouldReturn200_whenOrderExists() throws Exception {
        String orderId = createOrderAndGetId();

        mockMvc.perform(get("/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.customerName").value("John Doe"));
    }

    @Test
    void getOrderById_shouldReturn500_whenOrderDoesNotExist() throws Exception {
        mockMvc.perform(get("/orders/{id}", 99999))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateOrder_shouldReturn200WithUpdatedData() throws Exception {
        String orderId = createOrderAndGetId();

        OrderRequestDTO updateRequest = new OrderRequestDTO(
                "Jane Doe",
                "jane@example.com",
                List.of(new OrderItemDTO("Product B", 3, BigDecimal.valueOf(15))));

        mockMvc.perform(put("/orders/{id}", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Jane Doe"))
                .andExpect(jsonPath("$.customerEmail").value("jane@example.com"))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productName").value("Product B"));
    }

    @Test
    void addItem_shouldReturn200WithNewItem() throws Exception {
        String orderId = createOrderAndGetId();

        OrderItemDTO newItem = new OrderItemDTO("Product C", 1, BigDecimal.valueOf(50));

        mockMvc.perform(post("/orders/{id}/items", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[1].productName").value("Product C"));
    }

    @Test
    void deleteOrder_shouldReturn204() throws Exception {
        String orderId = createOrderAndGetId();

        mockMvc.perform(delete("/orders/{id}", orderId))
                .andExpect(status().isNoContent());
    }
}
