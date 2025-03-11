package com.example.controller;

import com.example.model.Cart;
import com.example.model.Product;
import com.example.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
@Tag(name = "Cart Controller", description = "APIs for managing shopping carts")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(final CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/")
    @Operation(summary = "Add a new cart",
            description = "Creates a new cart in the system")
    @ApiResponse(responseCode = "200",
            description = "Cart created successfully")
    public Cart addCart(@RequestBody final Cart cart) {
        return cartService.addCart(cart);
    }

    @GetMapping("/")
    @Operation(summary = "Get all carts",
            description = "Retrieves all carts in the system")
    @ApiResponse(responseCode = "200",
            description = "List of carts retrieved successfully")
    public ArrayList<Cart> getCarts() {
        return cartService.getCarts();
    }

    @GetMapping("/{cartId}")
    @Operation(summary = "Get a specific cart",
            description = "Retrieves a cart by its ID")
    @ApiResponse(responseCode = "200",
            description = "Cart retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Cart not found")
    public Cart getCartById(@PathVariable final UUID cartId) {
        return cartService.getCartById(cartId);
    }

    @PutMapping("/addProduct/{cartId}")
    @Operation(summary = "Add a product to a cart",
            description = "Adds a product to the specified cart")
    @ApiResponse(responseCode = "200",
            description = "Product added to cart successfully")
    @ApiResponse(responseCode = "404", description = "Cart not found")
    public String addProductToCart(@PathVariable final UUID cartId,
                                   @RequestBody final Product product) {
        cartService.addProductToCart(cartId, product);
        return "Product added to cart successfully.";
    }

    @DeleteMapping("/delete/{cartId}")
    @Operation(summary = "Delete a cart",
            description = "Deletes a cart by its ID")
    @ApiResponse(responseCode = "200",
            description = "Cart deleted successfully")
    @ApiResponse(responseCode = "404", description = "Cart not found")
    public String deleteCartById(@PathVariable final UUID cartId) {
        cartService.deleteCartById(cartId);
        return "Cart deleted successfully";
    }
}
