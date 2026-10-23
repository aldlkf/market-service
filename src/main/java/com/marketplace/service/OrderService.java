package com.marketplace.service;

import com.marketplace.dto.OrderRequest;
import com.marketplace.dto.OrderResponseDto;
import com.marketplace.exception.*;
import com.marketplace.model.Order;
import com.marketplace.model.OrderStatus;
import com.marketplace.model.Product;
import com.marketplace.model.User;
import com.marketplace.repository.OrderRepository;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    public OrderResponseDto createOrder(User user, OrderRequest request) {
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
        order.setCreatedAt(LocalDateTime.now()); // Теперь createdAt всегда заполнен

        Order savedOrder = orderRepository.save(order);
        return new OrderResponseDto(savedOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getUserOrders(User user, Pageable pageable) {
        return orderRepository.findByUser(user, pageable).map(OrderResponseDto::new);
    }

    @Transactional
    public BigDecimal topUpBalance(User currentUser, BigDecimal amount) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
        BigDecimal newBalance = currentBalance.add(amount);
        user.setBalance(newBalance);
        userRepository.save(user);

        return newBalance;
    }

    @Transactional
    public OrderResponseDto cancelOrder(Long orderId, User currentUser) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

    boolean isAdmin = currentUser.getRole() != null && currentUser.getRole().name().equals("ADMIN");
        if (!order.getUser().getId().equals(currentUser.getId()) && !isAdmin) {
        throw new AccessDeniedCustomException("У вас нет прав для отмены этого заказа");
    }

    if ("CANCELED".equalsIgnoreCase(order.getStatus())) {
        throw new IllegalOrderStateException("Заказ уже был отменен ранее");
    }

    User orderOwner = order.getUser();
    BigDecimal refundAmount = order.getTotalPrice();
    orderOwner.setBalance(orderOwner.getBalance().add(refundAmount));
        userRepository.save(orderOwner);

    Product product = order.getProduct();
    if (product != null) {
        product.setStockQuantity(product.getStockQuantity() + order.getQuantity());
        productRepository.save(product);
    }

    order.setStatus("CANCELED");
    Order savedOrder = orderRepository.save(order);

    return new OrderResponseDto(savedOrder);
    }

    @Transactional
    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatus newStatus){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->new RuntimeException("Заказ не найден"));

        OrderStatus currentStatus;
        try {
            currentStatus = OrderStatus.valueOf(order.getStatus());
        } catch (IllegalArgumentException e){
            throw new IllegalOrderStateException("Некорректный текущий статус заказа" + order.getStatus());
        }

        if(!currentStatus.canTransitionTo(newStatus)){
            throw new IllegalOrderStateException(
                    String.format("Недопустимый переход статуса: %s -> %s", currentStatus, newStatus)
            );
        }

        order.setStatus(newStatus.name());
        Order updated = orderRepository.save(order);
        return new OrderResponseDto(updated);

    }

}