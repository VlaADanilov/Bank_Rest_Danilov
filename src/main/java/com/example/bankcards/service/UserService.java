package com.example.bankcards.service;

import com.example.bankcards.dto.request.UserFilterRequestDto;
import com.example.bankcards.dto.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface UserService {
    void deleteUser(UUID id);

    Page<UserResponseDto> getUsers(PageRequest of, UserFilterRequestDto filter);
}
