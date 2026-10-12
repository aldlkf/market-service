package com.marketplace.controller;

import com.marketplace.dto.TopUpBalanceRequest;
import com.marketplace.model.User;
import com.marketplace.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Управление профилем пользователя и балансом")
public class UserController {

    private final OrderService orderService;

    public UserController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/balance/top-up")
    @Operation(
            summary = "Пополнить баланс текущего пользователя",
            description = "Увеличивает баланс авторизованного пользователя на указанную сумму. Доступно любому аутентифицированному пользователю."
    )
    public ResponseEntity<Map<String, Object>> topUpBalance(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody TopUpBalanceRequest request
    ) {
        BigDecimal newBalance = orderService.topUpBalance(currentUser, request.getAmount());
        return ResponseEntity.ok(Map.of(
                "message", "Баланс успешно пополнен",
                "newBalance", newBalance
        ));
    }
}