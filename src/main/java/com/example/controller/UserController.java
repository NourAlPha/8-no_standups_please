package com.example.controller;

import com.example.model.Order;
import com.example.model.User;
import com.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@Tag(name = "User Controller", description = "Endpoints related to Users")

public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/")
    @Operation(
            summary = "Create new user",
            description = "Adds a new user to the system."
    )
    public User addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @GetMapping("/")
    @Operation(
            summary = "Gets all users",
            description = "gets all the users in the system."
    )
    public ArrayList<User> getUsers(){
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    @Operation(
            summary = "Get specific user",
            description = "get a specific user in the system."
    )
    public User getUserById(@PathVariable UUID userId){
        return userService.getUserById(userId);
    }

    @GetMapping("/{userId}/orders")
    @Operation(
            summary = "Get the orders of specific user",
            description = "get the orders of specific user in the system by passing the user ID."
    )
    public List<Order> getOrdersByUserId(@PathVariable UUID userId){
        return userService.getOrdersByUserId(userId);
    }




}
