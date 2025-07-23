package com.example.bankcards.dto.response;

import com.example.bankcards.entity.enums.CardStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record CardHugeResponseDto(
        String cardNumb,
        LocalDate expiresAt,
        CardStatus status,
        Integer money,
        UUID userId
) {
}
