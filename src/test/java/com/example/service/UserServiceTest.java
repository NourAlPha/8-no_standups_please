package com.example.service;

import com.example.exception.InvalidActionException;
import com.example.exception.NotFoundException;
import com.example.exception.ValidationException;
import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;


class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartService cartService;


    @Mock
    private OrderService orderService;

    @InjectMocks
    private UserService userService;

    private User mockUser;
    @Mock
    private Cart mockCart;
    private Product mockProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User("John Doe");
        final double productPrice = 1000.0;
        mockProduct = new Product("Laptop", productPrice);
    }

    @Test
    void addUser_ShouldAddUserSuccessfully() {
        when(userRepository.addUser(mockUser)).thenReturn(mockUser);

        User result = userService.addUser(mockUser);

        assertNotNull(result);
        assertEquals(mockUser, result);
        verify(userRepository, times(1)).addUser(mockUser);
    }

    @Test
    void addUser_ShouldThrowExceptionForNullUser() {
        when(userRepository.addUser(mockUser)).thenThrow(
                new ValidationException("Object id cannot be null"));

        assertThrows(ValidationException.class,
                () -> userService.addUser(null));
        verify(userRepository, never()).addUser(any());
    }

    @Test
    void addUser_ShouldThrowExceptionForEmptyName() {
        User invalidUser = new User("");

        assertThrows(ValidationException.class,
                () -> userService.addUser(invalidUser));
        verify(userRepository, never()).addUser(any());
    }

    @Test
    void getUsers_ShouldReturnListOfUsers() {
        List<User> users = List.of(mockUser);
        when(userRepository.getObjects()).thenReturn(new ArrayList<>(users));

        List<User> result = userService.getUsers();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(users, result);
        verify(userRepository, times(1)).getObjects();
    }

    @Test
    void getUsers_ShouldReturnEmptyListWhenNoUsersExist() {
        when(userRepository.getObjects()).thenReturn(new ArrayList<>());

        List<User> result = userService.getUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).getObjects();
    }

    @Test
    void getUsers_ShouldHandleRepositoryError() {
        when(userRepository.getObjects()).
                thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> userService.getUsers());
        verify(userRepository,
                times(1)).getObjects();
    }

    @Test
    void getUserById_ShouldReturnUserWhenExists() {
        when(userRepository.getObjectById(mockUser.getId()))
                .thenReturn(mockUser);

        User result = userService.getUserById(mockUser.getId());


        assertNotNull(result);
        assertEquals(mockUser, result);
        verify(userRepository, times(1))
                .getObjectById(mockUser.getId());
    }

    @Test
    void getUserById_ShouldThrowExceptionForNonExistentUser() {
        when(userRepository.getObjectById(mockUser.getId()))
                .thenThrow(new NotFoundException(
                        String.format("id %s not found", mockUser.getId())));

        assertThrows(NotFoundException.class,
                () -> userService.getUserById(mockUser.getId()));
        verify(userRepository, times(1)).
                getObjectById(mockUser.getId());
    }

    @Test
    void getUserById_ShouldHandleInvalidUUID() {
        when(userRepository.getObjectById(null))
                .thenThrow(new ValidationException("id cannot be null"));

        assertThrows(ValidationException.class,
                () -> userService.getUserById(null));
        verify(userRepository, never()).getObjectById(any());
    }

    @Test
    void getOrdersByUserId_ShouldReturnEmptyListOfOrders() {
        // Arrange
        when(userRepository.getOrdersByUserId(mockUser.getId())).thenReturn(new ArrayList<>());

        // Act
        List<Order> result = userService.getOrdersByUserId(mockUser.getId());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).getOrdersByUserId(mockUser.getId());
    }

    @Test
    void getOrdersByUserId_ShouldThrowExceptionForNonExistentUser() {
        // Arrange
        when(userRepository.getOrdersByUserId(mockUser.getId())).thenThrow(NotFoundException.class);

        // Act & Assert
        assertThrows(NotFoundException.class,
                () -> userService.getOrdersByUserId(mockUser.getId()));
        verify(userRepository, times(1)).getOrdersByUserId(mockUser.getId());
    }

    @Test
    void getOrdersByUserId_ShouldHandleInvalidUUID() {
        assertThrows(ValidationException.class,
                () -> userService.getOrdersByUserId(null));
        verify(userRepository, never()).getOrdersByUserId(any());
    }

    @Test
    void addOrderToUser_ShouldAddOrderSuccessfully() {
        // Arrange
        when(userRepository.getObjectById(mockUser.getId()))
                .thenReturn(mockUser);
        when(cartService.getCartByUserId(mockUser.getId()))
                .thenReturn(mockCart);
        final double productPrice = 1000.0;
        when(mockCart.getProducts()).thenReturn(List.of(mockProduct));
        when(cartService.emptyCart(mockUser.getId())).thenReturn(productPrice);

        ArrayList<User> mockUsers = new ArrayList<>();
        mockUsers.add(mockUser);
        when(userRepository.getObjectsArray()).thenReturn(mockUsers);

        userService.addOrderToUser(mockUser.getId());

        verify(userRepository, times(1)).addOrderToUser(any(),any());
        verify(orderService, times(1)).addOrder(any(Order.class));
    }


    @Test
    void addOrderToUser_ShouldThrowExceptionForNonExistentUser() {
        when(userRepository.getObjectById(mockUser.getId()))
                .thenThrow(new NotFoundException(
                        String.format("id %s not found", mockUser.getId())));

        assertThrows(NotFoundException.class,
                () -> userService.addOrderToUser(mockUser.getId()));
        verify(cartService, never()).getCartByUserId(any());
    }

    @Test
    void addOrderToUser_ShouldHandleEmptyCart() {
        // Arrange
        when(userRepository.getObjectById(mockUser.getId()))
                .thenReturn(mockUser);
        when(cartService.getCartByUserId(mockUser.getId()))
                .thenReturn(mockCart);
        final double productPrice = 0.0;
        when(cartService.emptyCart(mockUser.getId())).thenReturn(productPrice);

        // Now this works because mockCart is a Mockito mock
        when(mockCart.getProducts()).thenReturn(List.of());

        // Act & Assert
        assertThrows(InvalidActionException.class,
                () -> userService.addOrderToUser(mockUser.getId()));
        verify(orderService, never()).addOrder(any());
    }

    @Test
    void emptyCart_ShouldEmptyCartSuccessfully() {
        // Arrange
        when(userRepository.getUserById(mockUser.getId()))
                .thenReturn(mockUser);
        when(cartService.getCartByUserId(mockUser.getId()))
                .thenReturn(mockCart);
        final double productPrice = 1000.0;
        when(cartService.emptyCart(mockUser.getId())).thenReturn(productPrice);

        // Act
        userService.emptyCart(mockUser.getId());

        Cart result = cartService.getCartByUserId(mockUser.getId());
        // Assert
        assertNotNull(result);
        assertTrue(result.getProducts().isEmpty());
        verify(cartService, times(1)).emptyCart(mockUser.getId());
    }

    @Test
    void emptyCart_ShouldThrowExceptionForNonExistentUser() {
        when(userRepository.getUserById(mockUser.getId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
                () -> userService.emptyCart(mockUser.getId()));
        verify(cartService, never()).emptyCart(any());
    }

    @Test
    void emptyCart_ShouldHandleInvalidUUID() {
        assertThrows(ValidationException.class,
                () -> userService.emptyCart(null));
        verify(cartService, never()).emptyCart(any());
    }

    @Test
    void removeOrder_ShouldRemoveOrderSuccessfully() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(userRepository.getUserById(mockUser.getId()))
                .thenReturn(mockUser);

        // Act
        userService.removeOrderFromUser(mockUser.getId(), orderId);

        // Assert
        verify(userRepository, times(1))
                .removeOrderFromUser(mockUser.getId(), orderId);
    }

    @Test
    void removeOrder_ShouldThrowExceptionForNonExistentUser() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        doThrow(NotFoundException.class)
                .when(userRepository)
                .removeOrderFromUser(mockUser.getId(), orderId);

        // Act & Assert
        assertThrows(NotFoundException.class,
                () -> userService.removeOrderFromUser(mockUser.getId(), orderId));
        verify(userRepository, times(1))
                .removeOrderFromUser(mockUser.getId(), orderId);
    }

    @Test
    void removeOrder_ShouldHandleInvalidUUID() {
        assertThrows(ValidationException.class,
                () -> userService.removeOrderFromUser(null, null));
        verify(userRepository, never())
                .removeOrderFromUser(any(), any());
    }


    @Test
    void deleteUserById_ShouldDeleteUserSuccessfully() {
        userService.deleteUserById(mockUser.getId());

        verify(userRepository, times(1))
                .deleteObjectById(mockUser.getId());
    }

    @Test
    void deleteUserById_ShouldThrowExceptionForNonExistentUser() {
        // Arrange
        UUID randomUserId = UUID.randomUUID();
        doThrow(new NotFoundException("User not found"))
                .when(userRepository)
                .deleteObjectById(randomUserId);

        // Act & Assert
        assertThrows(NotFoundException.class,
                () -> userService.deleteUserById(randomUserId));
        verify(userRepository, times(1))
                .deleteObjectById(randomUserId);
    }

    @Test
    void deleteUserById_ShouldHandleInvalidUUID() {
        doThrow(new ValidationException("User ID cannot be null"))
                .when(userRepository).deleteObjectById(null);

        assertThrows(ValidationException.class,
                () -> userService.deleteUserById(null));
        verify(userRepository, never())
                .deleteObjectById(any());
    }
}