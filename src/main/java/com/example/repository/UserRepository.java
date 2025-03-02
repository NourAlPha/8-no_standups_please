package com.example.repository;

import com.example.model.Order;
import com.example.model.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class UserRepository extends MainRepository<User> {

    @Value("${spring.application.userDataPath}")
    private String usersPath;

    private static ArrayList<User> users;

    public UserRepository() {

    }

    public ArrayList<User> getUsers() {
        intializeUsers();
        return users;
    }

    public User getUserById(final UUID userId) {
        intializeUsers();
        for (User user : users) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not " +
                "found");
    }

    public User addUser(final User user) {
        intializeUsers();
        users.add(user);
        save(user);
        return user;
    }

    public List<Order> getOrdersByUserId(final UUID userId) {
        User user = getUserById(userId);
        return user.getOrders();
    }

    public void addOrderToUser(final UUID userId, final Order order) {
        User user = getUserById(userId);
        user.addOrder(order);
        overrideData(users);
    }

    public void removeOrderFromUser(final UUID userId, final UUID orderId) {
        User user = getUserById(userId);
        List<Order> orders = user.getOrders();
        for (Order order : orders) {
            if (order.getId().equals(orderId)) {
                user.removeOrder(order);
                break;
            }
        }
        overrideData(users);
    }

    public void deleteUserById(final UUID userId) {
        intializeUsers();
        User user = getUserById(userId);
        users.remove(user);
        overrideData(users);
    }

    @Override
    protected String getDataPath() {
        return usersPath;
    }

    @Override
    protected Class<User[]> getArrayType() {
        return User[].class;
    }

    private void intializeUsers() {
        if (users == null) {
            users = findAll();
        }
    }
}
