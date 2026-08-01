package com.marketplace.service;

import com.marketplace.model.Order;
import com.marketplace.model.Product;
import com.marketplace.model.User;
import com.marketplace.repository.OrderRepository;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.List;
import java.util.logging.Logger;

public class OrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private long orderIdCounter = 1;

    public OrderService(UserRepository userRepository, ProductRepository productRepository, OrderRepository orderRepository){
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    public boolean createOrder(Long userId, Long productId, int quantity){
        System.out.println(">>> MAKING ORDER <<<");

        var userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()){
            System.out.println("ERROR: NO USER WITH "+userId+" Id");
            return false;
        }
        User user = userOptional.get();

        var productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()){
            System.out.println("ERROR: NO PRODUCT WITH "+productId+" Id");
            return false;
        }
        Product product = productOptional.get();

        if (product.getStockQuantity() < quantity){
            System.out.println("ERROR: NOT ENOUGH ON STOCK! AVAILABLE: "+product.getStockQuantity());
            return false;
        }

        BigDecimal totalPrice = product.getPrice().multiply(new BigDecimal(quantity));

        if (user.getBalance().compareTo(totalPrice) < 0){
            System.out.println("ERROR: USER "+user.getName()+" HAS NOT ENOUGH MONEY! BALANCE: "+ user.getFormattedBalance());
            return false;
        }

        user.setBalance(user.getBalance().subtract(totalPrice));
        product.setStockQuantity(product.getStockQuantity() - quantity);

        Order order = new Order(orderIdCounter++, userId, productId, quantity, totalPrice, "PAID");
        orderRepository.save(order);

        System.out.println("SUCCESS! YOUR ORDER IS №" + order.getId());
        System.out.println("ORDER DETAIL: " + order);

        return true;
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

}
