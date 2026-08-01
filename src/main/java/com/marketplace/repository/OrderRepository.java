package com.marketplace.repository;

import com.marketplace.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private final List<Order> orders = new ArrayList<>();

    public Order save(Order order){
        orders.add(order);
        return order;
    }

    public List<Order> findByUserId(Long userId){
        List<Order> usersOrders = new ArrayList<>();
        for (Order order : orders){
            if (order.getUserId().equals(userId)){
                usersOrders.add(order);
            }
        }
        return usersOrders;
    }

    public List<Order> findAll(){
        return orders;
    }
}
