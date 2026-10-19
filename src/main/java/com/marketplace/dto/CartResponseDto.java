package com.marketplace.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartResponseDto {

    private List<CartItemResponseDto> items;
    private BigDecimal totalAmount;

    public CartResponseDto(){}

    public CartResponseDto(List<CartItemResponseDto> items, BigDecimal totalAmount){
        this.items = items;
        this.totalAmount = totalAmount;
    }

    public List<CartItemResponseDto> getItems(){
        return items;
    }

    public void setItems(List<CartItemResponseDto> items){
        this.items = items;
    }

    public BigDecimal getTotalAmount(){
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount){
        this.totalAmount = totalAmount;
    }
}