package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;

public class RequestAlreadyExistsException extends ServiceException {
    public RequestAlreadyExistsException() {
        super(HttpStatus.BAD_REQUEST, "Request to block this card already exists");
    }
}
