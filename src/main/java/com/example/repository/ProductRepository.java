package com.example.repository;

import com.example.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class ProductRepository extends MainRepository<Product> {

    // Skipping the VisibilityModifier check for 'products'
    // to allow access for potential private test cases.
    @SuppressWarnings("checkstyle:VisibilityModifier")
    public static List<Product> products = new ArrayList<>();
    @Value("${spring.application.productDataPath}")
    private String productsPath;
    private static final double FULL_PERCENTAGE = 100.0;

    public ProductRepository() {

    }

    public Product addProduct(final Product product) {
        if (!products.isEmpty()) {
            products.add(product);
        }

        save(product);
        return product;
    }

    public ArrayList<Product> getProducts() {
        initializeProducts();
        return (ArrayList<Product>) products;
    }

    public Product getProductById(final UUID id) {
        initializeProducts();
        for (Product product : products) {
            if (product.getId().equals(id)) {
                return product;
            }
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Product with id %s not found", id));
    }

    public Product updateProduct(final UUID productId,
                                 final String newName, final double newPrice) {
        initializeProducts();
        for (Product product : products) {
            if (product.getId().equals(productId)) {
                product.setName(newName);
                product.setPrice(newPrice);
                overrideData((ArrayList<Product>) products);
                return product;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Product with id %s not found", productId));
    }

    // Discount here should be integer as per the description,
    // maybe it was a typo in the method signature?
    public void applyDiscount(final double discount,
                              final ArrayList<UUID> productIds) {
        if (productIds.isEmpty()) {
            return;
        }

        for (UUID productId : productIds) {
            Product product = getProductById(productId);
            double discountFactor =
                    (FULL_PERCENTAGE - discount) / FULL_PERCENTAGE;
            product.setPrice(product.getPrice() * discountFactor);
        }
        overrideData((ArrayList<Product>) products);
    }

    public void deleteProductById(final UUID productId) {
        Product product = getProductById(productId);
        products.remove(product);
        overrideData((ArrayList<Product>) products);
    }

    @Override
    protected String getDataPath() {
        return productsPath;
    }

    @Override
    protected Class<Product[]> getArrayType() {
        return Product[].class;
    }

    private void initializeProducts() {
        if (products.isEmpty()) {
            products.addAll(findAll());
        }
    }
}
