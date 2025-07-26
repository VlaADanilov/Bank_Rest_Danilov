package com.example.bankcards.controller;

import com.example.bankcards.controller.api.AdminApi;
import com.example.bankcards.dto.request.CardRequestDto;
import com.example.bankcards.dto.response.CardSmallResponseDto;
import com.example.bankcards.dto.response.RequestToBlockResponseDto;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.BlockService;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminController implements AdminApi {
    private final BlockService blockService;
    private final CardService cardService;

    @Override
    public UUID createCard(CardRequestDto card) {
        log.info("Creating new card for user: %s".formatted(card.userId()));
        UUID cardId = cardService.createCard(card);
        log.info("Successfully created card with ID: %s".formatted(cardId));
        return cardId;
    }

    @Override
    public void blockCard(UUID cardId) {
        log.info("Blocking card with ID: %s".formatted(cardId));
        cardService.block(cardId);
        log.info("Successfully blocked card with ID: %s".formatted(cardId));
    }

    @Override
    public void activeCard(UUID cardId, LocalDate expiryDate) {
        log.info("Activating card with ID: %s, expiry date: %s".formatted(cardId, expiryDate));
        cardService.activate(cardId, expiryDate);
        log.info("Successfully activated card with ID: %s".formatted(cardId));
    }


    @Override
    public Page<CardSmallResponseDto> getCards(int page, int size, List<CardStatus> filter, UUID userId) {
        log.debug("Getting cards - page: %s, size: %s, filter: %s, userId: %s".formatted(page, size, filter, userId));
        Page<CardSmallResponseDto> cards = cardService.getCards(
                PageRequest.of(page, size),
                filter,
                userId);
        log.debug("Successfully retrieved %s cards".formatted(cards.getTotalElements()));
        return cards;
    }

    @Override
    public Page<RequestToBlockResponseDto> getRequests(int page, int size) {
        log.debug("Getting block requests - page: %s, size: %s".formatted(page, size));
        Page<RequestToBlockResponseDto> requests = blockService.getAll(PageRequest.of(page, size));
        log.debug("Successfully retrieved %s block requests".formatted(requests.getTotalElements()));
        return requests;
    }
}
