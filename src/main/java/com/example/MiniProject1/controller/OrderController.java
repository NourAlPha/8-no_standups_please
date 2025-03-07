package com.example.MiniProject1.controller;

import com.example.MiniProject1.model.Order;
import com.example.MiniProject1.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/order")
@Tag(name = "Order Controller", description = "Endpoints related to orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/")
    @Operation(
            summary = "Create new order",
            description = "Adds a new order to the system."
    )
    public void addOrder(@RequestBody final Order order) {
        orderService.addOrder(order);
    }

    @GetMapping("/{orderId}")
    @Operation(
            summary = "Find order by ID",
            description = "Retrieves details of specific order by Id."
    )
    public Order getOrderById(@PathVariable final UUID orderId) {
        return orderService.getOrderById(orderId);
    }

    @GetMapping("/")
    @Operation(
            summary = "Get all orders",
            description = "Retrieves a list of all orders in the system."
    )
    public ArrayList<Order> getOrders() {
        return orderService.getOrders();
    }

    @DeleteMapping("/delete/{orderId}")
    @Operation(
            summary = "Remove order by ID",
            description = "Deletes an order by its from the system."
    )
    public String deleteOrderById(@PathVariable final UUID orderId) {
        orderService.deleteOrderById(orderId);
        return "Order deleted successfully";
    }
}
