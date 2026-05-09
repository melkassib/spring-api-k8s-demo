package com.melkassib.demo.dtos;
 
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
 
public record CreateOrderRequest(
        @NotBlank(message = "Order number is required")
        String orderNumber,
        
        @Valid
        @NotEmpty(message = "Order items cannot be empty")
        List<CreateOrderItemRequest> items
) {
}
