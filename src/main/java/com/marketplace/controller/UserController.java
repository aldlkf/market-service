package com.marketplace.controller;

import com.marketplace.model.Order;
import com.marketplace.model.User;
import com.marketplace.repository.OrderRepository;
import com.marketplace.repository.UserRepository;
import com.marketplace.exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public UserController(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{userId}/orders")
    public List<Order> getUserOrders(@PathVariable Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователь с ID " + userId + " не найден");
        }
        return orderRepository.findByUserId(userId);
    }

}
