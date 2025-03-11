package com.example.service;

import com.example.model.Cart;
import com.example.model.Product;
import com.example.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setTests() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void givenValidCart_whenAddCart_thenCartIsSaved() {
        // Given
        Cart cart = new Cart(UUID.randomUUID());
        when(cartRepository.addCart(cart)).thenReturn(cart);

        // When
        Cart savedCart = cartService.addCart(cart);

        // Then
        assertNotNull(savedCart);
        assertEquals(cart, savedCart);
        verify(cartRepository, times(1)).addCart(cart);
    }

    @Test
    void givenNullCart_whenAddCart_thenThrowException() {
        // Given
        Cart cart = null;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> cartService.addCart(cart));
        verify(cartRepository, never()).addCart(any());
    }

    @Test
    void givenCartWithExistingId_whenAddCart_thenThrowException() {
        // Given
        UUID userId = UUID.randomUUID();
        Cart existingCart = new Cart(userId);

        // When
        when(cartRepository.getCartByUserId(userId)).thenReturn(existingCart);

        // Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            cartService.addCart(existingCart);
        });

        assertEquals("A cart already exists for user ID: " + userId, exception.getMessage());

        verify(cartRepository, never()).addCart(existingCart);
    }


    @Test
    void givenCartsExist_whenGetAllCarts_thenReturnAllCarts() {
        // Given
        List<Cart> carts = new ArrayList<>();
        carts.add(new Cart(UUID.randomUUID()));
        carts.add(new Cart(UUID.randomUUID()));
        when(cartRepository.getCarts()).thenReturn((ArrayList<Cart>) carts);

        // When
        List<Cart> result = cartService.getCarts();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(cartRepository, times(1)).getCarts();
    }

    @Test
    void givenNoCartsExist_whenGetAllCarts_thenReturnEmptyList() {
        // Given
        when(cartRepository.getCarts()).thenReturn(new ArrayList<>());

        // When
        List<Cart> result = cartService.getCarts();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(cartRepository, times(1)).getCarts();
    }

    @Test
    void givenRepositoryReturnsNull_whenGetAllCarts_thenThrowsException() {
        // Given
        when(cartRepository.getCarts()).thenReturn(null);

        // When & Then
        assertThrows(IllegalStateException.class, () -> cartService.getCarts());
        verify(cartRepository, times(1)).getCarts();
    }

    @Test
    void givenValidCartId_whenGetCartById_thenReturnCart() {
        // Given
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart(cartId);
        when(cartRepository.getCartById(cartId)).thenReturn(cart);

        // When
        Cart result = cartService.getCartById(cartId);

        // Then
        assertNotNull(result);
        assertEquals(cart, result);
        verify(cartRepository, times(1)).getCartById(cartId);
    }

    @Test
    void givenInvalidCartId_whenGetCartById_thenThrowException() {
        // Given
        UUID invalidCartId = UUID.randomUUID();
        when(cartRepository.getCartById(invalidCartId)).thenReturn(null);

        // When & Then
        assertThrows(IllegalStateException.class, () -> cartService.getCartById(invalidCartId));

    }

    @Test
    void givenRepositoryReturnsNull_whenGetCartById_thenThrowsException() {
        // Given
        UUID cartId = UUID.randomUUID();
        when(cartRepository.getCartById(cartId)).thenReturn(null);

        // When & Then
        assertThrows(IllegalStateException.class, () -> cartService.getCartById(cartId));
        verify(cartRepository, times(1)).getCartById(cartId);
    }

    @Test
    void givenValidCartIdAndProduct_whenAddProductToCart_thenProductIsAdded() {
        // Given
        UUID cartId = UUID.randomUUID();
        Product product = new Product("Product 1", 10.0);
        Cart cart = new Cart(cartId);

        when(cartRepository.getCartById(cartId)).thenReturn(cart);
        doNothing().when(cartRepository).addProductToCart(cartId, product);

        // When
        cartService.addProductToCart(cartId, product);

        // Then
        verify(cartRepository, times(1)).getCartById(cartId);
        verify(cartRepository, times(1)).addProductToCart(cartId, product);
    }

    @Test
    void givenInvalidCartId_whenAddProductToCart_thenThrowException() {
        // Given
        UUID invalidCartId = UUID.randomUUID();
        Product product = new Product("Product 1", 10.0);
        doThrow(new IllegalStateException("Cart not found")).when(cartRepository).addProductToCart(invalidCartId, product);

        // When & Then
        assertThrows(IllegalStateException.class, () -> cartService.addProductToCart(invalidCartId, product));
    }

    @Test
    void givenNullProduct_whenAddProductToCart_thenThrowException() {
        // Given
        UUID cartId = UUID.randomUUID();
        Product product = null;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> cartService.addProductToCart(cartId, product));
        verify(cartRepository, never()).addProductToCart(any(), any());
    }
}