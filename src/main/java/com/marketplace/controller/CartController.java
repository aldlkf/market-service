package com.marketplace.controller;

import com.marketplace.dto.CartItemRequest;
import com.marketplace.dto.CartItemResponseDto;
import com.marketplace.dto.CartResponseDto;
import com.marketplace.dto.OrderResponseDto;
import com.marketplace.model.User;
import com.marketplace.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Управление корзиной покупок")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService){
        this.cartService = cartService;
    }

    @GetMapping
    @Operation(summary = "Получить содержимое корзины", description = "Возвращает список товаров в корзине текущего пользователя и общую сумму.")
    public ResponseEntity<CartResponseDto> getCart(@AuthenticationPrincipal User currentUser){
        return ResponseEntity.ok(cartService.getCart(currentUser));
    }

    @PostMapping("/items")
    @Operation(summary = "Добавить товар в корзину", description = "Добавляет товар или увеличивает его количество, если он уже в корзине.")
    public ResponseEntity<CartItemResponseDto> addToCart(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CartItemRequest request
    ){
        CartItemResponseDto response = cartService.addToCart(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "Удалить позицию из корзины", description = "Удаляет конкретный товар из корзины по ID записи корзины.")
    public ResponseEntity<Void> removeFromCart(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id
    ){
        cartService.removeFromCart(currentUser, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(summary = "Очистить корзину", description = "Удаляет все товары из корзины текущего пользователя.")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal User currentUser){
        cartService.clearCart(currentUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkout")
    @Operation(
            summary = "Оформить заказ из корзины (Checkout)",
            description = "Списывает общую сумму с баланса, уменьшает остаток товаров на складе, создает заказы со статусом PAID и очищает корзину."
    )
    public ResponseEntity<List<OrderResponseDto>> checkout(
            @AuthenticationPrincipal User currentUser
    ){
        List<OrderResponseDto> orders = cartService.checkout(currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(orders);
    }

}

