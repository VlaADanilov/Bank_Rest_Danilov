package com.example.bankcards.controller;

import com.example.bankcards.controller.api.AdminApi;
import com.example.bankcards.dto.request.CardFilterRequestDto;
import com.example.bankcards.dto.request.CardRequestDto;
import com.example.bankcards.dto.response.CardSmallResponseDto;
import com.example.bankcards.dto.response.RequestToBlockResponseDto;
import com.example.bankcards.service.BlockService;
import com.example.bankcards.service.CardService;
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
    private final CardService cardService;

    @Override
    public UUID createCard(CardRequestDto card) {
        return cardService.createCard(card);
    }

    @Override
    public void blockCard(UUID cardId) {
        cardService.block(cardId);
    }

    @Override
    public void activeCard(UUID cardId, LocalDate expiryDate) {
        cardService.activate(cardId, expiryDate);
    }


    @Override
    public Page<CardSmallResponseDto> getCards(int page, int size, CardFilterRequestDto filter, UUID userId) {
        return cardService.getCards(
                PageRequest.of(page, size),
                filter,
                userId);
    }

    @Override
    public Page<RequestToBlockResponseDto> getRequests(int page, int size) {
        return blockService.getAll(PageRequest.of(page, size));
    }
}
