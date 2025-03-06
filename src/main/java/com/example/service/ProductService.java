package com.example.service;

import com.example.model.Product;
import com.example.repository.CartRepository;
import com.example.repository.OrderRepository;
import com.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class ProductService extends MainService<Product> {

    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public ProductService(final ProductRepository productRepository,
                          final CartRepository cartRepository,
                          final OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
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
        Product updatedProduct = productRepository.updateProduct(productId,
                newName, newPrice);

        // Ensure the new price is reflected in all carts
        cartRepository.updateProductInCart(productId, newPrice);

        return updatedProduct;
    }

    public void applyDiscount(final double discount,
                              final ArrayList<UUID> productIds) {
        productRepository.applyDiscount(discount, productIds);

        // After applying the discount, update all carts that contain
        // these products
        for (UUID productId : productIds) {
            Product product = productRepository.getProductById(productId);
            cartRepository.updateProductInCart(productId, product.getPrice());
        }
    }

    public void deleteProductById(final UUID productId) {
        //Check if the product exists in any active orders
        //If it does, throw an exception
        if (orderRepository.isProductInOrder(productId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Product is in active orders");
        }

        // Delete the product from all carts that contain
        // this product
        cartRepository.removeProductFromAllCarts(productId);

        productRepository.deleteProductById(productId);
    }
}
