package com.marketplace.service;

import com.marketplace.dto.CartItemRequest;
import com.marketplace.dto.CartItemResponseDto;
import com.marketplace.dto.CartResponseDto;
import com.marketplace.exception.ProductNotFoundException;
import com.marketplace.exception.ProductOutOfStockException;
import com.marketplace.model.CartItem;
import com.marketplace.model.Product;
import com.marketplace.model.User;
import com.marketplace.repository.CartItemRepository;
import com.marketplace.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository){
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public CartResponseDto getCart(User user){
        List<CartItem> items = cartItemRepository.findByUser(user);
        List<CartItemResponseDto> itemDtos = items.stream()
                .map(CartItemResponseDto::new)
                .toList();

        BigDecimal total = itemDtos.stream()
                .map(CartItemResponseDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponseDto(itemDtos, total);
    }

    @Transactional
    public CartItemResponseDto addToCart(User user, CartItemRequest request){
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден"));

        if (product.getStockQuantity() < request.getQuantity()){
            throw new ProductOutOfStockException("Недостаточно товара на складе");
        }

        CartItem cartItem = cartItemRepository.findByUserAndProductId(user, request.getProductId())
                .map(item -> {
                    int updatedQuantity = item.getQuantity() + request.getQuantity();
                    if (product.getStockQuantity() < updatedQuantity) {
                        throw new ProductOutOfStockException("Недостаточно товара на складе для добавления");
                    }
                    item.setQuantity(updatedQuantity);
                    return item;
                })
                .orElseGet(() -> new CartItem(user, product, request.getQuantity()));

        CartItem saved = cartItemRepository.save(cartItem);
        return new CartItemResponseDto(saved);
    }

    @Transactional
    public void removeFromCart(User user, Long itemId){
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Позиция в корзине не найдена"));

        if (!cartItem.getUser().getId().equals(user.getId())){
            throw new RuntimeException("У вас нет доступа к этой позиции");
        }

        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void clearCart(User user){
        cartItemRepository.deleteByUser(user);
    }
}