package com.example.bankcards.controller;

import com.example.bankcards.controller.api.CardApi;
import com.example.bankcards.dto.request.CardTransferRequestDto;
import com.example.bankcards.dto.response.CardHugeResponseDto;
import com.example.bankcards.dto.response.CardSmallResponseDto;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.BlockService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.UserReturner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CardController implements CardApi {
    private final BlockService blockService;
    private final UserReturner userReturner;
    private final CardService cardService;

    @Override
    public Page<CardSmallResponseDto> getMyCards(int page, int size, List<CardStatus> filter) {
        UUID userId = userReturner.getUserId();
        log.debug("Getting cards for user: %s, page: %s, size: %s, filter: %s".formatted(userId, page, size, filter));
        Page<CardSmallResponseDto> cards = cardService.getCards(PageRequest.of(page, size),
                filter,
                userId);
        log.debug("Successfully retrieved %s cards for user: %s".formatted(cards.getTotalElements(), userId));
        return cards;
    }

    @Override
    public CardHugeResponseDto getById(UUID id) {
        UUID userId = userReturner.getUserId();
        log.debug("Getting card by ID: %s for user: %s".formatted(id, userId));
        CardHugeResponseDto card = cardService.getById(id, userId, userReturner.getUserRole());
        log.debug("Successfully retrieved card by ID: %s for user: %s".formatted(id, userId));
        return card;
    }

    @Override
    public UUID requestToBlockCard(UUID id) {
        UUID userId = userReturner.getUserId();
        log.info("User: %s requesting to block card: %s".formatted(userId, id));
        UUID requestId = blockService.blockCard(id, userId);
        log.info("Successfully created block request: %s for card: %s by user: %s".formatted(requestId, id, userId));
        return requestId;
    }

    @Override
    public void transferMoney(CardTransferRequestDto requestDto) {
        UUID userId = userReturner.getUserId();
        log.info("User: %s initiating money transfer from card: %s to card: %s, amount: %s".formatted(
                userId, requestDto.fromCard(), requestDto.toCard(), requestDto.money()));
        cardService.transferMoney(requestDto, userId);
        log.info("Successfully completed money transfer for user: %s".formatted(userId));
    }

    @Override
    public void deleteCard(UUID id) {
        UUID userId = userReturner.getUserId();
        log.info("User: %s requesting to delete card: %s".formatted(userId, id));
        cardService.deleteById(id, userId, userReturner.getUserRole());
        log.info("Successfully deleted card: %s by user: %s".formatted(id, userId));
    }
}