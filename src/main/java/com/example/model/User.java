package com.example.model;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class User {
    private UUID id;
    private String name;
    private List<Order> orders = new ArrayList<>();

    public User() {
        this.id = UUID.randomUUID();
    }

    public User(final String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(final List<Order> orders) {
        this.orders = orders;
    }

    public void addOrder(final Order order) {
        this.orders.add(order);
    }

    public void removeOrder(final Order order) {
        this.orders.remove(order);
    }
}
