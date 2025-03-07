package com.example.MiniProject1.repository;

import com.example.MiniProject1.model.Order;
import com.example.MiniProject1.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.UUID;

@Repository
@SuppressWarnings("rawtypes")
public class OrderRepository extends GenericRepository<Order> {

    @Value("${spring.application.orderDataPath}")
    private String ordersPath;

    public void addOrder(final Order order) {
        addObject(order);
    }

    public ArrayList<Order> getOrders() {
        return getObjects();
    }

    public Order getOrderById(final UUID orderId) {
        return getObjectById(orderId);
    }

    public void deleteOrderById(final UUID orderId) {
        deleteObjectById(orderId);
    }

    public boolean isProductInOrder(final UUID productId) {
        ArrayList<Order> orders = getOrders();

        for (Order order : orders) {
            for (Product product : order.getProducts()) {
                if (product.getId().equals(productId)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected String getDataPath() {
        return ordersPath;
    }

    @Override
    protected Class<Order[]> getArrayType() {
        return Order[].class;
    }

}
