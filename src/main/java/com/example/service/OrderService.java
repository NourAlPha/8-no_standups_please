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
        checkObject(order);
        checkId(order.getId());
        if (order.getUserId() == null) {
            throw new ValidationException("User ID is required");
        }
        orderRepository.addOrder(order);
    }

    public ArrayList<Order> getOrders() {
        return getObjects();
    }

    public Order getOrderById(final UUID orderId) {
        return getObjectById(orderId);
    }

    public void deleteOrderById(final UUID orderId) {
        checkId(orderId);
        orderRepository.deleteOrderById(orderId);
    }
}
