package com.marketplace.exception;

public class ProductOutOfStockException extends MarketplaceException {
    public ProductOutOfStockException(String message) {
        super(message);
    }
}