package com.example.controller;

import com.example.MiniProject1.MiniProject1Application;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest(classes = MiniProject1Application.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderControllerTest {

    private static final String ORDER_URL = "/order";
    private static final String USER_URL = "/user";
    private static final String PRODUCT_URL = "/product";
    private static final String CART_URL = "/cart";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final int HUNDRED = 100;
    private static final int TWO_HUNDRED = 200;
    private static final int THREE_HUNDRED = 300;

    private static final UserDTO USER = new UserDTO("Ahmed");

    private static final ProductDTO PRODUCT_1 =
            new ProductDTO("Laptop", HUNDRED);
    private static final ProductDTO PRODUCT_2 =
            new ProductDTO("Phone", TWO_HUNDRED);
    private static final ProductDTO PRODUCT_3 =
            new ProductDTO("Tablet", THREE_HUNDRED);

    private static final CartDTO CART_1 =
            new CartDTO(USER.getId(), List.of(PRODUCT_1, PRODUCT_2));
    private static final CartDTO CART_2 =
            new CartDTO(USER.getId(), List.of(PRODUCT_2, PRODUCT_3));
    private static final CartDTO CART_3 =
            new CartDTO(USER.getId(), List.of(PRODUCT_1, PRODUCT_3));

    private static final OrderDTO ORDER_1 =
            new OrderDTO(USER.getId(), HUNDRED, CART_1.getProducts());
    private static final OrderDTO ORDER_2 =
            new OrderDTO(USER.getId(), TWO_HUNDRED, CART_2.getProducts());
    private static final OrderDTO ORDER_3 =
            new OrderDTO(USER.getId(), THREE_HUNDRED, CART_3.getProducts());

    @BeforeAll
    public void setUp() throws Exception {
        createProducts();
        createUser();
    }

    @Test
    public void addOrder_ValidOrder_Returns200() throws Exception {
        addOrderExpectOk(ORDER_1);
    }

    @Test
    public void addOrder_AddingMissingUserId_Returns400()
            throws Exception {
        OrderDTO orderMissingUser =
                new OrderDTO(UUID.randomUUID(), HUNDRED, CART_1.getProducts());

        addOrderExpectBadRequest(orderMissingUser);
    }

    @Test
    public void addOrder_AddingNullUserId_Returns400() throws Exception {
        OrderDTO orderNullUser =
                new OrderDTO(null, TWO_HUNDRED, CART_2.getProducts());

        addOrderExpectBadRequest(orderNullUser);
    }

    @Test
    public void addOrder_AddingMissingProducts_Returns400()
            throws Exception {
        OrderDTO orderMissingProducts =
                new OrderDTO(USER.getId(), HUNDRED, null);

        addOrderExpectBadRequest(orderMissingProducts);
    }

    @Test
    public void getOrderById_ValidId_ReturnsOrder200() throws Exception {
        addOrderExpectOk(ORDER_1);

        getOrderExpectSameId(ORDER_1);
    }

    @Test
    public void getOrderById_ValidIdAmongIds_ReturnsOrder200()
            throws Exception {
        addOrderExpectOk(ORDER_1);
        addOrderExpectOk(ORDER_2);
        addOrderExpectOk(ORDER_3);

        getOrderExpectSameId(ORDER_2);
    }

    @Test
    public void getOrderById_InvalidId_Returns404() throws Exception {
        OrderDTO randomOrder = new OrderDTO(
                USER.getId(), HUNDRED, CART_1.getProducts());
        getOrderExpectNotFound(randomOrder);
    }

    @Test
    public void getOrders_EmptyOrders_ReturnsEmptyList200() throws Exception {
        getOrdersExpectLength(ZERO);
    }

    @Test
    public void getOrders_Orders_ReturnsOrders200() throws Exception {
        addOrderExpectOk(ORDER_1);
        addOrderExpectOk(ORDER_2);
        addOrderExpectOk(ORDER_3);

        getOrdersExpectLength(THREE);
    }

    @Test
    public void getOrders_SequentiallyCheckingOrders_OrdersListIsUpToDate()
            throws Exception {
        getOrdersExpectLength(ZERO);

        addOrderExpectOk(ORDER_1);
        getOrdersExpectLength(ONE);

        addOrderExpectOk(ORDER_2);
        getOrdersExpectLength(TWO);

        addOrderExpectOk(ORDER_3);
        getOrdersExpectLength(THREE);
    }

    @Test
    public void deleteOrderById_ValidId_Returns200() throws Exception {
        OrderDTO order = addOrderToUserExpectOk(CART_1);

        getOrdersExpectLength(ONE);
        getOrderExpectSameId(order);

        deleteOrderExpectOk(order);

        getOrdersExpectLength(ZERO);
        getOrderExpectNotFound(order);
    }

    @Test
    public void deleteOrderById_ValidIdAmongIds_Returns200() throws Exception {
        addOrderToUserExpectOk(CART_1);
        OrderDTO order2 = addOrderToUserExpectOk(CART_2);
        addOrderToUserExpectOk(CART_3);

        getOrdersExpectLength(THREE);

        getOrderExpectSameId(order2);
        deleteOrderExpectOk(order2);
        getOrderExpectNotFound(order2);

        getOrdersExpectLength(TWO);
    }

    @Test
    public void deleteOrderById_InvalidId_Returns404() throws Exception {
        OrderDTO randomOrder = new OrderDTO(
                USER.getId(), HUNDRED, CART_1.getProducts());
        deleteOrderExpectNotFound(randomOrder);
    }

    @AfterAll
    public void tearDownAll() throws Exception {
        removeProducts();
        deleteUser();
    }

    @AfterEach
    public void tearDown() throws Exception {
        removeOrdersFromUser();
        removeAllOrders();
    }

    private void createUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(USER_URL + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(USER)));
    }

    private void deleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(
                USER_URL + "/delete/" + USER.getId()));
    }

    private void createProducts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(PRODUCT_URL + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(PRODUCT_1)));

        mockMvc.perform(MockMvcRequestBuilders.post(PRODUCT_URL + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(PRODUCT_2)));

        mockMvc.perform(MockMvcRequestBuilders.post(PRODUCT_URL + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(PRODUCT_3)));
    }

    private void removeProducts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(
                PRODUCT_URL + "/delete/" + PRODUCT_1.getId()));

        mockMvc.perform(MockMvcRequestBuilders.delete(
                PRODUCT_URL + "/delete/" + PRODUCT_2.getId()));

        mockMvc.perform(MockMvcRequestBuilders.delete(
                PRODUCT_URL + "/delete/" + PRODUCT_3.getId()));
    }

    private void addOrderExpectOk(final OrderDTO order) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(ORDER_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk());
    }

    private void addOrderExpectBadRequest(final OrderDTO order)
            throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(ORDER_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isBadRequest());
    }

    private OrderDTO addOrderToUserExpectOk(final CartDTO cartDTO)
            throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post(CART_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartDTO)))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post(
                        USER_URL
                                + "/"
                                + USER.getId()
                                + "/checkout"))
                .andExpect(status().isOk());

        removeCartFromUser();

        List<OrderDTO> orders = new ArrayList<>();
        mockMvc.perform(MockMvcRequestBuilders.get(ORDER_URL + "/"))
                .andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    orders.addAll(objectMapper.readValue(content,
                            objectMapper.getTypeFactory()
                                    .constructCollectionType(List.class,
                                            OrderDTO.class)));
                })
                .andExpect(status().isOk());

        assertFalse(orders.isEmpty());
        return orders.getLast();
    }

    private void getOrderExpectSameId(final OrderDTO order) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(
                        ORDER_URL + "/" + order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId().toString()));
    }

    private void getOrderExpectNotFound(final OrderDTO order) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(
                        ORDER_URL + "/" + order.getId()))
                .andExpect(status().isNotFound());
    }

    private void getOrdersExpectLength(final int length) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(ORDER_URL + "/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(length));
    }

    private void deleteOrderExpectOk(final OrderDTO order) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(
                        ORDER_URL + "/delete/" + order.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Order deleted successfully"));
    }

    private void deleteOrderExpectNotFound(final OrderDTO order)
            throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(
                        ORDER_URL + "/delete/" + order.getId()))
                .andExpect(status().isNotFound());
    }

    private void removeOrdersFromUser() throws Exception {
        List<OrderDTO> orders = new ArrayList<>();
        mockMvc.perform(MockMvcRequestBuilders.get(USER_URL
                        + "/"
                        + USER.getId()
                        + "/orders"))
                .andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    if (content.isEmpty()) {
                        return;
                    }
                    orders.addAll(objectMapper.readValue(content,
                            objectMapper.getTypeFactory()
                                    .constructCollectionType(List.class,
                                            OrderDTO.class)));
                });

        for (final OrderDTO order : orders) {
            mockMvc.perform(MockMvcRequestBuilders.post(
                            USER_URL + "/" + USER.getId() + "/removeOrder")
                    .param("orderId", order.getId().toString()));
        }
    }

    private void removeAllOrders() throws Exception {
        List<OrderDTO> orders = new ArrayList<>();
        mockMvc.perform(MockMvcRequestBuilders.get(ORDER_URL + "/"))
                .andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    if (content.isEmpty()) {
                        return;
                    }
                    orders.addAll(objectMapper.readValue(content,
                            objectMapper.getTypeFactory()
                                    .constructCollectionType(List.class,
                                            OrderDTO.class)));
                });

        for (final OrderDTO order : orders) {
            mockMvc.perform(MockMvcRequestBuilders.delete(
                    ORDER_URL + "/delete/" + order.getId()));
        }
    }

    private void removeCartFromUser() throws Exception {
        List<CartDTO> carts = new ArrayList<>();
        mockMvc.perform(MockMvcRequestBuilders.get(CART_URL + "/"))
                .andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    if (content.isEmpty()) {
                        return;
                    }
                    carts.addAll(objectMapper.readValue(content,
                            objectMapper.getTypeFactory()
                                    .constructCollectionType(List.class,
                                            CartDTO.class)));
                });

        for (final CartDTO cart : carts) {
            if (cart.getUserId().equals(USER.getId())) {
                mockMvc.perform(MockMvcRequestBuilders.delete(
                        CART_URL + "/delete/" + cart.getId()));
            }
        }
    }

    public static class OrderDTO {
        private UUID id;
        private UUID userId;
        private double totalPrice;
        private List<ProductDTO> products;

        public OrderDTO(final UUID userId, final double totalPrice,
                        final List<ProductDTO> products) {
            this.id = UUID.randomUUID();
            this.userId = userId;
            this.totalPrice = totalPrice;
            this.products = products;
        }

        public UUID getUserId() {
            return userId;
        }

        public void setUserId(final UUID userId) {
            this.userId = userId;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(final double totalPrice) {
            this.totalPrice = totalPrice;
        }

        public UUID getId() {
            return id;
        }

        public void setId(final UUID id) {
            this.id = id;
        }

        public List<ProductDTO> getProducts() {
            return products;
        }

        public void setProducts(final List<ProductDTO> products) {
            this.products = products;
        }
    }

    public static class UserDTO {
        private UUID id;
        private String name;

        public UserDTO(final String name) {
            this.id = UUID.randomUUID();
            this.name = name;
        }

        public UUID getId() {
            return id;
        }

        public void setId(final UUID id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }
    }

    public static class ProductDTO {
        private UUID id;
        private String name;
        private double price;

        public ProductDTO(final String name, final double price) {
            this.id = UUID.randomUUID();
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(final double price) {
            this.price = price;
        }

        public UUID getId() {
            return id;
        }

        public void setId(final UUID id) {
            this.id = id;
        }
    }

    public static class CartDTO {
        private UUID id;
        private UUID userId;
        private List<ProductDTO> products;

        public CartDTO(final UUID userId, final List<ProductDTO> products) {
            this.id = UUID.randomUUID();
            this.userId = userId;
            this.products = products;
        }

        public UUID getUserId() {
            return userId;
        }

        public void setUserId(final UUID userId) {
            this.userId = userId;
        }

        public List<ProductDTO> getProducts() {
            return products;
        }

        public void setProducts(final List<ProductDTO> products) {
            this.products = products;
        }

        public void addProduct(final ProductDTO product) {
            this.products.add(product);
        }

        public void removeProduct(final ProductDTO product) {
            this.products.remove(product);
        }

        public UUID getId() {
            return id;
        }

        public void setId(final UUID id) {
            this.id = id;
        }
    }
}
