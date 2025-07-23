package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends ServiceException {
    public InvalidPasswordException() {
        super(HttpStatus.BAD_REQUEST,
                "Invalid password");
    }
}
