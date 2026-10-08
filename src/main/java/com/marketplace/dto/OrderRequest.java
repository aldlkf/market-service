package com.marketplace.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class OrderRequest {

    @NotNull(message = "ID товара не может быть пустым")
    private Long productId;

    @NotNull(message = "Количество товара должно быть указано")
    @Min(value = 1, message = "Количество товара должно быть не менее 1")
    private Integer quantity;

    public OrderRequest() {
    }

    public OrderRequest(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
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