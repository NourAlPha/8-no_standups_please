package com.example.service;

import com.example.model.Order;
import com.example.repository.OrderRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

        try {
            if (order.getUserId() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "User ID is required");
            }
            if (order.getProducts() == null || order.getProducts().isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Products are required");
            }
            // Checking if the user exists.
            userRepository.getUserById(order.getUserId());
            orderRepository.addOrder(order);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }

    public ArrayList<Order> getOrders() {
        return orderRepository.getOrders();
    }

    public Order getOrderById(final UUID orderId) {
        return orderRepository.getOrderById(orderId);
    }

    public void deleteOrderById(final UUID orderId)
            throws IllegalArgumentException {
        // Removing the order from the user (mimicking referencing).
        Order order = orderRepository.getOrderById(orderId);
        userRepository.removeOrderFromUser(order.getUserId(), orderId);

        orderRepository.deleteOrderById(orderId);
    }
}
