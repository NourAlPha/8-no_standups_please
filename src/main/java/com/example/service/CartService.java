package com.example.service;

import com.example.exception.ValidationException;
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
        if (cart.getId() == null) {
            throw new ValidationException("Cart id cannot be null");
        }
        return cartRepository.addCart(cart);
    }

    public ArrayList<Cart> getCarts() {
        return cartRepository.getCarts();
    }

    public Cart getCartById(final UUID cartId) {
        if (cartId == null) {
            throw new ValidationException("id cannot be null");
        }
        return cartRepository.getCartById(cartId);
    }

    public Cart getCartByUserId(final UUID userId) {
        return cartRepository.getCartByUserId(userId);
    }

    public void addProductToCart(final UUID cartId, final Product product) {
        cartRepository.addProductToCart(cartId, product);
    }

    public void deleteProductFromCart(final UUID cartId,
                                      final Product product) {
        cartRepository.deleteProductFromCart(cartId, product);
    }

    public double emptyCart(final UUID cartId) {
        return cartRepository.emptyCart(cartId);
    }
}

