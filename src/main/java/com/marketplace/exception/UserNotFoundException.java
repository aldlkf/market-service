package com.marketplace.exception;

public class UserNotFoundException extends MarketplaceException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
