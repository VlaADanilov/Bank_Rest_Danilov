package com.example.bankcards.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CardTransferRequestDto(
        @NotNull
        UUID fromCard,
        @NotNull
        UUID toCard,
        @Min(1)
        Integer money
) {
}
