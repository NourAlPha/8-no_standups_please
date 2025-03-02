package com.example.repository;

import com.example.model.Order;
import com.example.model.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class UserRepository extends GenericRepository<User> {

    @Value("${spring.application.userDataPath}")
    private String usersPath;

    public UserRepository() {

    }

    public ArrayList<User> getUsers() {
        return getObjects();
    }

    public User getUserById(final UUID userId) {
        return getObjectById(userId);
    }

    public User addUser(final User user) {
        return addObject(user);
    }

    public List<Order> getOrdersByUserId(final UUID userId) {
        User user = getUserById(userId);
        return user.getOrders();
    }

    public void addOrderToUser(final UUID userId, final Order order) {
        User user = getUserById(userId);
        user.addOrder(order);
        overrideData(getUsers());
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
        overrideData(getUsers());
    }

    public void deleteUserById(final UUID userId) {
        deleteObjectById(userId);
    }

    @Override
    protected String getDataPath() {
        return usersPath;
    }

    @Override
    protected Class<User[]> getArrayType() {
        return User[].class;
    }
}
