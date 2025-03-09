package com.example.service;

import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.OrderRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService extends MainService<User>{
    private final UserRepository userRepository;

    @Autowired
    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(final User user) {
        return userRepository.addUser(user);
    }

    public ArrayList<User> getUsers() {
        return userRepository.getUsers();
    }

    public User getUserById(final UUID userId) {
        return userRepository.getUserById(userId);
    }

    public List<Order> getOrdersByUserId(final UUID userId) { return userRepository.getOrdersByUserId(userId); }


}
