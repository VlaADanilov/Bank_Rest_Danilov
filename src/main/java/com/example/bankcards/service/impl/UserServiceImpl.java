package com.example.bankcards.service.impl;

import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public void deleteUser(UUID id) {
        log.info("Deleting user with ID: %s".formatted(id));

        if (!userRepository.existsById(id)) {
            log.warn("Delete user failed: user not found with ID: %s".formatted(id));
            throw new UserNotFoundException(id);
        }

        userRepository.deleteById(id);
        log.info("Successfully deleted user with ID: %s".formatted(id));
    }

    @Override
    public Page<UserResponseDto> getUsers(PageRequest of, String partOfUsername) {
        log.debug("Getting users - page: %s, size: %s, partOfUsername: %s".formatted(of.getPageNumber(), of.getPageSize(), partOfUsername));

        Page<UserResponseDto> users;
        if (partOfUsername == null || partOfUsername.isEmpty()) {
            log.debug("Getting all users without filter");
            users = userRepository
                    .findAll(of).map(userMapper::toResponseDto);
        } else {
            log.debug("Getting users with username filter: %s".formatted(partOfUsername));
            users = userRepository
                    .findByUsernameLikeIgnoreCase(
                            "%" + partOfUsername.toLowerCase() + "%", of)
                    .map(userMapper::toResponseDto);
        }

        log.debug("Successfully retrieved %s users".formatted(users.getTotalElements()));
        return users;
    }
}