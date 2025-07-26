package com.example.bankcards.controller;

import com.example.bankcards.controller.api.UserApi;
import com.example.bankcards.dto.request.LoginRequestDto;
import com.example.bankcards.dto.request.UserRequestDto;
import com.example.bankcards.dto.response.JwtResponseDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController implements UserApi {
    private final AuthService authService;
    private final UserService userService;

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Page<UserResponseDto> getUsers(int page, int size, String partOfUsername) {
        log.debug("Getting users - page: %s, size: %s, partOfUsername: %s".formatted(page, size, partOfUsername));
        Page<UserResponseDto> users = userService.getUsers(PageRequest.of(page,size), partOfUsername);
        log.debug("Successfully retrieved %s users".formatted(users.getTotalElements()));
        return users;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteUser(UUID id) {
        log.info("Deleting user with ID: %s".formatted(id));
        userService.deleteUser(id);
        log.info("Successfully deleted user with ID: %s".formatted(id));
    }

    @Override
    public UUID createUser(UserRequestDto requestDto) {
        log.info("Creating new user with username: %s".formatted(requestDto.username()));
        UUID userId = authService.register(requestDto);
        log.info("Successfully created user with ID: %s and username: %s".formatted(userId, requestDto.username()));
        return userId;
    }

    @Override
    public JwtResponseDto login(LoginRequestDto loginRequestDto) {
        log.info("User login attempt for username: %s".formatted(loginRequestDto.username()));
        JwtResponseDto jwtResponse = authService.login(loginRequestDto);
        log.info("Successfully authenticated user: %s".formatted(loginRequestDto.username()));
        return jwtResponse;
    }
}