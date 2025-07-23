package com.example.bankcards.mapper;

import com.example.bankcards.dto.request.UserRequestDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.User;

public interface UserMapper {
    User toEntity(UserRequestDto requestDto);

    UserResponseDto toResponseDto(User user);
}
