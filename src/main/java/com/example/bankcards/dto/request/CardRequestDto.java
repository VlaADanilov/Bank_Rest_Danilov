package com.example.bankcards.dto.request;

import java.time.LocalDate;
import java.util.UUID;

public record CardRequestDto(
        String cardNumber,
        LocalDate expiryDate,
        UUID userId
) {
}
