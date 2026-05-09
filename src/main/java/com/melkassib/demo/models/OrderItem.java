package com.melkassib.demo.models;

import java.math.BigDecimal;

public record OrderItem(
        Long id,
        Long orderId,
        String productName,
        Integer quantity,
        BigDecimal price
) {
}
