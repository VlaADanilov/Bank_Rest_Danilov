package com.example.bankcards.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RequestToBlockResponseDto(
        UUID id,
        UUID cardId
) {
}
