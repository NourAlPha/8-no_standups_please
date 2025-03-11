package com.example.service;

import com.example.exception.ValidationException;
import com.example.model.Order;
import com.example.repository.OrderRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class OrderService extends MainService<Order, OrderRepository> {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderService(final OrderRepository orderRepository,
                        final UserRepository userRepository) {
        super(orderRepository);
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public void addOrder(final Order order) {
        try {
            if (order == null) {
                throw new IllegalArgumentException("Order cannot be null");
            }
            if (order.getUserId() == null) {
                throw new IllegalArgumentException("User ID is required");
            }
            if (order.getProducts() == null || order.getProducts().isEmpty()) {
                throw new IllegalArgumentException("Products are required");
            }
            // Checking if the user exists.
            userRepository.getUserById(order.getUserId());
            orderRepository.addOrder(order);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public ArrayList<Order> getOrders() {
        return getObjects();
    }

    public Order getOrderById(final UUID orderId) {
        return getObjectById(orderId);
    }

    public void deleteOrderById(final UUID orderId) {
        if (orderId == null) {
            throw new ValidationException("id cannot be null");
        }
        // Removing the order from the user (mimicking referencing).
        Order order = orderRepository.getOrderById(orderId);
        userRepository.removeOrderFromUser(order.getUserId(), orderId);

        orderRepository.deleteOrderById(orderId);
    }
}
