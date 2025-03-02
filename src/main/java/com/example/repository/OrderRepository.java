package com.example.repository;

import com.example.model.Order;
import com.example.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class OrderRepository extends MainRepository<Order> {
    @Value("${spring.application.orderDataPath}")
    private String ordersPath;


    @SuppressWarnings("checkstyle:VisibilityModifier")
    public static ArrayList<Order> orders = new ArrayList<>();


    public void addOrder(Order order){
        orders.add(order);
        save(order);
    }

    public ArrayList<Order> getOrders(){
        initializeOrders();
        return orders;
    }

    public Order getOrderById(UUID orderId){
        initializeOrders();
        for (Order order : orders) {
            if (order.getId().equals(orderId)) {

                return order;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("Order with id %s not found", orderId));
    }

    public void deleteOrderById(UUID orderId){
        Order order = getOrderById(orderId);
        orders.remove(order);
        overrideData(orders);
    }

    @Override
    protected String getDataPath() {
        return ordersPath;
    }

    @Override
    protected Class<Order[]> getArrayType() {
        return Order[].class;
    }

    private void initializeOrders() {
        if (orders.isEmpty()) {
            orders.addAll(findAll());
        }
    }
}
