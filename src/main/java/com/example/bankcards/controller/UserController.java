package com.example.bankcards.controller;

import com.example.bankcards.controller.api.UserApi;
import com.example.bankcards.dto.request.LoginRequestDto;
import com.example.bankcards.dto.request.UserFilterRequestDto;
import com.example.bankcards.dto.request.UserRequestDto;
import com.example.bankcards.dto.response.JwtResponseDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {
    private final AuthService authService;
    private final UserService userService;

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Page<UserResponseDto> getUsers(int page, int size, UserFilterRequestDto filter) {
        return userService.getUsers(PageRequest.of(page,size), filter);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteUser(UUID id) {
        userService.deleteUser(id);
    }

    @Override
    public UUID createUser(@Valid UserRequestDto requestDto) {
        return authService.register(requestDto);
    }

    @Override
    public JwtResponseDto login(@Valid LoginRequestDto loginRequestDto) {
        return authService.login(loginRequestDto);
    }
}
