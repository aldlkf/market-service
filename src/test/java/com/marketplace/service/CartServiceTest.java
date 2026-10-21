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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private CartService cartService;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Ilyas");
        testUser.setEmail("ilyas@example.com");
        testUser.setBalance(new BigDecimal("500000.00"));

        testProduct = new Product();
        testProduct.setId(10L);
        testProduct.setTitle("RTX 5070");
        testProduct.setPrice(new BigDecimal("200000.00"));
        testProduct.setStockQuantity(5);
    }

    @Test
    @DisplayName("Успешное добавление нового товара в корзину")
    void addToCart_Success_NewItem(){
        CartItemRequest request = new CartItemRequest(10L, 2);

        when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.findByUserAndProductId(testUser, 10L)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(inv -> {
            CartItem item = inv.getArgument(0);
            item.setId(100L);
            return item;
        });

        CartItemResponseDto response = cartService.addToCart(testUser, request);

        assertNotNull(response);
        assertEquals(10L, response.getProductId());
        assertEquals(2, response.getQuantity());
        assertEquals(new BigDecimal("400000.00"), response.getSubtotal());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Ошибка добавления в корзину: недостаточно товара на складе")
    void addToCart_OutOfStock(){
        CartItemRequest request = new CartItemRequest(10L, 10); // На складе всего 5

        when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));

        assertThrows(ProductOutOfStockException.class, () -> cartService.addToCart(testUser, request));
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Ошибка добавления в корзину: товар не найден")
    void addToCart_ProductNotFound(){
        CartItemRequest request = new CartItemRequest(999L, 1);

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> cartService.addToCart(testUser, request));
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Получение содержимого корзины с подсчетом общей суммы")
    void getCart_Success(){
        CartItem item = new CartItem(testUser, testProduct, 2);
        item.setId(100L);

        when(cartItemRepository.findByUser(testUser)).thenReturn(List.of(item));

        CartResponseDto cart = cartService.getCart(testUser);

        assertNotNull(cart);
        assertEquals(1, cart.getItems().size());
        assertEquals(new BigDecimal("400000.00"), cart.getTotalAmount());
    }

    @Test
    @DisplayName("Успешный Checkout: баланс и остаток списаны, заказы созданы, корзина очищена")
    void checkout_Success(){
        CartItem item = new CartItem(testUser, testProduct, 2);
        item.setId(100L);

        when(cartItemRepository.findByUser(testUser)).thenReturn(List.of(item));
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(500L);
            return o;
        });

        List<OrderResponseDto> orders = cartService.checkout(testUser);

        assertNotNull(orders);
        assertEquals(1, orders.size());
        assertEquals("PAID", orders.get(0).getStatus());

        assertEquals(new BigDecimal("100000.00"), testUser.getBalance());

        assertEquals(3, testProduct.getStockQuantity());

        verify(userRepository, times(1)).save(testUser);
        verify(productRepository, times(1)).save(testProduct);
        verify(cartItemRepository, times(1)).deleteByUser(testUser);
    }

    @Test
    @DisplayName("Ошибка Checkout: пустая корзина")
    void checkout_EmptyCart_ThrowsException(){
        when(cartItemRepository.findByUser(testUser)).thenReturn(Collections.emptyList());

        assertThrows(IllegalStateException.class, () -> cartService.checkout(testUser));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Ошибка Checkout: недостаточно средств на балансе")
    void checkout_InsufficientFunds_ThrowsException(){
        testUser.setBalance(new BigDecimal("50000.00")); // Товар стоит 400000 (2 * 200000)

        CartItem item = new CartItem(testUser, testProduct, 2);
        item.setId(100L);

        when(cartItemRepository.findByUser(testUser)).thenReturn(List.of(item));
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        assertThrows(InsufficientFundsException.class, () -> cartService.checkout(testUser));
        verify(orderRepository, never()).save(any(Order.class));
        verify(cartItemRepository, never()).deleteByUser(any());
    }
}