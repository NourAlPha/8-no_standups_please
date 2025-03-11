package com.example.MiniProject1;

import com.example.model.Product;
import com.example.repository.CartRepository;
import com.example.repository.OrderRepository;
import com.example.repository.ProductRepository;
import com.example.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceUnitTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ProductService productService;

    private static final double TEST_PRODUCT_PRICE = 100.0;
    private static final double UPDATED_PRODUCT_PRICE = 80.0;
    private static final double INVALID_PRODUCT_PRICE = -10.0;
    private UUID productId;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productId = UUID.randomUUID();
        product = new Product("Test Product", TEST_PRODUCT_PRICE);
    }

    // **Tests for addProduct**
    @Test
    void testAddProductSuccessfully() {
        when(productRepository.addProduct(any())).thenReturn(product);
        Product result = productService.addProduct(product);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals(TEST_PRODUCT_PRICE, result.getPrice());
    }

    @Test
    void testAddProductWithEmptyName() {
        Product invalidProduct = new Product("", TEST_PRODUCT_PRICE);
        when(productRepository.addProduct(any())).thenThrow(
                new IllegalArgumentException("Invalid name"));

        assertThrows(IllegalArgumentException.class, ()
                -> productService.addProduct(invalidProduct));
    }

    @Test
    void testAddProductWithNegativePrice() {
        Product invalidProduct = new Product("Invalid Product",
                INVALID_PRODUCT_PRICE);
        when(productRepository.addProduct(any())).thenThrow(
                new IllegalArgumentException("Invalid price"));

        assertThrows(IllegalArgumentException.class, ()
                -> productService.addProduct(invalidProduct));
    }

    // **Tests for getProducts**
    @Test
    void testGetProductsSuccessfully() {
        ArrayList<Product> products = new ArrayList<>();
        products.add(product);
        when(productRepository.getProducts()).thenReturn(products);

        ArrayList<Product> result = productService.getProducts();
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetProductsEmptyList() {
        when(productRepository.getProducts()).thenReturn(new ArrayList<>());

        ArrayList<Product> result = productService.getProducts();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetProductsReturnsNull() {
        when(productRepository.getProducts()).thenReturn(null);

        assertNull(productService.getProducts());
    }

    // **Tests for getProductById**
    @Test
    void testGetProductByIdSuccessfully() {
        when(productRepository.getProductById(productId)).thenReturn(product);

        Product result = productService.getProductById(productId);
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productRepository.getProductById(productId)).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> {
            productService.getProductById(productId);
        });
    }

    @Test
    void testGetProductByInvalidId() {
        UUID invalidId = null;

        assertThrows(ResponseStatusException.class, ()
                -> productService.getProductById(invalidId));
    }

    // **Tests for updateProduct**
    @Test
    void testUpdateProductSuccessfully() {
        Product updatedProduct = new Product("Updated Product",
                UPDATED_PRODUCT_PRICE);
        when(productRepository.updateProduct(eq(productId), anyString(),
                anyDouble())).thenReturn(updatedProduct);

        Product result = productService.updateProduct(productId,
                "Updated Product", UPDATED_PRODUCT_PRICE);
        assertNotNull(result);
        assertEquals("Updated Product", result.getName());
        assertEquals(UPDATED_PRODUCT_PRICE, result.getPrice());

        verify(cartRepository, times(1)).updateProductInAllCarts(productId,
                UPDATED_PRODUCT_PRICE);
    }

    @Test
    void testUpdateNonExistingProduct() {
        when(productRepository.updateProduct(
                any(), any(), anyDouble())).thenReturn(null);

        Product result = productService.updateProduct(productId,
                "Updated Product", UPDATED_PRODUCT_PRICE);
        assertNull(result);
    }

    @Test
    void testUpdateProductWithInvalidData() {
        assertThrows(ResponseStatusException.class, ()
                -> productService.updateProduct(productId, "",
                INVALID_PRODUCT_PRICE));
    }

    // **Tests for applyDiscount**
    @Test
    void testApplyDiscountSuccessfully() {
        ArrayList<UUID> productIds = new ArrayList<>();
        productIds.add(productId);
        when(productRepository.getProductById(productId)).thenReturn(product);

        productService.applyDiscount(UPDATED_PRODUCT_PRICE, productIds);

        verify(productRepository, times(1)).applyDiscount(UPDATED_PRODUCT_PRICE,
                productIds);
        verify(cartRepository, times(1)).updateProductInAllCarts(productId,
                product.getPrice());
    }

    @Test
    void testApplyDiscountEmptyList() {
        ArrayList<UUID> productIds = new ArrayList<>();

        productService.applyDiscount(UPDATED_PRODUCT_PRICE, productIds);

        verify(productRepository, times(1)).applyDiscount(UPDATED_PRODUCT_PRICE,
                productIds);
    }

    @Test
    void testApplyInvalidDiscount() {
        ArrayList<UUID> productIds = new ArrayList<>();
        productIds.add(productId);

        assertThrows(ResponseStatusException.class, ()
                -> productService.applyDiscount(INVALID_PRODUCT_PRICE,
                productIds));
    }
}
