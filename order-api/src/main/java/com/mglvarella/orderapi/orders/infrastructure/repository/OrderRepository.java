package com.mglvarella.orderapi.orders.infrastructure.repository;

import com.mglvarella.orderapi.orders.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
