package com.marketplace.service;

import com.marketplace.exception.InsufficientFundsException;
import com.marketplace.exception.ProductNotFoundException;
import com.marketplace.exception.ProductOutOfStockException;
import com.marketplace.exception.UserNotFoundException;
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

    public OrderService(UserRepository userRepository,
                        ProductRepository productRepository,
                        OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order createOrder(Long userId, Long productId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден"));

        if (product.getStockQuantity() < quantity) {
            throw new ProductOutOfStockException("Недостаточно товара на складе");
        }

        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));

        if (user.getBalance().compareTo(totalPrice) < 0) {
            throw new InsufficientFundsException("Недостаточно средств на балансе");
        }

        user.setBalance(user.getBalance().subtract(totalPrice));
        product.setStockQuantity(product.getStockQuantity() - quantity);

        userRepository.save(user);
        productRepository.save(product);

        Order order = new Order(null, user, product, quantity, totalPrice, "PAID");
        return orderRepository.save(order);
    }
}