package com.melkassib.demo.services;

import com.melkassib.demo.dtos.CreateOrderRequest;
import com.melkassib.demo.dtos.PatchOrderItemRequest;
import com.melkassib.demo.dtos.PatchOrderRequest;
import com.melkassib.demo.exceptions.OrderNotFoundException;
import com.melkassib.demo.models.Order;
import com.melkassib.demo.models.OrderItem;
import com.melkassib.demo.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllOrders();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findOrderById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public Order createOrder(CreateOrderRequest createOrderRequest) {
        List<OrderItem> orderItems = createOrderRequest.items().stream()
                .map(itemRequest ->
                    new OrderItem(
                        null,
                        null,
                        itemRequest.productName(),
                        itemRequest.quantity(),
                        itemRequest.price()
                    )
                )
                .toList();

        Order newOrder = new Order(null, createOrderRequest.orderNumber(), orderItems, null, LocalDateTime.now());
        return orderRepository.createOrder(newOrder);
    }

    public Order updateOrder(Long id, PatchOrderRequest patchOrderRequest) {
        Order existingOrder = getOrderById(id);

        String orderNumber = patchOrderRequest.orderNumber() != null ? patchOrderRequest.orderNumber() : existingOrder.orderNumber();
        List<OrderItem> currentItems = new ArrayList<>(existingOrder.orderItems());

        if (patchOrderRequest.items() != null) {
            for (PatchOrderItemRequest patchItemRequest : patchOrderRequest.items()) {
                if (patchItemRequest.id() != null) {
                    for (int i = 0; i < currentItems.size(); i++) {
                        OrderItem existingItem = currentItems.get(i);
                        if (existingItem.id().equals(patchItemRequest.id())) {
                            String productName = patchItemRequest.productName() != null ? patchItemRequest.productName() : existingItem.productName();
                            Integer quantity = patchItemRequest.quantity() != null ? patchItemRequest.quantity() : existingItem.quantity();
                            BigDecimal price = patchItemRequest.price() != null ? patchItemRequest.price() : existingItem.price();
                            currentItems.set(i, new OrderItem(existingItem.id(), id, productName, quantity, price));
                            break;
                        }
                    }
                } else {
                    currentItems.add(new OrderItem(
                            null,
                            id,
                            patchItemRequest.productName(),
                            patchItemRequest.quantity() != null ? patchItemRequest.quantity() : 0,
                            patchItemRequest.price() != null ? patchItemRequest.price() : BigDecimal.ZERO
                    ));
                }
            }
        }

        Order updatedOrder = new Order(id, orderNumber, currentItems, null, existingOrder.createdAt());
        return orderRepository.updateOrder(updatedOrder);
    }

    public void deleteOrderById(Long id) {
        orderRepository.deleteOrderById(id);
    }

}
