package com.marketplace.dto;

import com.marketplace.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Запрос на смену статуса заказа")
public class UpdateOrderStatusRequest {

    @NotNull(message = "Статус обязателен")
    @Schema(description = "Новый статус заказа", example = "SHIPPED")
    private OrderStatus status;

    public UpdateOrderStatusRequest(){}

    public UpdateOrderStatusRequest(OrderStatus status){
        this.status = status;
    }

    public OrderStatus getStatus(){
        return status;
    }

    public void setStatus(OrderStatus status){
        this.status = status;
    }
}
