package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;

public class CardAlreadyExistsException extends ServiceException {
    public CardAlreadyExistsException(String cardNumber) {
        super(HttpStatus.BAD_REQUEST,
                "Card number %s already exists in bank".formatted(cardNumber));
    }
}
