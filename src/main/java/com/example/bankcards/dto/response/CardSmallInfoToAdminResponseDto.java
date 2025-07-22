package com.example.bankcards.dto.response;

import com.example.bankcards.entity.enums.CardStatus;

import java.util.UUID;

public record CardSmallInfoToAdminResponseDto(
        String cardNumber,
        CardStatus cardStatus,
        UUID userId
) {
}
