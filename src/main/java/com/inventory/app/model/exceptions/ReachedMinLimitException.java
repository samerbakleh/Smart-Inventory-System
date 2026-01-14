package com.inventory.app.model.exceptions;

public class ReachedMinLimitException extends RuntimeException {
    public ReachedMinLimitException(String message) {
        super(message);
    }
}
