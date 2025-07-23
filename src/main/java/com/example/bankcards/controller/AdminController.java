package com.example.bankcards.controller;

import com.example.bankcards.controller.api.AdminApi;
import com.example.bankcards.dto.request.CardFilterRequestDto;
import com.example.bankcards.dto.request.CardRequestDto;
import com.example.bankcards.dto.response.CardSmallResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
public class AdminController implements AdminApi {
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
}
