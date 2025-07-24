package com.example.bankcards.controller;

import com.example.bankcards.controller.api.AdminApi;
import com.example.bankcards.dto.request.CardFilterRequestDto;
import com.example.bankcards.dto.request.CardRequestDto;
import com.example.bankcards.dto.response.CardSmallResponseDto;
import com.example.bankcards.dto.response.RequestToBlockResponseDto;
import com.example.bankcards.service.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AdminController implements AdminApi {
    private final BlockService blockService;

    @Override
    public UUID createCard(CardRequestDto card) {
        return null;
    }

    @Override
    public void blockCard(UUID blockRequestId) {

    }

    @Override
    public void activeCard(UUID cardId, LocalDate expiryDate) {

    }

    @Override
    public void deleteCard(UUID id) {

    }

    @Override
    public Page<CardSmallResponseDto> getCards(int page, int size, CardFilterRequestDto filter, UUID userId) {
        return null;
    }

    @Override
    public Page<RequestToBlockResponseDto> getRequests(int page, int size) {
        return blockService.getAll(PageRequest.of(page, size));
    }
}
