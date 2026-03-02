package com.mglvarella.orderapi.orders.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerName;
    private String customerEmail;
    private LocalDateTime orderDate;
    private OrderStatus status;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;
    private BigDecimal totalAmount;

    public Order() {
    }

    public Order(
            String customerName,
            String customerEmail,
            List<OrderItem> items) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.items = items;
        if (items != null) {
            items.forEach(item -> item.setOrder(this));
        }
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
        this.calculateTotalAmount();
    }

    public Long getId() {
        return id;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerName() {
        return this.customerName;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerEmail() {
        return this.customerEmail;
    }

    public LocalDateTime getOrderDate() {
        return this.orderDate;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public BigDecimal calculateTotalAmount() {
        this.totalAmount = (items == null) ? BigDecimal.ZERO
                : items.stream()
                        .map(OrderItem::getSubTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        return this.totalAmount;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
        item.setOrder(this);
        calculateTotalAmount();
    }
}
