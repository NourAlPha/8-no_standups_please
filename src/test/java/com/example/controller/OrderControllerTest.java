package com.example.controller;

import com.example.MiniProject1.MiniProject1Application;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest(classes = MiniProject1Application.class)
@AutoConfigureMockMvc
public class OrderControllerTest {

    private static final String BASE_URL = "/order";

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
    private static final OrderDTO ORDER_1 =
            new OrderDTO(USER.getId(), HUNDRED);
    private static final OrderDTO ORDER_2 =
            new OrderDTO(USER.getId(), TWO_HUNDRED);
    private static final OrderDTO ORDER_3 =
            new OrderDTO(USER.getId(), THREE_HUNDRED);

    @BeforeAll
    public static void setUp() throws Exception {
        createUser(USER);
    }

    @Test
    public void addOrder_ValidOrder_Returns200() throws Exception {
        addOrderExpectOk(ORDER_1);
        addOrderToUserExpectOk(ORDER_1, USER);
    }

    @Test
    public void addOrder_AddingMissingUserId_Returns400()
            throws Exception {
        OrderDTO orderMissingUser =
                new OrderDTO(UUID.randomUUID(), HUNDRED);

        addOrderExpectBadRequest(orderMissingUser);
    }

    @Test
    public void addOrder_AddingNullUserId_Returns400() throws Exception {
        OrderDTO orderNullUser =
                new OrderDTO(null, TWO_HUNDRED);

        addOrderExpectBadRequest(orderNullUser);
    }

    @Test
    public void getOrderById_ValidId_ReturnsOrder200() throws Exception {
        addOrderExpectOk(ORDER_1);
        addOrderToUserExpectOk(ORDER_1, USER);

        getOrderExpectSameId(ORDER_1);
    }

    @Test
    public void getOrderById_ValidIdAmongIds_ReturnsOrder200()
            throws Exception {
        addOrderExpectOk(ORDER_1);
        addOrderToUserExpectOk(ORDER_1, USER);

        addOrderExpectOk(ORDER_2);
        addOrderToUserExpectOk(ORDER_2, USER);

        addOrderExpectOk(ORDER_3);
        addOrderToUserExpectOk(ORDER_3, USER);


        getOrderExpectSameId(ORDER_2);
    }

    @Test
    public void getOrderById_InvalidId_Returns404() throws Exception {
        OrderDTO randomOrder = new OrderDTO(USER.getId(), HUNDRED);
        getOrderExpectNotFound(randomOrder);
    }

    @Test
    public void getOrders_EmptyOrders_ReturnsEmptyList200() throws Exception {
        getOrdersExpectLength(ZERO);
    }

    @Test
    public void getOrders_Orders_ReturnsOrders200() throws Exception {
        addOrderExpectOk(ORDER_1);
        addOrderToUserExpectOk(ORDER_1, USER);

        addOrderExpectOk(ORDER_2);
        addOrderToUserExpectOk(ORDER_2, USER);

        addOrderExpectOk(ORDER_3);
        addOrderToUserExpectOk(ORDER_3, USER);

        getOrdersExpectLength(THREE);
    }

    @Test
    public void getOrders_SequentiallyCheckingOrders_OrdersListIsUpToDate()
            throws Exception {
        getOrdersExpectLength(ZERO);

        addOrderExpectOk(ORDER_1);
        addOrderToUserExpectOk(ORDER_1, USER);

        getOrdersExpectLength(ONE);

        addOrderExpectOk(ORDER_2);
        addOrderToUserExpectOk(ORDER_2, USER);

        getOrdersExpectLength(TWO);

        addOrderExpectOk(ORDER_3);
        addOrderToUserExpectOk(ORDER_3, USER);

        getOrdersExpectLength(THREE);
    }

    @Test
    public void deleteOrderById_ValidId_Returns200() throws Exception {
        addOrderExpectOk(ORDER_1);
        addOrderToUserExpectOk(ORDER_1, USER);

        getOrdersExpectLength(ONE);
        getOrderExpectSameId(ORDER_1);

        deleteOrderExpectOk(ORDER_1);

        getOrdersExpectLength(ZERO);
        getOrderExpectNotFound(ORDER_1);
    }

    @Test
    public void deleteOrderById_ValidIdAmongIds_Returns200() throws Exception {
        addOrderExpectOk(ORDER_1);
        addOrderToUserExpectOk(ORDER_1, USER);

        addOrderExpectOk(ORDER_2);
        addOrderToUserExpectOk(ORDER_2, USER);

        addOrderExpectOk(ORDER_3);
        addOrderToUserExpectOk(ORDER_3, USER);

        getOrdersExpectLength(THREE);
        getOrderExpectSameId(ORDER_2);

        deleteOrderExpectOk(ORDER_2);

        getOrdersExpectLength(TWO);
        getOrderExpectNotFound(ORDER_2);
    }

    @Test
    public void deleteOrderById_InvalidId_Returns404() throws Exception {
        OrderDTO randomOrder = new OrderDTO(USER.getId(), HUNDRED);
        deleteOrderExpectNotFound(randomOrder);
    }

    @AfterAll
    public static void tearDownAll() throws Exception {
        deleteUser(USER);
    }

    @AfterEach
    public void tearDown() throws Exception {
        List<OrderDTO> orders = new ArrayList<>();
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/"))
                .andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    orders.addAll(objectMapper.readValue(content,
                            objectMapper.getTypeFactory()
                                    .constructCollectionType(List.class,
                                            OrderDTO.class)));
                });

        for (final OrderDTO order : orders) {
            mockMvc.perform(MockMvcRequestBuilders.delete(
                    BASE_URL + "/delete/" + order.getId()));
        }
    }

    private static void createUser(final UserDTO user) throws Exception {
        // TODO(NourAlPha): Implement this method after adding user controller.
    }

    private static void deleteUser(final UserDTO user) throws Exception {
        // TODO(NourAlPha): Implement this method after adding user controller.
    }

    private void addOrderExpectOk(final OrderDTO order) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk());
    }

    private void addOrderExpectBadRequest(final OrderDTO order)
            throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isBadRequest());
    }

    private void addOrderToUserExpectOk(final OrderDTO order,
                                        final UserDTO user) throws Exception {
        // TODO(NourAlPha): Implement this method after adding user controller.
    }

    private void getOrderExpectSameId(final OrderDTO order) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(
                        BASE_URL + "/" + order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId().toString()));
    }

    private void getOrderExpectNotFound(final OrderDTO order) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(
                        BASE_URL + "/" + order.getId()))
                .andExpect(status().isNotFound());
    }

    private void getOrdersExpectLength(final int length) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(length));
    }

    private void deleteOrderExpectOk(final OrderDTO order) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(
                        BASE_URL + "/delete/" + order.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Order deleted successfully"));
    }

    private void deleteOrderExpectNotFound(final OrderDTO order)
            throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(
                        BASE_URL + "/delete/" + order.getId()))
                .andExpect(status().isNotFound());
    }

    public static class OrderDTO {
        private UUID id;
        private UUID userId;
        private double totalPrice;

        public OrderDTO(final UUID userId, final double totalPrice) {
            this.id = UUID.randomUUID();
            this.userId = userId;
            this.totalPrice = totalPrice;
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
}
