package com.melkassib.demo.repositories;

import com.melkassib.demo.exceptions.OrderNotFoundException;
import com.melkassib.demo.models.Order;
import com.melkassib.demo.models.OrderItem;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository {

    private final JdbcClient jdbcClient;

    public OrderRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Order> findAllOrders() {
        return jdbcClient.sql("SELECT * FROM orders")
                .query((rs, rowNum) -> {
                    Long orderId = rs.getLong("id");
                    List<OrderItem> items = jdbcClient.sql("SELECT * FROM order_items WHERE order_id = :orderId")
                            .param("orderId", orderId)
                            .query(OrderItem.class)
                            .list();
                    
                    return new Order(
                            orderId,
                            rs.getString("order_number"),
                            items,
                            rs.getBigDecimal("total_price"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                }).list();
    }

    public Optional<Order> findOrderById(Long id) {
        return jdbcClient.sql("SELECT * FROM orders WHERE id = :id")
                .param("id", id)
                .query((rs, _) -> {
                    Long orderId = rs.getLong("id");
                    List<OrderItem> items = jdbcClient.sql("SELECT * FROM order_items WHERE order_id = :orderId")
                            .param("orderId", orderId)
                            .query(OrderItem.class)
                            .list();

                    return new Order(
                            orderId,
                            rs.getString("order_number"),
                            items,
                            rs.getBigDecimal("total_price"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                }).optional();
    }

    public Order createOrder(Order order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        BigDecimal totalPrice = order.orderItems().stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        jdbcClient.sql("INSERT INTO orders (order_number, total_price, created_at) VALUES (:orderNumber, :totalPrice, :createdAt)")
                .param("orderNumber", order.orderNumber())
                .param("totalPrice", totalPrice)
                .param("createdAt", order.createdAt())
                .update(keyHolder, "id");

        Long orderId = keyHolder.getKeyAs(Long.class);

        List<OrderItem> orderItems = order.orderItems().stream()
                .map(item -> {
                    KeyHolder itemKeyHolder = new GeneratedKeyHolder();
                    jdbcClient.sql("""
                            INSERT INTO order_items (order_id, product_name, quantity, price)
                            VALUES (:orderId, :productName, :quantity, :price)
                            """)
                            .param("orderId", orderId)
                            .param("productName", item.productName())
                            .param("quantity", item.quantity())
                            .param("price", item.price())
                            .update(itemKeyHolder, "id");

                    return new OrderItem(itemKeyHolder.getKeyAs(Long.class), orderId, item.productName(), item.quantity(), item.price());
                }).toList();

        return new Order(orderId, order.orderNumber(), orderItems, totalPrice, order.createdAt());
    }

    public void deleteOrderById(Long id) {
        int updated = jdbcClient.sql("DELETE FROM orders WHERE id = :id")
                .param("id", id)
                .update();

        if (updated == 0) {
            throw new OrderNotFoundException(id);
        }
    }

    public Order updateOrder(Order order) {
        Order existingOrder = findOrderById(order.id())
                .orElseThrow(() -> new OrderNotFoundException(order.id()));

        BigDecimal totalPrice = order.orderItems().stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        jdbcClient.sql("UPDATE orders SET order_number = :orderNumber, total_price = :totalPrice WHERE id = :id")
                .param("orderNumber", order.orderNumber())
                .param("totalPrice", totalPrice)
                .param("id", order.id())
                .update();

        jdbcClient.sql("DELETE FROM order_items WHERE order_id = :orderId")
                .param("orderId", order.id())
                .update();

        List<OrderItem> orderItems = order.orderItems().stream()
                .map(item -> {
                    KeyHolder itemKeyHolder = new GeneratedKeyHolder();
                    jdbcClient.sql("""
                            INSERT INTO order_items (order_id, product_name, quantity, price)
                            VALUES (:orderId, :productName, :quantity, :price)
                            """)
                            .param("orderId", order.id())
                            .param("productName", item.productName())
                            .param("quantity", item.quantity())
                            .param("price", item.price())
                            .update(itemKeyHolder, "id");

                    return new OrderItem(
                            itemKeyHolder.getKeyAs(Long.class),
                            order.id(),
                            item.productName(),
                            item.quantity(),
                            item.price()
                    );
                }).toList();

        return new Order(order.id(), order.orderNumber(), orderItems, totalPrice, existingOrder.createdAt());
    }
}
