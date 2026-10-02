package com.marketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.dto.OrderRequest;
import com.marketplace.model.Order;
import com.marketplace.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    @DisplayName("POST /api/orders - Успешное создание заказа (201 Created)")
    void createOrder_Success() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setUserId(1L);
        request.setProductId(1L);
        request.setQuantity(2);

        Order mockOrder = new Order();
        mockOrder.setId(100L);
        mockOrder.setQuantity(2);
        mockOrder.setTotalPrice(new BigDecimal("700000.00"));
        mockOrder.setStatus("PAID");

        when(orderService.createOrder(anyLong(), anyLong(), anyInt())).thenReturn(mockOrder);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    @DisplayName("POST /api/orders - Ошибка валидации, пустое тело (400 Bad Request)")
    void createOrder_ValidationError_ThrowsBadRequest() throws Exception {
        OrderRequest invalidRequest = new OrderRequest();
        invalidRequest.setUserId(1L);
        invalidRequest.setProductId(1L);
        invalidRequest.setQuantity(0);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.quantity").value("Количество товара должно быть не менее 1"));
    }
}