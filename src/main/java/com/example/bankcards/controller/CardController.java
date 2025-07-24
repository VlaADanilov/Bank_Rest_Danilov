package com.example.bankcards.controller;

import com.example.bankcards.controller.api.CardApi;
import com.example.bankcards.dto.request.CardFilterRequestDto;
import com.example.bankcards.dto.request.CardTransferRequestDto;
import com.example.bankcards.dto.response.CardHugeResponseDto;
import com.example.bankcards.dto.response.CardSmallResponseDto;
import com.example.bankcards.service.BlockService;
import com.example.bankcards.util.UserReturner;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CardController implements CardApi {
    private final BlockService blockService;
    private final UserReturner userReturner;

    @Override
    public Page<CardSmallResponseDto> getMyCards(int page, int size, CardFilterRequestDto filter) {
        return null;
    }

    @Override
    public CardHugeResponseDto getById(UUID id) {
        return null;
    }

    @Override
    public UUID requestToBlockCard(UUID id) {
        return blockService.blockCard(id, userReturner.getUserId());
    }

    @Override
    public UUID transferMoney(CardTransferRequestDto requestDto) {
        return null;
    }
}
