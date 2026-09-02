package com.marketplace.service;

import com.marketplace.exception.InsufficientFundsException;
import com.marketplace.exception.UserNotFoundException;
import com.marketplace.model.Order;
import com.marketplace.model.Product;
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
    void setUp(){
        testUser = new User(1L, "Ильяс", "ilyas@example.com", new BigDecimal("500000.00"));
        testProduct = new Product(1L, "RTX 5070", new BigDecimal("350000.00"), 5);
    }

    @Test
    @DisplayName("Успешное создание заказа: списание баланса и товара")
    void createOrder_Success(){
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order createOrder = orderService.createOrder(1L,1L,1);

        assertNotNull(createOrder);
        assertEquals(0, new BigDecimal("150000.00").compareTo(testUser.getBalance()));
        assertEquals(4, testProduct.getStockQuantity());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Ошибка создания заказа: недостаточно средств на балансе")
    void createOrder_InsufficientFunds_ThrowsException(){
        User poorUser = new User(2L,"Бедный клиент","poor@exapmle.com",new BigDecimal("10000.00"));
        when(userRepository.findById(2L)).thenReturn(Optional.of(poorUser));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        assertThrows(InsufficientFundsException.class, () -> orderService.createOrder(2L, 1L, 1));
        verify(orderRepository, never()).save(any(Order.class));
    }

}
