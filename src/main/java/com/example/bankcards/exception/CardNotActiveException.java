package com.example.bankcards.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class CardNotActiveException extends ServiceException {
    public CardNotActiveException(UUID cardId) {
        super(HttpStatus.I_AM_A_TEAPOT,
                "card with id %s not available!".formatted(cardId.toString()));

    }
}
