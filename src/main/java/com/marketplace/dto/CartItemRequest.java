package com.marketplace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Запрос на добавление/изменение товара в корзине")
public class CartItemRequest {

    @NotNull(message = "ID товара обязателен")
    @Schema(description = "ID товара", example = "1")
    private Long productId;

    @NotNull(message = "Количество обязательно")
    @Min(value = 1, message = "Количество должно быть не менее 1")
    @Schema(description = "Количество", example = "2")
    private Integer quantity;

    public CartItemRequest(){}

    public CartItemRequest(Long productId, Integer quantity){
        this.productId = productId;
        this.quantity =  quantity;
    }

    public Long getProductId(){
        return productId;
    }

    public void setProductId(Long productId){
        this.productId = productId;
    }

    public Integer getQuantity(){
        return quantity;
    }

    public void setQuantity(Integer quantity){
        this.quantity = quantity;
    }

}
