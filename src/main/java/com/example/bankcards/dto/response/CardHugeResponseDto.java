package com.example.bankcards.dto.response;

import com.example.bankcards.entity.enums.CardStatus;

import java.time.LocalDate;

public record CardHugeResponseDto(
        String cardNumb,
        LocalDate expiresAt,
        CardStatus status,
        Integer money
) {
}
