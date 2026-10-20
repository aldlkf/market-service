package com.marketplace.service;

import com.marketplace.dto.CartItemRequest;
import com.marketplace.dto.CartItemResponseDto;
import com.marketplace.dto.CartResponseDto;
import com.marketplace.dto.OrderResponseDto;
import com.marketplace.exception.InsufficientFundsException;
import com.marketplace.exception.ProductNotFoundException;
import com.marketplace.exception.ProductOutOfStockException;
import com.marketplace.model.CartItem;
import com.marketplace.model.Order;
import com.marketplace.model.Product;
import com.marketplace.model.User;
import com.marketplace.repository.CartItemRepository;
import com.marketplace.repository.OrderRepository;
import com.marketplace.repository.ProductRepository;
import com.marketplace.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public CartService(CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository,
                       OrderRepository orderRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public CartResponseDto getCart(User user) {
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
    public CartItemResponseDto addToCart(User user, CartItemRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден"));

        if (product.getStockQuantity() < request.getQuantity()) {
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
    public void removeFromCart(User user, Long itemId) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Позиция в корзине не найдена"));

        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("У вас нет доступа к этой позиции");
        }

        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }

    @Transactional
    public List<OrderResponseDto> checkout(User currentUser) {
        List<CartItem> cartItems = cartItemRepository.findByUser(currentUser);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Корзина пуста");
        }

        BigDecimal totalCost = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new ProductOutOfStockException("Недостаточно товара на складе: " + product.getTitle());
            }
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalCost = totalCost.add(itemTotal);
        }

        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (user.getBalance().compareTo(totalCost) < 0) {
            throw new InsufficientFundsException("Недостаточно средств на балансе для оформления всей корзины");
        }

        user.setBalance(user.getBalance().subtract(totalCost));
        userRepository.save(user);

        List<OrderResponseDto> createdOrders = new ArrayList<>();
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);

            Order order = new Order();
            order.setUser(user);
            order.setProduct(product);
            order.setQuantity(item.getQuantity());
            order.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            order.setStatus("PAID");
            order.setCreatedAt(LocalDateTime.now());

            Order savedOrder = orderRepository.save(order);
            createdOrders.add(new OrderResponseDto(savedOrder));
        }

        cartItemRepository.deleteByUser(user);

        return createdOrders;
    }
}