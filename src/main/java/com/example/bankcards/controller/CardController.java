package com.example.bankcards.controller;

import com.example.bankcards.controller.api.CardApi;
import com.example.bankcards.dto.request.CardFilterRequestDto;
import com.example.bankcards.dto.request.CardTransferRequestDto;
import com.example.bankcards.dto.response.CardHugeResponseDto;
import com.example.bankcards.dto.response.CardSmallResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class CardController implements CardApi {
    @Override
    public Page<CardSmallResponseDto> getMyCards(int page, int size, CardFilterRequestDto filter) {
        return null;
    }

    @Override
    public CardHugeResponseDto getById(UUID id) {
        return null;
    }

    @Override
    public void blockCard(UUID id) {

    }

    @Override
    public UUID transferMoney(CardTransferRequestDto requestDto) {
        return null;
    }
}
