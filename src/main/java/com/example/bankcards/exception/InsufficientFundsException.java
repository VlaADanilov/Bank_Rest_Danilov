package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;

public class InsufficientFundsException extends ServiceException{
    public InsufficientFundsException() {
        super(HttpStatus.I_AM_A_TEAPOT, "Insufficient funds to complete the operation!");
    }
}
