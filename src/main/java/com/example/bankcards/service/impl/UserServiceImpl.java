package com.example.bankcards.service.impl;

import com.example.bankcards.dto.request.UserFilterRequestDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public Page<UserResponseDto> getUsers(PageRequest of, String partOfUsername) {
        if (partOfUsername == null || partOfUsername.isEmpty()) {
            return userRepository
                    .findAll(of).map(userMapper::toResponseDto);
        } else {
            return userRepository
                    .findByUsernameLikeIgnoreCase(
                            "%" + partOfUsername.toLowerCase() + "%", of)
                    .map(userMapper::toResponseDto);
        }
    }
}
