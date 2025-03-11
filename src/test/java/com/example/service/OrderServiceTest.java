package com.example.service;

import com.example.exception.NotFoundException;
import com.example.exception.ValidationException;
import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.OrderRepository;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;

public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    private static final int ONE = 1;
    private static final int THREE = 3;
    private static final int HUNDRED = 100;
    private static final int TWO_HUNDRED = 200;
    private static final int THREE_HUNDRED = 300;

    private static final User USER =
            new User("Ahmed");

    private static final Product PRODUCT_1 =
            new Product("Laptop", HUNDRED);
    private static final Product PRODUCT_2 =
            new Product("Phone", TWO_HUNDRED);
    private static final Product PRODUCT_3 =
            new Product("Tablet", THREE_HUNDRED);

    private static final Cart CART_1 = new Cart(USER.getId());
    private static final Cart CART_2 = new Cart(USER.getId());
    private static final Cart CART_3 = new Cart(USER.getId());

    private static final Order ORDER_1 =
            new Order(USER.getId(), HUNDRED, CART_1.getProducts());
    private static final Order ORDER_2 =
            new Order(USER.getId(), TWO_HUNDRED, CART_2.getProducts());
    private static final Order ORDER_3 =
            new Order(USER.getId(), THREE_HUNDRED, CART_3.getProducts());

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        CART_1.addProduct(PRODUCT_1);
        CART_2.addProduct(PRODUCT_2);
        CART_3.addProduct(PRODUCT_3);
    }

    @Test
    public void addOrder_ValidOrder_Success() {
        when(userRepository.getUserById(USER.getId())).thenReturn(USER);
        doNothing().when(orderRepository).addOrder(ORDER_1);

        orderService.addOrder(ORDER_1);
        verify(orderRepository, times(ONE)).addOrder(ORDER_1);
    }

    @Test
    public void addOrder_NullUserId_ExceptionThrown() {
        Order order = new Order(null, HUNDRED, CART_1.getProducts());

        assertThrows(ValidationException.class,
                () -> orderService.addOrder(order));
        verify(orderRepository, never()).addOrder(any());
    }

    @Test
    public void addOrder_MissingUserId_Success() {
        Order order = new Order(
                UUID.randomUUID(), HUNDRED, CART_1.getProducts());
        doNothing().when(orderRepository).addOrder(order);

        orderService.addOrder(order);

        verify(orderRepository, times(ONE)).addOrder(order);
    }

    @Test
    public void addOrder_EmptyProducts_Success() {
        Order order = new Order(USER.getId(), HUNDRED, new ArrayList<>());
        doNothing().when(orderRepository).addOrder(order);

        orderService.addOrder(order);

        verify(orderRepository, times(ONE)).addOrder(order);
    }

    @Test
    public void getOrderById_ValidOrderId_Success() {
        when(orderRepository.getObjectById(ORDER_1.getId()))
                .thenReturn(ORDER_1);

        orderService.getOrderById(ORDER_1.getId());
        verify(orderRepository, times(ONE)).getObjectById(ORDER_1.getId());
    }

    @Test
    public void getOrderById_NullId_Success() {
        assertThrows(ValidationException.class,
                () -> orderService.getOrderById(null));
        verify(orderRepository, never()).getObjectById(any());
    }

    @Test
    public void getOrderById_InvalidOrderId_ExceptionThrown() {
        Order randomOrder =
                new Order(UUID.randomUUID(), HUNDRED, CART_1.getProducts());
        when(orderRepository.getObjectById(randomOrder.getId()))
                .thenThrow(new NotFoundException(
                        String.format("id %s not found", randomOrder.getId())));

        assertThrows(NotFoundException.class,
                () -> orderService.getOrderById(randomOrder.getId()));
        verify(orderRepository, times(ONE)).getObjectById(randomOrder.getId());
    }

    @Test
    public void getOrders_EmptyOrders_Success() {
        when(orderRepository.getObjects()).thenReturn(new ArrayList<>());

        List<Order> orders = orderService.getOrders();

        assertTrue(orders.isEmpty());
        verify(orderRepository, times(ONE)).getObjects();
    }

    @Test
    public void getOrders_OrdersExist_Success() {
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(ORDER_1);
        orders.add(ORDER_2);
        orders.add(ORDER_3);

        when(orderRepository.getObjects()).thenReturn(orders);

        List<Order> actualOrders = orderService.getOrders();

        assertEquals(THREE, actualOrders.size());
        verify(orderRepository, times(ONE)).getObjects();
    }

    @Test
    public void getOrders_RepositoryError_ExceptionThrown() {
        when(orderRepository.getObjects())
                .thenThrow(new RuntimeException("Error"));

        assertThrows(RuntimeException.class,
                () -> orderService.getOrders());
        verify(orderRepository, times(ONE)).getObjects();
    }

    @Test
    public void deleteOrderById_ValidOrderId_Success() {
        when(orderRepository.getOrderById(ORDER_1.getId()))
                .thenReturn(ORDER_1);
        doNothing().when(orderRepository).deleteOrderById(ORDER_1.getId());

        orderService.deleteOrderById(ORDER_1.getId());
        verify(orderRepository, times(ONE)).deleteOrderById(ORDER_1.getId());
    }

    @Test
    public void deleteOrderById_NonExistentId_Success() {
        Order randomOrder =
                new Order(UUID.randomUUID(), HUNDRED, CART_1.getProducts());
        doNothing().when(orderRepository).deleteOrderById(randomOrder.getId());

        orderService.deleteOrderById(randomOrder.getId());

        verify(orderRepository, times(ONE)).deleteOrderById(randomOrder.getId());
    }

    @Test
    public void deleteOrderById_NullId_ExceptionThrown() {
        assertThrows(ValidationException.class,
                () -> orderService.deleteOrderById(null));
        verify(orderRepository, never()).deleteOrderById(any());
    }
}
