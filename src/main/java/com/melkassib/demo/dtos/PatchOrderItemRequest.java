package com.melkassib.demo.dtos;

import java.math.BigDecimal;

public record PatchOrderItemRequest(
        Long id,
        String productName,
        Integer quantity,
        BigDecimal price
) {
}
