package com.marketplace.controller;

import com.marketplace.dto.OrderRequest;
import com.marketplace.model.Order;
import com.marketplace.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Управление заказами и покупками")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Создать новый заказ", description = "Списывает баланс пользователя и уменьшает остаток товара на складе")
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderRequest request) {
        Order createdOrder = orderService.createOrder(
                request.getUserId(),
                request.getProductId(),
                request.getQuantity()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }
}