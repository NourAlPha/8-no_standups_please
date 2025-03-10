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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ProductTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setTests() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void givenNullProductId_whenDeleteProductById_thenThrowBadRequest() {
        // Given
        UUID productId = null;

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productService.deleteProductById(productId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Product ID cannot be null", exception.getReason());

    }

    @Test
    void givenNonExistentProductId_whenDeleteProductById_thenThrowNotFound() {
        // Given
        UUID productId = UUID.randomUUID();
        when(productRepository.getProductById(productId)).thenReturn(null);

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productService.deleteProductById(productId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Product not found with ID: " + productId, exception.getReason());

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