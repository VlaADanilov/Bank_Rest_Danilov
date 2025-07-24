package com.example.bankcards.controller.api;

import com.example.bankcards.dto.request.CardFilterRequestDto;
import com.example.bankcards.dto.request.CardRequestDto;
import com.example.bankcards.dto.response.CardSmallResponseDto;
import com.example.bankcards.dto.response.RequestToBlockResponseDto;
import com.example.bankcards.util.validation.ValidDate;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RequestMapping("/api/v1/admin")
public interface AdminApi {
    @PostMapping("/card")
    UUID createCard(@RequestBody CardRequestDto card);

    @PostMapping("/block/{id}")
    void blockCard(@PathVariable("id") UUID cardId);

    @PostMapping("/active/{id}")
    void activeCard(@PathVariable("id") UUID cardId,
                    @ValidDate @RequestParam() LocalDate expiryDate);

    @GetMapping("/card")
    Page<CardSmallResponseDto> getCards(
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @RequestParam(defaultValue = "1") int size,
            CardFilterRequestDto filter,
            @RequestParam(required = false) UUID userId);

    @GetMapping("/requestsToBlock")
    Page<RequestToBlockResponseDto> getRequests(
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @RequestParam(defaultValue = "1") int size
    );
}
