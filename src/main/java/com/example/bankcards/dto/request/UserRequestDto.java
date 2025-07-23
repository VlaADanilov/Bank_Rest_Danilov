package com.example.bankcards.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDto(
        @NotBlank @Size(min = 2, max = 20)
        String username,
        @NotBlank @Size(min = 2, max = 20)
        String password,
        @NotBlank @Size(min = 2, max = 20)
        String firstName,
        @NotBlank @Size(min = 2, max = 20)
        String lastName,
        @NotBlank @Size(min = 2, max = 20)
        String patronymicName
) {
}
