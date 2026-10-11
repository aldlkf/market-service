package com.marketplace.controller;

import com.marketplace.dto.OrderRequest;
import com.marketplace.model.Order;
import com.marketplace.model.User;
import com.marketplace.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Управление заказами и покупками")
public class OrderController{

    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Создать новый заказ", description = "Списывает баланс пользователя и уменьшает остаток товара на складе")
    public ResponseEntity<Order> createOrder(
        @AuthenticationPrincipal User currentUser,
        @Valid @RequestBody OrderRequest request
    ){
        Order createOrder = orderService.createOrder(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createOrder);
    }

    @GetMapping("/my")
    @Operation(
            summary = "История заказов текущего пользователя",
            description = "Возвращает список всех заказов авторизованного пользователя с пагинацией. Заказы других пользователей недоступны."
    )
    public ResponseEntity<org.springframework.data.domain.Page<Order>> getMyOrders(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ){
        org.springframework.data.domain.Sort sort = direction.equalsIgnoreCase("desc")
                ? org.springframework.data.domain.Sort.by(sortBy).descending()
                : org.springframework.data.domain.Sort.by(sortBy).ascending();

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);
        return ResponseEntity.ok(orderService.getUserOrders(currentUser, pageable));
    }
}