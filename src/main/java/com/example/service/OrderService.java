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
public class OrderService extends MainService<Order> {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderService(final OrderRepository orderRepository,
                        final UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public void addOrder(final Order order) {
        if (order.getId() == null) {
            throw new ValidationException("Order id cannot be null");
        }
        orderRepository.addOrder(order);
    }

    public ArrayList<Order> getOrders() {
        return orderRepository.getOrders();
    }

    public Order getOrderById(final UUID orderId) {
        if (orderId == null) {
            throw new ValidationException("id cannot be null");
        }
        return orderRepository.getOrderById(orderId);
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
