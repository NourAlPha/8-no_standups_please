package com.example.service;

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
        verify(userRepository, times(ONE)).getUserById(USER.getId());
    }

    @Test
    public void addOrder_NullUserId_ExceptionThrown() {
        Order order = new Order(null, HUNDRED, CART_1.getProducts());

        assertExceptionAndVerifyNoInvocations(order);
    }

    @Test
    public void addOrder_MissingUserId_ExceptionThrown() {
        Order order = new Order(
                UUID.randomUUID(), HUNDRED, CART_1.getProducts());
        when(userRepository.getUserById(order.getUserId()))
                .thenThrow(new IllegalArgumentException(
                        String.format("id %s not found", order.getId())));

        assertThrows(IllegalArgumentException.class,
                () -> orderService.addOrder(order));
        verify(orderRepository, never()).addOrder(order);
        verify(userRepository, times(ONE)).getUserById(order.getUserId());
    }

    @Test
    public void addOrder_EmptyProducts_ExceptionThrown() {
        Order order = new Order(USER.getId(), HUNDRED, new ArrayList<>());

        assertExceptionAndVerifyNoInvocations(order);
    }

    @Test
    public void getOrderById_ValidOrderId_Success() {
        when(orderRepository.getOrderById(ORDER_1.getId())).thenReturn(ORDER_1);

        orderService.getOrderById(ORDER_1.getId());
        verify(orderRepository, times(ONE)).getOrderById(ORDER_1.getId());
    }

    @Test
    public void getOrderById_NullId_Success() {
        assertThrows(IllegalArgumentException.class,
                () -> orderService.getOrderById(null));
        verify(orderRepository, never()).getOrderById(any());
    }

    @Test
    public void getOrderById_InvalidOrderId_ExceptionThrown() {
        Order randomOrder = createNonExistentOrder();

        assertThrows(IllegalArgumentException.class,
                () -> orderService.getOrderById(randomOrder.getId()));
        verify(orderRepository, times(ONE)).getOrderById(randomOrder.getId());
    }

    @Test
    public void getOrders_EmptyOrders_Success() {
        when(orderRepository.getOrders()).thenReturn(new ArrayList<>());

        List<Order> orders = orderService.getOrders();

        assertTrue(orders.isEmpty());
        verify(orderRepository, times(ONE)).getOrders();
    }

    @Test
    public void getOrders_OrdersExist_Success() {
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(ORDER_1);
        orders.add(ORDER_2);
        orders.add(ORDER_3);

        when(orderRepository.getOrders()).thenReturn(orders);

        List<Order> actualOrders = orderService.getOrders();
        System.out.println(actualOrders);

        assertEquals(THREE, actualOrders.size());
        verify(orderRepository, times(ONE)).getOrders();
    }

    @Test
    public void getOrders_RepositoryError_ExceptionThrown() {
        when(orderRepository.getOrders())
                .thenThrow(new IllegalArgumentException("Error"));

        assertThrows(IllegalArgumentException.class,
                () -> orderService.getOrders());
        verify(orderRepository, times(ONE)).getOrders();
    }

    @Test
    public void deleteOrderById_ValidOrderId_Success() {
        when(orderRepository.getOrderById(ORDER_1.getId())).thenReturn(ORDER_1);
        doNothing().when(userRepository)
                .removeOrderFromUser(ORDER_1.getUserId(), ORDER_1.getId());
        doNothing().when(orderRepository).deleteOrderById(ORDER_1.getId());

        orderService.deleteOrderById(ORDER_1.getId());
        verify(orderRepository, times(ONE)).getOrderById(ORDER_1.getId());
        verify(userRepository, times(ONE))
                .removeOrderFromUser(ORDER_1.getUserId(), ORDER_1.getId());
        verify(orderRepository, times(ONE)).deleteOrderById(ORDER_1.getId());
    }

    @Test
    public void deleteOrderById_NonExistentId_ExceptionThrown() {
        Order randomOrder = createNonExistentOrder();

        assertThrows(IllegalArgumentException.class,
                () -> orderService.deleteOrderById(randomOrder.getId()));
    }

    @Test
    public void deleteOrderById_NullId_ExceptionThrown() {
        assertThrows(IllegalArgumentException.class,
                () -> orderService.deleteOrderById(null));
        verify(orderRepository, never()).deleteOrderById(any());
    }

    private Order createNonExistentOrder() {
        Order randomOrder =
                new Order(UUID.randomUUID(), HUNDRED, CART_1.getProducts());
        when(orderRepository.getOrderById(randomOrder.getId()))
                .thenThrow(new IllegalArgumentException(
                        String.format("id %s not found", randomOrder.getId())));
        return randomOrder;
    }

    private void assertExceptionAndVerifyNoInvocations(final Order order) {
        assertThrows(IllegalArgumentException.class,
                () -> orderService.addOrder(order));
        verify(orderRepository, never()).addOrder(order);
        verify(userRepository, never()).getUserById(any());
    }
}
