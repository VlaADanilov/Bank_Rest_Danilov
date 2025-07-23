package com.example.bankcards.service;

import com.example.bankcards.dto.request.LoginRequestDto;
import com.example.bankcards.dto.request.UserRequestDto;
import com.example.bankcards.dto.response.JwtResponseDto;
import jakarta.validation.Valid;

import java.util.UUID;

public interface AuthService {
    UUID register(@Valid UserRequestDto registerRequestDto);

    JwtResponseDto login(@Valid LoginRequestDto loginRequestDto);
}
