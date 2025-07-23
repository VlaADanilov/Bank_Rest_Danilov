package com.example.bankcards.controller.api;

import com.example.bankcards.dto.request.LoginRequestDto;
import com.example.bankcards.dto.request.UserFilterRequestDto;
import com.example.bankcards.dto.request.UserRequestDto;
import com.example.bankcards.dto.response.JwtResponseDto;
import com.example.bankcards.dto.response.UserResponseDto;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/v1/user")
public interface UserApi {
    @GetMapping()
    Page<UserResponseDto> getUsers(
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @RequestParam(defaultValue = "1") int size,
            UserFilterRequestDto filter
    );

    @DeleteMapping("/{id}")
    void deleteUser(@PathVariable("id") UUID id);

    @PostMapping
    UUID createUser(UserRequestDto requestDto);

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    JwtResponseDto login(@RequestBody
                         LoginRequestDto loginRequestDto);
}
