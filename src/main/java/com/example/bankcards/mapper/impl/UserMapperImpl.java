package com.example.bankcards.mapper.impl;

import com.example.bankcards.dto.request.UserRequestDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserMapperImpl implements UserMapper {
    @Override
    public User toEntity(UserRequestDto requestDto) {
        log.debug("Mapping UserRequestDto to User entity, username: %s".formatted(requestDto.username()));

        User user = User.builder()
                .username(requestDto.username())
                .firstName(requestDto.firstName())
                .lastName(requestDto.lastName())
                .patronymicName(requestDto.patronymicName())
                .build();

        log.debug("Successfully mapped UserRequestDto to User entity with username: %s".formatted(requestDto.username()));
        return user;
    }

    @Override
    public UserResponseDto toResponseDto(User user) {
        log.debug("Mapping User entity to UserResponseDto, ID: %s, username: %s".formatted(user.getId(), user.getUsername()));

        UserResponseDto dto = UserResponseDto.builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .patronymic(user.getPatronymicName())
                .role(user.getRole())
                .id(user.getId())
                .build();

        log.debug("Successfully mapped User entity to UserResponseDto, ID: %s".formatted(user.getId()));
        return dto;
    }
}