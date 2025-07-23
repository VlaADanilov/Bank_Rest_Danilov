package com.example.bankcards.exception;

import java.util.UUID;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String username) {
        super("User with username %s not found".formatted(username));
    }

    public UserNotFoundException(UUID uuid) {
        super("User with id %s not found".formatted(uuid.toString()));
    }
}
