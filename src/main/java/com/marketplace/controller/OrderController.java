package com.marketplace.controller;

import com.marketplace.dto.OrderRequest;
import com.marketplace.dto.OrderResponseDto;
import com.marketplace.model.User;
import com.marketplace.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<OrderResponseDto> createOrder(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody OrderRequest request
    ) {
        OrderResponseDto createdOrder = orderService.createOrder(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping("/my")
    @Operation(
            summary = "История заказов текущего пользователя",
            description = "Возвращает список всех заказов авторизованного пользователя с пагинацией. Заказы других пользователей недоступны."
    )
    public ResponseEntity<Page<OrderResponseDto>> getMyOrders(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(orderService.getUserOrders(currentUser, pageable));
    }

    @PostMapping("/{id}/cancel")
    @Operation(
            summary = "Отменить заказ",
            description = "Отменяет оплаченный заказ, возвращает средства на баланс пользователя и восстанавливает остаток товара на складе. Доступно владельцу заказа или администратору."
    )

    public ResponseEntity<OrderResponseDto> cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ){
        OrderResponseDto canceledOrder = orderService.cancelOrder(id, currentUser);
        return ResponseEntity.ok(canceledOrder);
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Изменить статус заказа (ADMIN)",
            description = "Позволяет администратору менять статус заказа (PAID -> SHIPPED -> DELIVERED). Требуется роль ADMIN."
    )
    public ResponseEntity<com.marketplace.dto.OrderResponseDto> updateOrderStatus(
            @PathVariable Long id,
            @jakarta.validation.Valid @RequestBody com.marketplace.dto.UpdateOrderStatusRequest request
    ){
        com.marketplace.dto.OrderResponseDto updated = orderService.updateOrderStatus(id, request.getStatus());
        return ResponseEntity.ok(updated);
    }

}