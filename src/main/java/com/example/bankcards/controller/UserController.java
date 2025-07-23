package com.example.bankcards.controller;

import com.example.bankcards.controller.api.UserApi;
import com.example.bankcards.dto.request.LoginRequestDto;
import com.example.bankcards.dto.request.UserFilterRequestDto;
import com.example.bankcards.dto.request.UserRequestDto;
import com.example.bankcards.dto.response.JwtResponseDto;
import com.example.bankcards.dto.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UserController implements UserApi {
    @Override
    public Page<UserResponseDto> getUsers(int page, int size, UserFilterRequestDto filter) {
        return null;
    }

    @Override
    public void deleteUser(UUID id) {

    }

    @Override
    public UUID createUser(UserRequestDto requestDto) {
        return null;
    }

    @Override
    public JwtResponseDto login(LoginRequestDto loginRequestDto) {
        return null;
    }
}
