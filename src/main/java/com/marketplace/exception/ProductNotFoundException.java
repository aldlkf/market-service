package com.marketplace.exception;

public class ProductNotFoundException extends MarketplaceException{
    public ProductNotFoundException(Long productId) {
        super("Product with ID"+ productId + "not found");
    }
}
