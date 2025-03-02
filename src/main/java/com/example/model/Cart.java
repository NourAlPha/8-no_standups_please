package com.example.model;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Component
public class Cart implements Identifiable {
    private UUID id;
    private UUID userId;
    private List<Product> products = new ArrayList<>();

    public Cart() {
        this.id = UUID.randomUUID();
    }

    public Cart(final UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(final UUID userId) {
        this.userId = userId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(final List<Product> products) {
        this.products = products;
    }

    public void addProduct(final Product product) {
        this.products.add(product);
    }

    public void removeProduct(final Product product) {
        this.products.remove(product);
    }
}
