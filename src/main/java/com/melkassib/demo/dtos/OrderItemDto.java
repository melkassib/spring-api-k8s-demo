package com.melkassib.demo.dtos;

import java.math.BigDecimal;

public record OrderItemDto(
        Long id,
        String productName,
        Integer quantity,
        BigDecimal price
) {
}
