package com.example.repository;

import com.example.model.Cart;
import com.example.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class CartRepository extends GenericRepository<Cart> {

    @Value("${spring.application.cartDataPath}")
    private String cartsPath;

    public CartRepository() {
    }

    public Cart addCart(final Cart cart) {
        return addObject(cart);
    }

    public ArrayList<Cart> getCarts() {
        return getObjects();
    }

    public Cart getCartById(final UUID id) {
        return getObjectById(id);
    }

    public Cart getCartByUserId(final UUID userId) {
        ArrayList<Cart> carts = getCarts();
        for (Cart cart : carts) {
            if (cart.getUserId().equals(userId)) {
                return cart;
            }
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Cart with userId %s not found", userId));
    }

    public void addProductToCart(final UUID cartId, final Product product) {
        Cart cart = getCartById(cartId);
        cart.addProduct(product);
        saveAll(getCarts());
    }

    public void deleteProductFromCart(final UUID cartId,
                                      final Product product) {
        Cart cart = getCartById(cartId);
        cart.removeProduct(product);
        saveAll(getCarts());
    }

    public void deleteCartById(final UUID cartId) {
        deleteObjectById(cartId);
    }

    public void updateProductInAllCarts(final UUID productId,
                                        final double newPrice) {
        ArrayList<Cart> carts = getCarts();
        boolean updated = false;

        for (Cart cart : carts) {
            for (Product product : cart.getProducts()) {
                if (product.getId().equals(productId)) {
                    product.setPrice(newPrice);
                    updated = true;
                }
            }
        }

        // Only save if there was an update
        if (updated) {
            saveAll(carts);
        }
    }

    public void removeProductFromAllCarts(final UUID productId) {
        ArrayList<Cart> carts = getCarts();
        boolean updated = false;

        for (Cart cart : carts) {
            boolean removed = cart.getProducts().removeIf(product ->
                    product.getId().equals(productId));
            if (removed) {
                updated = true;
            }
        }

        if (updated) {
            saveAll(carts);
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
