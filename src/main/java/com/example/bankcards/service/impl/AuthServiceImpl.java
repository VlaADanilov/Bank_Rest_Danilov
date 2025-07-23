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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final WorkWithJwt jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UUID register(UserRequestDto registerRequestDto) {
        if (userRepository.existsByUsername(registerRequestDto.username())) {
            throw new UsernameAlreadyExistsException(
                    registerRequestDto.username());
        }
        User entity = userMapper.toEntity(registerRequestDto);
        entity.setPassword(passwordEncoder.encode(registerRequestDto.password()));
        userRepository.save(entity);
        return entity.getId();
    }

    @Override
    public JwtResponseDto login(LoginRequestDto loginRequestDto) {
        User user =
                userRepository.findByUsername(loginRequestDto.username())
                        .orElseThrow(() -> new UserNotFoundException(loginRequestDto.username()));
        if (!passwordEncoder.matches(loginRequestDto.password(), user.getPassword())) {
            throw new InvalidPasswordException();
        }
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole());
        return new JwtResponseDto(accessToken, user.getId());
    }
}
