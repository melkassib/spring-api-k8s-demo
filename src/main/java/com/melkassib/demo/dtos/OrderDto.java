package com.melkassib.demo.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        Long id,
        String orderNumber,
        List<OrderItemDto> orderItems,
        BigDecimal totalPrice,
        LocalDateTime createdAt
) {
}
