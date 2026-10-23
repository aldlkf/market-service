package com.marketplace.model;

import java.util.List;

public enum OrderStatus {
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELED;

    public boolean canTransitionTo(OrderStatus nextStatus){
        return switch (this){
            case PAID -> nextStatus == SHIPPED || nextStatus == CANCELED;
            case SHIPPED -> nextStatus == DELIVERED;
            case DELIVERED, CANCELED -> false;
        };
    }
}
