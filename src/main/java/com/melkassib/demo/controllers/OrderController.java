package com.melkassib.demo.controllers;

import com.melkassib.demo.dtos.CreateOrderRequest;
import com.melkassib.demo.dtos.OrderDto;
import com.melkassib.demo.dtos.PatchOrderRequest;
import com.melkassib.demo.mappers.OrderMapper;
import com.melkassib.demo.models.Order;
import com.melkassib.demo.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderDto> getOrders() {
        return orderService.getAllOrders().stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(OrderMapper.toDto(order));
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest) {
        Order createdOrder = orderService.createOrder(createOrderRequest);
        return new ResponseEntity<>(OrderMapper.toDto(createdOrder), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrder(
        @PathVariable Long id,
        @RequestBody PatchOrderRequest patchOrderRequest
    ) {
        Order updatedOrder = orderService.updateOrder(id, patchOrderRequest);
        return ResponseEntity.ok(OrderMapper.toDto(updatedOrder));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable Long id) {
        orderService.deleteOrderById(id);
        return ResponseEntity.noContent().build();
    }
}
