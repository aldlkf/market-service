package com.marketplace.dto;

import com.marketplace.model.CartItem;

import java.math.BigDecimal;

public class CartItemResponseDto {

    private Long id;
    private Long productId;
    private String productTitle;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;

    public CartItemResponseDto() {
    }

    public CartItemResponseDto(CartItem item) {
        this.id = item.getId();
        if (item.getProduct() != null) {
            this.productId = item.getProduct().getId();
            this.productTitle = item.getProduct().getTitle();
            this.price = item.getProduct().getPrice();
            if (item.getProduct().getPrice() != null && item.getQuantity() != null) {
                this.subtotal = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            }
        }
        this.quantity = item.getQuantity();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}