package com.melkassib.demo.dtos;

import java.util.List;

public record PatchOrderRequest(
        String orderNumber,
        List<PatchOrderItemRequest> items
) {
}
