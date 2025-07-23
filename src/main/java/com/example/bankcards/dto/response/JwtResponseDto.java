package com.example.bankcards.dto.response;


import java.util.UUID;

public record JwtResponseDto(
        String accessToken,
        UUID userId
) {
}
