package com.example.model;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Product implements Identifiable {
    private UUID id;
    private String name;
    private double price;

    public Product() {
        this.id = UUID.randomUUID();
    }

    public Product(final String name, final double price) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.price = price;
    }

    public Product(final UUID id, final String name, final double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(final double price) {
        this.price = price;
    }
}
