package com.example.bankcards.service.impl;

import com.example.bankcards.dto.request.LoginRequestDto;
import com.example.bankcards.dto.request.UserRequestDto;
import com.example.bankcards.dto.response.JwtResponseDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.InvalidPasswordException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.exception.UsernameAlreadyExistsException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.util.WorkWithJwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final WorkWithJwt jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UUID register(UserRequestDto registerRequestDto) {
        log.info("Registering new user with username: %s".formatted(registerRequestDto.username()));

        if (userRepository.existsByUsername(registerRequestDto.username())) {
            log.warn("Registration failed: username already exists: %s".formatted(registerRequestDto.username()));
            throw new UsernameAlreadyExistsException(registerRequestDto.username());
        }

        User entity = userMapper.toEntity(registerRequestDto);
        entity.setPassword(passwordEncoder.encode(registerRequestDto.password()));
        log.debug("Password encoded for user: %s".formatted(registerRequestDto.username()));

        userRepository.save(entity);
        log.info("Successfully registered user with ID: %s and username: %s".formatted(entity.getId(), registerRequestDto.username()));

        return entity.getId();
    }

    @Override
    public JwtResponseDto login(LoginRequestDto loginRequestDto) {
        log.info("Login attempt for username: %s".formatted(loginRequestDto.username()));

        User user = userRepository.findByUsername(loginRequestDto.username())
                .orElseThrow(() -> {
                    log.warn("Login failed: user not found: %s".formatted(loginRequestDto.username()));
                    return new UserNotFoundException(loginRequestDto.username());
                });

        if (!passwordEncoder.matches(loginRequestDto.password(), user.getPassword())) {
            log.warn("Login failed: invalid password for user: %s".formatted(loginRequestDto.username()));
            throw new InvalidPasswordException();
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole());
        log.info("Successfully authenticated user: %s with ID: %s".formatted(user.getUsername(), user.getId()));

        return new JwtResponseDto(accessToken, user.getId());
    }
}