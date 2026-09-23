package com.marketplace.exception;

public class ProductNotFoundException extends MarketplaceException{
    public ProductNotFoundException(String message) {
        super(message);
    }
}
