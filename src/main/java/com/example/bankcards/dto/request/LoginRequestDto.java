package com.example.bankcards.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message = "Username не может быть пустым")
        String username,
        @NotBlank(message = "Password не может быть пустым")
        String password
) {
}
