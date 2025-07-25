package com.example.bankcards.dto.response;

import com.example.bankcards.entity.enums.CardStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CardSmallResponseDto(
        UUID cardId,
        String cardNumber,
        CardStatus cardStatus,
        UUID userId
) {
}
