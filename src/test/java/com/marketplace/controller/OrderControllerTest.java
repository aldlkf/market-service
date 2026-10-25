package com.marketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.dto.OrderRequest;
import com.marketplace.dto.OrderResponseDto;
import com.marketplace.model.Order;
import com.marketplace.model.Product;
import com.marketplace.model.User;
import com.marketplace.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        // Поднимаем чистый MockMvc для конкретного контроллера без Spring Context и Security
        this.mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    @DisplayName("POST /api/orders - успешное создание заказа")
    void createOrder_Success() throws Exception {
        OrderRequest request = new OrderRequest(1L, 2);

        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setTitle("SHEP");
        product.setPrice(new BigDecimal("1000.00"));

        Order order = new Order();
        order.setId(10L);
        order.setUser(user);
        order.setProduct(product);
        order.setQuantity(2);
        order.setTotalPrice(new BigDecimal("2000.00"));
        order.setStatus("PAID");
        order.setCreatedAt(LocalDateTime.now());

        OrderResponseDto responseDto = new OrderResponseDto(order);

        when(orderService.createOrder(any(), any(OrderRequest.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    @DisplayName("POST /api/orders - ошибка валидации (пустой запрос)")
    void createOrder_ValidationError_ThrowsBadRequest() throws Exception {
        OrderRequest emptyRequest = new OrderRequest();

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest());
    }
}