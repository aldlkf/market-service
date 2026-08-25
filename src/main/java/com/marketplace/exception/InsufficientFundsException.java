package com.marketplace.exception;

public class InsufficientFundsException extends MarketplaceException{
    public InsufficientFundsException(String message){
        super(message);
    }
}
