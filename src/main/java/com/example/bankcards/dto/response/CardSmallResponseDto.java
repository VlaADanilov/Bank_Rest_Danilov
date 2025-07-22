package com.example.bankcards.dto.response;

import com.example.bankcards.entity.enums.CardStatus;

public record CardSmallResponseDto(
        String cardNumber,
        CardStatus cardStatus
) {
}
