package com.marketplace.service;

import com.marketplace.exception.*;
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

    public OrderService(UserRepository userRepository, ProductRepository productRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    public Order createOrder(Long userId, Long productId, int quantity) {
        User ilyas = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));

        if (product.getStockQuantity() < quantity){
            throw new ProductOutOfStockException(("Not enough stock for product: " + product.getTitle()+". Requested " + quantity + ", Available" + product.getStockQuantity()));
        }

        BigDecimal totalPrice = product.getPrice().multiply(new BigDecimal(quantity));

        if (ilyas.getBalance().compareTo(totalPrice) < 0){
            throw new InsufficientFundsException("User " + ilyas.getName() + " has insufficient funds. Required: " + totalPrice + ", Available: " + ilyas.getBalance());
        }

        ilyas.setBalance(ilyas.getBalance().subtract(totalPrice));
        product.setStockQuantity(product.getStockQuantity() - quantity);

        Order order = new Order(orderIdCounter++, userId, productId, quantity, totalPrice, "PAID");
        return orderRepository.save(order);
    }

    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new MarketplaceException("Order with ID " + orderId + " not found"));

        if("CANCELLED".equals(order.getStatus())){
            throw new MarketplaceException("Order №" + orderId + " is already cancelled");
        }

        User ilyas = userRepository.findById(order.getUserId()).orElseThrow(() -> new UserNotFoundException(order.getUserId()));

        Product product = productRepository.findById(order.getProductId()).orElseThrow(() -> new ProductNotFoundException(order.getProductId()));

        ilyas.setBalance(ilyas.getBalance().add(order.getTotalPrice()));
        product.setStockQuantity(product.getStockQuantity() + order.getQuantity());

        order.setStatus("CANCELLED");
    }

    public List<Order> getUserOrders(Long userId){
        return orderRepository.findByUserId(userId);
    }

}
