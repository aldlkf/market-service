package com.marketplace.dto;

import com.marketplace.model.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderResponseDto {

    private Long id;
    private Long productId;
    private String productTitle;
    private Integer quantity;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime createdAt;

    public OrderResponseDto(){}

    public OrderResponseDto(Order order){
        this.id = order.getId();
        this.productId = order.getProduct() != null ? order.getProduct().getId() : null;
        this.productTitle = order.getProduct() != null ? order.getProduct().getTitle() : null;
        this.quantity = order.getQuantity();
        this.totalPrice = order.getTotalPrice();
        this.status = order.getStatus();
        this.createdAt = order.getCreatedAt();
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Long getProductId(){
        return productId;
    }

    public void setProductId(Long productId){
        this.productId = productId;
    }

    public String getProductTitle(){
        return productTitle;
    }

    public void setProductTitle(String productTitle){
        this.productTitle = productTitle;
    }

    public Integer getQuantity(){
        return quantity;
    }

    public void setQuantity(Integer quantity){
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice(){
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice){
        this.totalPrice = totalPrice;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }

}
