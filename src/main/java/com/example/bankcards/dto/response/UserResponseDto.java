package com.example.bankcards.dto.response;

import com.example.bankcards.entity.enums.Role;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponseDto(
        UUID id,
        String username,
        Role role,
        String firstName,
        String lastName,
        String patronymic
) {
}
