package com.example.service;

import com.example.exception.InvalidActionException;
import com.example.exception.ValidationException;
import com.example.model.Cart;
import com.example.model.Order;
import com.example.model.Product;
import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@SuppressWarnings("checkstyle:GenericWhitespace")
public class UserService extends MainService<User, UserRepository> {
    private final UserRepository userRepository;
    private final CartService cartService;
    private final ProductService productService;
    private final OrderService orderService;

    @Autowired
    public UserService(final UserRepository userRepository,
                       final CartService cartService,
                       final ProductService productService,
                       final OrderService orderService) {
        super(userRepository);
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.productService = productService;
        this.orderService = orderService;
    }

    public User addUser(final User user) {
        checkObject(user);
        checkId(user.getId());
        if (user.getName().trim().isEmpty()) {
            throw new ValidationException("User name cannot be empty");
        }
        return userRepository.addUser(user);
    }

    public ArrayList<User> getUsers() {
        return getObjects();
    }

    public User getUserById(final UUID userId) {
        return getObjectById(userId);
    }

    public List<Order> getOrdersByUserId(final UUID userId) {
        checkId(userId);
        List<Order> orders = userRepository.getOrdersByUserId(userId);
        if (orders == null) {
            throw new InvalidActionException("User has no orders: " + userId);
        }
        return orders;
    }


    public void removeOrderFromUser(final UUID userId, final UUID orderId) {
        userRepository.removeOrderFromUser(userId, orderId);
    }

    public void deleteUserById(final UUID userId) {
        deleteObjectById(userId);
    }

    public void addOrderToUser(final UUID userId) {
        User user = getUserById(userId);
        Cart cart = cartService.getCartByUserId(userId);
        List<Product> products = cart.getProducts();
        if (products.isEmpty()) {
            throw new InvalidActionException("Cart"
                    + " is empty. Cannot create an order.");
        }
        double totalPrice = cartService.emptyCart(userId);
        Order order = new Order(userId, totalPrice, products);
        user.addOrder(order);
        userRepository.saveAll(userRepository.getObjectsArray());
        orderService.addOrder(order);
    }

    public void emptyCart(final UUID userId) {
        cartService.emptyCart(userId);
    }

    public void addProductToCart(final UUID userId, final UUID productId) {
        Cart cart = cartService.getCartByUserId(userId);
        Product product = productService.getProductById(productId);
        cartService.addProductToCart(cart.getId(), product);
    }

    public String deleteProductFromCart(final UUID userId,
                                       final UUID productId) {
        Cart cart = cartService.getCartByUserId(userId);
        Product product = productService.getProductById(productId);
        cartService.deleteProductFromCart(cart.getId(), product);
        if (cart.getProducts().isEmpty()) {
            return "Cart is empty";
        } else {
            return "Product deleted from cart";
        }
    }
}
