package com.example.bankcards.controller.api;

import com.example.bankcards.dto.request.CardTransferRequestDto;
import com.example.bankcards.dto.response.CardHugeResponseDto;
import com.example.bankcards.dto.response.CardSmallResponseDto;
import com.example.bankcards.entity.enums.CardStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/card")
@Validated
@Tag(name = "CardControllerApi",
        description = "API для работы с картами")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
})
@SecurityRequirement(name = "bearerAuth")
public interface CardApi {

    @Operation(
            summary = "Получить список моих карт",
            description = """
    Этот метод позволяет получить мои карты"""
    )
    @ApiResponse(responseCode = "200", description = "Карты успешно получены")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    Page<CardSmallResponseDto> getMyCards(
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @RequestParam(defaultValue = "10") int size,
            @RequestParam List<CardStatus> filter);

    @Operation(
            summary = "Получить информацию по карте",
            description = """
    Этот метод позволяет получить детальную информацию по карте"""
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о карте получена"),
            @ApiResponse(responseCode = "404", description = "Карта не существует"),
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    CardHugeResponseDto getById(@PathVariable UUID id);

    @Operation(
            summary = "Запрос на блокировку карты",
            description = """
    Этот метод позволяет отправить запрос на блокировку карты"""
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос успешно отправлен"),
            @ApiResponse(responseCode = "404", description = "Карта не существует"),
    })
    @PostMapping("/block/{id}")
    @ResponseStatus(HttpStatus.OK)
    UUID requestToBlockCard(@PathVariable("id") UUID id);

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Отправить деньги между своими счетами",
            description = """
    Этот метод позволяет отправить деньги между своими счетами"""
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Деньги успешно переведены"),
            @ApiResponse(responseCode = "418", description = "Недостаточно средств для совершения операции"),
    })
    void transferMoney(@Valid CardTransferRequestDto requestDto);

    @Operation(
            summary = "Удалить карту",
            description = """
    Этот метод позволяет удалить карту"""
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Карта не существует"),
    })
    @DeleteMapping("/{id}")
    void deleteCard(@PathVariable("id") UUID id);
}
