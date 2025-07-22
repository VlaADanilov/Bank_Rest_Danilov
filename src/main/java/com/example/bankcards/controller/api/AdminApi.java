package com.example.bankcards.controller.api;

import com.example.bankcards.dto.request.CardRequestDto;
import com.example.bankcards.dto.response.CardSmallInfoToAdminResponseDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.enums.CardStatus;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/admin")
public interface AdminApi {
    @PostMapping("/card")
    UUID createCard(@RequestBody CardRequestDto card);

    @PostMapping("/block/{id}")
    void blockCard(@PathVariable("id") UUID blockRequestId);

    @DeleteMapping("/card/{id}")
    void deleteCard(@PathVariable("id") UUID id);

    @GetMapping("/card")
    Page<CardSmallInfoToAdminResponseDto> getCards(
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Min(1) @RequestParam(defaultValue = "1") int size,
            @RequestParam(value = "status") List<CardStatus> filter);


}
