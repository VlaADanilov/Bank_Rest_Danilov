package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;

public class InsufficientFundsException extends ServiceException{
    public InsufficientFundsException() {
        super(HttpStatus.BAD_REQUEST, "Insufficient funds to complete the operation!");
    }
}
