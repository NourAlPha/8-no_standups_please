package com.example.repository;

import com.example.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class ProductRepository extends MainRepository<Product> {

    //CHECKSTYLE:OFF
    public static List<Product> products = new ArrayList<>();
    //CHECKSTYLE:ON
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
        if (products.isEmpty()) {
            products.addAll(findAll());
        }
        return (ArrayList<Product>) products;
    }

    public Product getProductById(final UUID id) {
        if (products.isEmpty()) {
            products.addAll(findAll());
        }
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
        if (products.isEmpty()) {
            products.addAll(findAll());
        }
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
            product.setPrice(product.getPrice()
                    * (FULL_PERCENTAGE - discount) / FULL_PERCENTAGE);
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
        return "src/main/java/com/example/data/products.json";
    }

    @Override
    protected Class<Product[]> getArrayType() {
        return Product[].class;
    }
}
