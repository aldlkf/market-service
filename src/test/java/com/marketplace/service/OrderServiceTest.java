package com.marketplace.service;

import com.marketplace.dto.OrderRequest;
import com.marketplace.dto.OrderResponseDto;
import com.marketplace.exception.AccessDeniedCustomException;
import com.marketplace.exception.IllegalOrderStateException;
import com.marketplace.exception.InsufficientFundsException;
import com.marketplace.exception.ProductNotFoundException;
import com.marketplace.exception.ProductOutOfStockException;
import com.marketplace.model.Order;
import com.marketplace.model.Product;
import com.marketplace.model.Role;
import com.marketplace.model.User;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Ilyas");
        testUser.setEmail("ilyas@example.com");
        testUser.setBalance(new BigDecimal("100000.00"));
        testUser.setRole(Role.USER);

        testProduct = new Product();
        testProduct.setId(10L);
        testProduct.setTitle("Клавиатура");
        testProduct.setPrice(new BigDecimal("20000.00"));
        testProduct.setStockQuantity(5);
    }

    @Test
    @DisplayName("Успешное создание заказа: баланс и остаток списываются корректно")
    void createOrder_Success() {
        OrderRequest request = new OrderRequest();
        request.setProductId(10L);
        request.setQuantity(2);

        when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(100L);
            return o;
        });

        OrderResponseDto response = orderService.createOrder(testUser, request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(new BigDecimal("40000.00"), response.getTotalPrice());
        assertEquals("PAID", response.getStatus());

        assertEquals(new BigDecimal("60000.00"), testUser.getBalance());
        assertEquals(3, testProduct.getStockQuantity());

        verify(userRepository, times(1)).save(testUser);
        verify(productRepository, times(1)).save(testProduct);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Ошибка создания заказа: товар не найден")
    void createOrder_ProductNotFound() {
        OrderRequest request = new OrderRequest();
        request.setProductId(999L);
        request.setQuantity(1);

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> orderService.createOrder(testUser, request));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Ошибка создания заказа: недостаточно товара на складе")
    void createOrder_OutOfStock() {
        OrderRequest request = new OrderRequest();
        request.setProductId(10L);
        request.setQuantity(10);

        when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));

        assertThrows(ProductOutOfStockException.class, () -> orderService.createOrder(testUser, request));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Ошибка создания заказа: недостаточно средств на балансе")
    void createOrder_InsufficientFunds() {
        testUser.setBalance(new BigDecimal("10000.00"));

        OrderRequest request = new OrderRequest();
        request.setProductId(10L);
        request.setQuantity(1);

        when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));

        assertThrows(InsufficientFundsException.class, () -> orderService.createOrder(testUser, request));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Успешная отмена заказа: деньги возвращаются, остаток увеличивается")
    void cancelOrder_Success() {
        Order order = new Order();
        order.setId(100L);
        order.setUser(testUser);
        order.setProduct(testProduct);
        order.setQuantity(2);
        order.setTotalPrice(new BigDecimal("40000.00"));
        order.setStatus("PAID");

        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponseDto response = orderService.cancelOrder(100L, testUser);

        assertEquals("CANCELED", response.getStatus());
        assertEquals(new BigDecimal("140000.00"), testUser.getBalance());
        assertEquals(7, testProduct.getStockQuantity());

        verify(userRepository, times(1)).save(testUser);
        verify(productRepository, times(1)).save(testProduct);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Ошибка отмены заказа: попытка отменить чужой заказ без роли ADMIN")
    void cancelOrder_AccessDenied() {
        User anotherUser = new User();
        anotherUser.setId(99L);
        anotherUser.setRole(Role.USER);

        Order order = new Order();
        order.setId(100L);
        order.setUser(testUser);
        order.setStatus("PAID");

        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        assertThrows(AccessDeniedCustomException.class, () -> orderService.cancelOrder(100L, anotherUser));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Ошибка отмены заказа: заказ уже отменен ранее")
    void cancelOrder_AlreadyCanceled() {
        Order order = new Order();
        order.setId(100L);
        order.setUser(testUser);
        order.setStatus("CANCELED");

        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        assertThrows(IllegalOrderStateException.class, () -> orderService.cancelOrder(100L, testUser));
        verify(orderRepository, never()).save(any(Order.class));
    }
}