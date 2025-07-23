package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends ServiceException {
    public UsernameAlreadyExistsException(String email) {
        super(HttpStatus.BAD_REQUEST,
                "User with username %s already exists".formatted(email));
    }
}
