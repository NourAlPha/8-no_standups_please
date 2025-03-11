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
public class CartService extends MainService<Cart, CartRepository> {

    private final CartRepository cartRepository;

    @Autowired
    public CartService(final CartRepository cartRepository) {
        super(cartRepository);
        this.cartRepository = cartRepository;
    }

    public Cart addCart(final Cart cart) {
        return addObject(cart);
    }

    public ArrayList<Cart> getCarts() {
        return getObjects();
    }

    public Cart getCartById(final UUID cartId) {
        return getObjectById(cartId);
    }

    public Cart getCartByUserId(final UUID userId) {
        return cartRepository.getCartByUserId(userId);
    }

    public void deleteCartById(final UUID cartId) {
        deleteObjectById(cartId);
    }

    public void addProductToCart(final UUID cartId, final Product product) {
        if (cartId == null) {
            throw new ValidationException("cartId cannot be null");
        }
        if (product == null) {
            throw new ValidationException("product cannot be null");
        }
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

