package com.example.bankcards.dto.request;

import com.example.bankcards.util.validation.ValidCardNumber;
import com.example.bankcards.util.validation.ValidDate;

import java.time.LocalDate;
import java.util.UUID;


public record CardRequestDto(
        @ValidCardNumber
        String cardNumber,
        @ValidDate
        LocalDate expiryDate,
        UUID userId
) {
}
