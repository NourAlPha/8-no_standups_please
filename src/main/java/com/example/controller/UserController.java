package com.example.controller;

import com.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@Tag(name = "User Controller", description = "Endpoints related to users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/{userId}/removeOrder")
    @Operation(
            summary = "Remove order from user",
            description = "Removes an order from a user by his id and the "
                    + "order id"
    )
    public String removeOrderFromUser(@PathVariable final UUID userId,
                                      @RequestParam final UUID orderId) {
        userService.removeOrderFromUser(userId, orderId);
        return "Order has been removed from user";
    }

    @DeleteMapping("/{userId}/emptyCart")
    @Operation(
            summary = "Empty cart",
            description = "Empties the cart of a user by his id"
    )
    public String emptyCart(@PathVariable final UUID userId) {
        userService.emptyCart(userId);
        return "Cart has been emptied";
    }

    @PutMapping("/addProductToCart")
    @Operation(
            summary = "Add product to cart",
            description = "Adds a product to the cart of a user by his id and "
                    + "the product id"
    )
    public String addProductToCart(@RequestParam final UUID userId,
                                   @RequestParam final UUID productId) {
        userService.addProductToCart(userId, productId);
        return "Product has been added to cart";
    }

    @PutMapping("/deleteProductFromCart")
    @Operation(
            summary = "Delete product from cart",
            description = "Deletes a product from the cart of a user by his id "
                    + "and the product id"
    )
    public String deleteProductFromCart(@RequestParam final UUID userId,
                                        @RequestParam final UUID productId) {
        userService.deleteProductFromCart(userId, productId);
        return "Product has been deleted from cart";
    }


    @DeleteMapping("/delete/{userId}")
    @Operation(
            summary = "Delete user",
            description = "Deletes a user by his id"
    )
    public String deleteUserById(@PathVariable final UUID userId) {
        userService.deleteUserById(userId);
        return "User deleted";
    }

}
