package com.example.bankcards.controller.api;

import com.example.bankcards.dto.request.CardRequestDto;
import com.example.bankcards.dto.response.CardSmallResponseDto;
import com.example.bankcards.dto.response.RequestToBlockResponseDto;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.util.validation.ValidDate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
})
@RequestMapping("/api/v1/admin")
@Validated
@Tag(name = "AdminControllerApi",
        description = "API для работы администратора")
@SecurityRequirement(name = "bearerAuth")
public interface AdminApi {

    @PostMapping("/card")
    @Operation(
            summary = "Создание карты пользователю",
            description = """
    Этот метод позволяет создать карту для пользователя"""
    )
    @ApiResponse(responseCode = "200", description = "Карта успешно создана")
    UUID createCard(@RequestBody @Valid CardRequestDto card);

    @PostMapping("/block/{id}")
    @Operation(
            summary = "Заблокировать карту",
            description = """
    Этот метод позволяет заблокировать карту пользователя"""
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта успешно заблокирована"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена"),
    })
    void blockCard(@PathVariable("id") UUID cardId);

    @PostMapping("/active/{id}")
    @Operation(
            summary = "Активировать карту",
            description = """
    Этот метод позволяет активировать карту пользователя, которая заблокирована или просрочена"""
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта успешно активирована"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена"),
    })
    void activeCard(@PathVariable("id") UUID cardId,
                    @ValidDate @RequestParam() LocalDate expiryDate);

    @GetMapping("/card")
    @Operation(
            summary = "Получить список карт",
            description = """
    Этот метод позволяет получить список карт общий, или по конкретному пользователю
    с фильтрацией по статусам"""
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карты успешно получены"),
            @ApiResponse(responseCode = "404", description = "Пользователь не существует"),
    })
    Page<CardSmallResponseDto> getCards(
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @RequestParam(defaultValue = "10") int size,
            @RequestParam List<CardStatus> filter,
            @RequestParam(required = false) UUID userId);

    @Operation(
            summary = "Получить список запросов на блокировку",
            description = """
    Этот метод позволяет получить список запросов на блокировку карт"""
    )
    @ApiResponse(responseCode = "200", description = "Запросы успешно получены")
    @GetMapping("/requestsToBlock")
    Page<RequestToBlockResponseDto> getRequests(
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @RequestParam(defaultValue = "10") int size
    );
}
