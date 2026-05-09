package com.melkassib.demo.mappers;

import com.melkassib.demo.dtos.OrderDto;
import com.melkassib.demo.dtos.OrderItemDto;
import com.melkassib.demo.models.Order;
import com.melkassib.demo.models.OrderItem;

import java.util.List;

public class OrderMapper {

    public static OrderDto toDto(Order order) {
        if (order == null) {
            return null;
        }

        List<OrderItemDto> itemDtos = order.orderItems().stream()
                .map(OrderMapper::toDto)
                .toList();

        return new OrderDto(
                order.id(),
                order.orderNumber(),
                itemDtos,
                order.totalPrice(),
                order.createdAt()
        );
    }

    public static OrderItemDto toDto(OrderItem item) {
        if (item == null) {
            return null;
        }

        return new OrderItemDto(
                item.id(),
                item.productName(),
                item.quantity(),
                item.price()
        );
    }

    private OrderMapper() {}
}
