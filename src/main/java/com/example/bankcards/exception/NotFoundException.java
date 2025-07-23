package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;

public abstract class NotFoundException extends ServiceException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
