package com.marketplace.service;

import com.marketplace.dto.OrderRequest;
import com.marketplace.exception.InsufficientFundsException;
import com.marketplace.exception.ProductNotFoundException;
import com.marketplace.exception.ProductOutOfStockException;
import com.marketplace.model.Order;
import com.marketplace.model.Product;
import com.marketplace.model.User;
import com.marketplace.repository.OrderRepository;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public OrderService(UserRepository userRepository, ProductRepository productRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order createOrder(User user, OrderRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден"));

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new ProductOutOfStockException("Недостаточно товара на складе");
        }

        BigDecimal totalCost = product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        if (user.getBalance().compareTo(totalCost) < 0) {
            throw new InsufficientFundsException("Недостаточно средств на балансе");
        }

        user.setBalance(user.getBalance().subtract(totalCost));
        product.setStockQuantity(product.getStockQuantity() - request.getQuantity());

        userRepository.save(user);
        productRepository.save(product);

        Order order = new Order();
        order.setUser(user);
        order.setProduct(product);
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(totalCost);
        order.setStatus("PAID");

        return orderRepository.save(order);
    }
}