package com.example.service;

import com.example.model.Cart;
import com.example.model.Product;
import com.example.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class CartService {

    private final CartRepository cartRepository;

    @Autowired
    public CartService(final CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Cart addCart(final Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }
        if (cart.getUserId() == null) {
            throw new IllegalArgumentException("Cart must have a user ID");
        }

        Cart existingCart = cartRepository.getCartByUserId(cart.getUserId());
        if (existingCart != null) {
            throw new IllegalStateException("A cart already exists for user ID: " + cart.getUserId());
        }
        return cartRepository.addCart(cart);
    }

    public ArrayList<Cart> getCarts() {
        ArrayList<Cart> carts = cartRepository.getCarts();
        if (carts == null) {
            throw new IllegalStateException("No carts found");
        }
        return carts;
    }

    public Cart getCartById(final UUID cartId) {
        if (cartId == null) {
            throw new IllegalArgumentException("Cart ID cannot be null");
        }
        Cart cart = cartRepository.getCartById(cartId);
        if (cart == null) {
            throw new IllegalStateException("Cart not found with ID: " + cartId);
        }
        return cart;
    }

    public Cart getCartByUserId(final UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        Cart cart = cartRepository.getCartByUserId(userId);
        if (cart == null) {
            throw new IllegalStateException("No cart found for user ID: " + userId);
        }
        return cart;
    }

    public void addProductToCart(final UUID cartId, final Product product) {
        if (cartId == null) {
            throw new IllegalArgumentException("Cart ID cannot be null");
        }
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (product.getId() == null) {
            throw new IllegalArgumentException("Product must have an ID");
        }
        Cart cart = cartRepository.getCartById(cartId);
        if (cart == null) {
            throw new IllegalStateException("Cart not found with ID: " + cartId);
        }
        cartRepository.addProductToCart(cartId, product);
    }

    public void deleteProductFromCart(final UUID cartId,
                                      final Product product) {
        cartRepository.deleteProductFromCart(cartId, product);
    }

    public void deleteCartById(final UUID cartId) {
        cartRepository.deleteCartById(cartId);
    }

    public double emptyCart(final UUID cartId) {
        return cartRepository.emptyCart(cartId);
    }
}

