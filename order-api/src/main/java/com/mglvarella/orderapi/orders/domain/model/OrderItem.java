package com.mglvarella.orderapi.orders.domain.model;

import java.math.BigDecimal;

public class OrderItem {
    private Long id;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subTotal;
}
