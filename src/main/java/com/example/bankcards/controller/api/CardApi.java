package com.example.bankcards.controller.api;

import com.example.bankcards.dto.request.CardTransferRequestDto;
import com.example.bankcards.dto.response.CardHugeResponseDto;
import com.example.bankcards.dto.response.CardSmallResponseDto;
import com.example.bankcards.entity.enums.CardStatus;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/card")
public interface CardApi {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    Page<CardSmallResponseDto> getMyCards(
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @RequestParam(defaultValue = "1") int size,
            @RequestParam(value = "status") List<CardStatus> filter);

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    CardHugeResponseDto getById(@PathVariable UUID id);

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    void deleteCard(@PathVariable("id") UUID id);

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.OK)
    UUID transferMoney(CardTransferRequestDto requestDto);

}
