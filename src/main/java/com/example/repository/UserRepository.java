package com.example.repository;
import com.example.model.Order;
import com.example.model.User;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.UUID;

@Repository
public class UserRepository {

    public static ArrayList<User> users;

    public void findAll() {
        try {
            users = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();
            ClassPathResource resource = new ClassPathResource("users.json");
            InputStream inputStream = resource.getInputStream();
            users = objectMapper.readValue(inputStream,
                    new TypeReference<>() {
                    });
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "File not found: " + e.getMessage());
        }
    }

    public ArrayList<User> getUsers() {
        if (users == null) {
            findAll();
        }
        return users;
    }

    public User getUserById(UUID userId) {
        if (users == null) {
            findAll();
        }
        for (User user : users) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "User not found");
    }

    public User addUser(User user) {
        if (users == null) {
            findAll();
        }
        users.add(user);
        writeUsers(users);
        return user;
    }

    public List<Order> getOrdersByUserId(UUID userId) {
        User user = getUserById(userId);
        return user.getOrders();
    }

    public void addOrderToUser(UUID userId, Order order) {
        User user = getUserById(userId);
        user.addOrder(order);
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(userId)) {
                users.set(i, user);
                break;
            }
        }
        writeUsers(users);
    }

    public void removeOrderFromUser(UUID userId, UUID orderId) {
        User user = getUserById(userId);
        List<Order> orders = user.getOrders();
        for (Order order : orders) {
            if (order.getId().equals(orderId)) {
                orders.remove(order);
                break;
            }
        }
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(userId)) {
                users.set(i, user);
                break;
            }
        }
        writeUsers(users);
    }

    public void deleteUserById(UUID userId) {
        if (users == null) {
            findAll();
        }
        for (User user : users) {
            if (user.getId().equals(userId)) {
                users.remove(user);
                writeUsers(users);
                return;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "User not found");
    }

    public void writeUsers(ArrayList<User> users) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ClassPathResource resource = new ClassPathResource("users.json");
            objectMapper.writeValue(resource.getFile(), users);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "File not found: " + e.getMessage());
        }
    }
}
