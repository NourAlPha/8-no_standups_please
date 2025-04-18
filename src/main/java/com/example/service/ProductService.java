package com.example.service;

import com.example.exception.InvalidActionException;
import com.example.exception.ValidationException;
import com.example.model.Product;
import com.example.repository.CartRepository;
import com.example.repository.OrderRepository;
import com.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@SuppressWarnings("rawtypes")
public class ProductService extends MainService<Product, ProductRepository> {

    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private static final double FULL_PERCENTAGE = 100.0;

    @Autowired
    public ProductService(final ProductRepository productRepository,
                          final CartRepository cartRepository,
                          final OrderRepository orderRepository) {
        super(productRepository);
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }

    public Product addProduct(final Product product) {
        return addObject(product);
    }

    public ArrayList<Product> getProducts() {
        return getObjects();
    }

    public Product getProductById(final UUID productId) {
        return getObjectById(productId);
    }

    public Product updateProduct(final UUID productId, final String newName,
                                 final double newPrice) {
        if (newPrice < 0) {
            throw new InvalidActionException("Price cannot be negative");
        }

        Product updatedProduct = productRepository.updateProduct(productId,
                newName, newPrice);

        // Ensure the new price is reflected in all carts
        cartRepository.updateProductInAllCarts(productId, newPrice);

        return updatedProduct;
    }

    public void applyDiscount(final double discount,
                              final ArrayList<UUID> productIds) {
        if (discount < 0 || discount > FULL_PERCENTAGE) {
            throw new InvalidActionException(
                    "Discount must be between 0 and 100");
        }

        productRepository.applyDiscount(discount, productIds);

        // After applying the discount, update all carts that contain
        // these products
        for (UUID productId : productIds) {
            Product product = productRepository.getProductById(productId);
            cartRepository.updateProductInAllCarts(productId,
                    product.getPrice());
        }
    }

    public void deleteProductById(final UUID productId) {
        checkId(productId);
        if (productRepository.getProductById(productId) == null) {
            throw new ValidationException("Product not found");
        }
        if (orderRepository.isProductInOrder(productId)) {
            throw new InvalidActionException("Product is in active orders");
        }

        cartRepository.removeProductFromAllCarts(productId);

        productRepository.deleteProductById(productId);
    }
}
