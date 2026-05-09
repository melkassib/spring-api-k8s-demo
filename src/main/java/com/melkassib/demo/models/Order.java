package com.melkassib.demo.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record Order(
        Long id,
        String orderNumber,
        List<OrderItem> orderItems,
        BigDecimal totalPrice,
        LocalDateTime createdAt
) {
}
