package com.example.bankcards.controller.api;

import com.example.bankcards.dto.request.LoginRequestDto;
import com.example.bankcards.dto.request.UserFilterRequestDto;
import com.example.bankcards.dto.request.UserRequestDto;
import com.example.bankcards.dto.response.JwtResponseDto;
import com.example.bankcards.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
})
@RequestMapping("/api/v1/user")
@Validated
@Tag(name = "UserControllerApi",
        description = "API для работы с пользователями и авторизацией")
public interface UserApi {

    @GetMapping
    @Operation(
            summary = "Получение пользователей",
            description = """
    Этот метод позволяет получить список пользователей с необязательной фильтрацией по username"""
    )
    @ApiResponse(responseCode = "200", description = "Пользователи успешно получены")
    Page<UserResponseDto> getUsers(
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @RequestParam(defaultValue = "1") int size,
            UserFilterRequestDto filter
    );

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удаление профиля пользователя",
            description = "Этот метод позволяет удалить аккаунт пользователя из системы"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь удалён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
    })
    void deleteUser(@PathVariable("id") UUID id);

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Регистрация пользователя в системе",
            description = "Этот метод позволяет зарегистрироваться в системе"
    )
    @ApiResponse(responseCode = "200", description = "Аккаунт создан")
    UUID createUser(@Valid UserRequestDto requestDto);

    @PostMapping("/login")

    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Вход в систему",
            description = "Этот метод позволяет войти в систему и получить JWT-токен"
    )
    @ApiResponse(responseCode = "200", description = "Вход выполнен успешно")
    JwtResponseDto login(@RequestBody @Valid
                         LoginRequestDto loginRequestDto);
}
