package com.example.bankcards.mapper.impl;

import com.example.bankcards.dto.request.UserRequestDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {
    @Override
    public User toEntity(UserRequestDto requestDto) {
        return User.builder()
                .username(requestDto.username())
                .firstName(requestDto.firstName())
                .lastName(requestDto.lastName())
                .patronymicName(requestDto.patronymicName())
                .build();
    }

    @Override
    public UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .patronymic(user.getPatronymicName())
                .role(user.getRole())
                .id(user.getId())
                .build();
    }
}
