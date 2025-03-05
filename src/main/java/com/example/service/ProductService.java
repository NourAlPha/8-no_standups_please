package com.example.service;

import com.example.model.Product;
import com.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class ProductService extends MainService<Product> {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product addProduct(final Product product) {
        return productRepository.addProduct(product);
    }

    public ArrayList<Product> getProducts() {
        return productRepository.getProducts();
    }

    public Product getProductById(final UUID productId) {
        return productRepository.getProductById(productId);
    }

    public Product updateProduct(final UUID productId, final String newName,
                                 final double newPrice) {
        return productRepository.updateProduct(productId, newName, newPrice);
    }

    public void applyDiscount(final double discount,
                              final ArrayList<UUID> productIds) {
        productRepository.applyDiscount(discount, new ArrayList<>(productIds));
    }

    public void deleteProductById(final UUID productId) {
        productRepository.deleteProductById(productId);
    }
}
