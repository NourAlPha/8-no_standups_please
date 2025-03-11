package com.example.service;

import com.example.exception.InvalidActionException;
import com.example.exception.NotFoundException;
import com.example.exception.ValidationException;
import com.example.model.Product;
import com.example.repository.CartRepository;
import com.example.repository.OrderRepository;
import com.example.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

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
        when(productRepository.addObject(any())).thenReturn(product);
        Product result = productService.addProduct(product);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals(TEST_PRODUCT_PRICE, result.getPrice());
    }

    @Test
    void testAddProductWithEmptyName() {
        Product invalidProduct = new Product("", TEST_PRODUCT_PRICE);
        when(productRepository.addObject(any())).thenThrow(
                new RuntimeException("Invalid name"));

        assertThrows(RuntimeException.class, ()
                -> productService.addProduct(invalidProduct));
    }

    @Test
    void testAddProductWithNegativePrice() {
        Product invalidProduct = new Product("Invalid Product",
                INVALID_PRODUCT_PRICE);
        when(productRepository.addObject(any())).thenThrow(
                new RuntimeException("Invalid price"));

        assertThrows(RuntimeException.class, ()
                -> productService.addProduct(invalidProduct));
    }

    // **Tests for getProducts**
    @Test
    void testGetProductsSuccessfully() {
        ArrayList<Product> products = new ArrayList<>();
        products.add(product);
        when(productRepository.getObjects()).thenReturn(products);

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
        when(productRepository.getObjects()).thenReturn(null);

        assertNull(productService.getProducts());
    }

    // **Tests for getProductById**
    @Test
    void testGetProductByIdSuccessfully() {
        when(productRepository.getObjectById(productId)).thenReturn(product);

        Product result = productService.getProductById(productId);
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productRepository.getObjectById(productId)).thenThrow(
                NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
            productService.getProductById(productId);
        });
    }

    @Test
    void testGetProductByInvalidId() {
        UUID invalidId = null;

        assertThrows(ValidationException.class, ()
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
        assertThrows(InvalidActionException.class, ()
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

        assertThrows(InvalidActionException.class, ()
                -> productService.applyDiscount(INVALID_PRODUCT_PRICE,
                productIds));
    }


    @Test
    void givenNullProductId_whenDeleteProductById_thenThrowBadRequest() {
        // Given
        UUID productId = null;

        // When & Then
        assertThrows(ValidationException.class, () -> {
            productService.deleteProductById(productId);
        });

    }

    @Test
    void givenNonExistentProductId_whenDeleteProductById_thenThrowNotFound() {
        // Given
        UUID productId = UUID.randomUUID();
        when(productRepository.getProductById(productId)).thenThrow(NotFoundException.class);

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            productService.deleteProductById(productId);
        });

    }

    @Test
    void givenValidProductId_whenDeleteProductById_thenProductIsDeleted() {
        // Given

        Product product = new Product("Product 1", 10.0);
        UUID productId = product.getId();
        when(productRepository.getProductById(productId)).thenReturn(product);
        when(orderRepository.isProductInOrder(productId)).thenReturn(false);
        doNothing().when(cartRepository).removeProductFromAllCarts(productId);
        doNothing().when(productRepository).deleteProductById(productId);

        // When
        productService.deleteProductById(productId);

        // Then
        verify(productRepository, times(1)).getProductById(productId);
        verify(orderRepository, times(1)).isProductInOrder(productId);
        verify(cartRepository, times(1)).removeProductFromAllCarts(productId);
        verify(productRepository, times(1)).deleteProductById(productId);
    }
}