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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CardController implements CardApi {
    private final BlockService blockService;
    private final UserReturner userReturner;
    private final CardService cardService;

    @Override
    public Page<CardSmallResponseDto> getMyCards(int page, int size, List<CardStatus> filter) {
        return cardService.getCards(PageRequest.of(page, size),
                filter,
                userReturner.getUserId());
    }

    @Override
    public CardHugeResponseDto getById(UUID id) {
        return cardService.getById(id, userReturner.getUserId(), userReturner.getUserRole());
    }

    @Override
    public UUID requestToBlockCard(UUID id) {
        return blockService.blockCard(id, userReturner.getUserId());
    }

    @Override
    public void transferMoney(CardTransferRequestDto requestDto) {
        cardService.transferMoney(requestDto, userReturner.getUserId());
    }

    @Override
    public void deleteCard(UUID id) {
        cardService.deleteById(id, userReturner.getUserId(), userReturner.getUserRole());
    }
}
