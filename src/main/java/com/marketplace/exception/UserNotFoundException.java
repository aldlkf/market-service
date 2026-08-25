package com.marketplace.exception;

public class UserNotFoundException extends MarketplaceException{
    public UserNotFoundException(Long userId) {
        super("User with ID "+ userId + "not found");
    }
}
