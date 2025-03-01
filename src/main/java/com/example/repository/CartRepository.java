package com.example.repository;

import com.example.model.Cart;
import com.example.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class CartRepository extends MainRepository<Cart> {

    private String cartsPath;
    private static ArrayList<Cart> carts = new ArrayList<>();

    public CartRepository() {
    }

    public Cart addCart(Cart cart) {
        if(!carts.isEmpty()){
            carts.add(cart);
        }

        save(cart);
        return cart;
    }

    public ArrayList<Cart> getCarts() {
        initializeCarts();
        return carts;
    }

    public Cart getCartById(UUID id) {
        initializeCarts();
        for (Cart cart : carts) {
            if (cart.getId().equals(id)) {
                return cart;
            }
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Cart with id %s not found", id));
    }

    public Cart getCartByUserId(UUID userId) {
        initializeCarts();
        for (Cart cart : carts) {
            if (cart.getUserId().equals(userId)) {
                return cart;
            }
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Cart with userId %s not found", userId));
    }

    public void addProductToCart(UUID cartId, Product product) {
        initializeCarts();
        Cart cart = getCartById(cartId);
        cart.addProduct(product);

        save(cart);
    }

    public void deleteProductFromCart(UUID cartId, Product product) {
        initializeCarts();
        Cart cart = getCartById(cartId);
        cart.removeProduct(product);

        save(cart);
    }

    public void deleteCartById(UUID cartId) {
        initializeCarts();
        Cart cart = getCartById(cartId);
        carts.remove(cart);

        saveAll(carts);
    }


    private void initializeCarts() {
        if (carts.isEmpty()) {
            carts = findAll();
        }
    }

    @Override
    protected String getDataPath() {
        return cartsPath;
    }

    @Override
    protected Class<Cart[]> getArrayType() {
        return Cart[].class;
    }
}
