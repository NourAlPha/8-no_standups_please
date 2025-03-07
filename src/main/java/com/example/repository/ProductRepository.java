package com.example.repository;

import com.example.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class ProductRepository extends GenericRepository<Product> {

    @Value("${spring.application.productDataPath}")
    private String productsPath;
    private static final double FULL_PERCENTAGE = 100.0;

    public ProductRepository() {

    }

    public Product addProduct(final Product product) {
        return addObject(product);
    }

    public ArrayList<Product> getProducts() {
        return getObjects();
    }

    public Product getProductById(final UUID id) {
        return getObjectById(id);
    }

    public Product updateProduct(final UUID productId,
                                 final String newName, final double newPrice) {
        ArrayList<Product> products = getProducts();
        for (Product product : products) {
            if (product.getId().equals(productId)) {
                product.setName(newName);
                product.setPrice(newPrice);
                overrideData(products);
                return product;
            }
        }
        return null;
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
        overrideData(getProducts());
    }

    public void deleteProductById(final UUID productId) {
        deleteObjectById(productId);
    }

    @Override
    protected String getDataPath() {
        return productsPath;
    }

    @Override
    protected Class<Product[]> getArrayType() {
        return Product[].class;
    }
}
