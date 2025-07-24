package com.example.bankcards.service;

import com.example.bankcards.dto.request.CardRequestDto;
import com.example.bankcards.dto.request.CardTransferRequestDto;
import com.example.bankcards.dto.response.CardHugeResponseDto;
import com.example.bankcards.dto.response.CardSmallResponseDto;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CardService {
    Page<CardSmallResponseDto> getCards(Pageable of,
                                        List<CardStatus> filter,
                                        UUID userId);

    CardHugeResponseDto getById(UUID cardId, UUID userId, Role userRole);

    void transferMoney(CardTransferRequestDto requestDto, UUID userId);

    void deleteById(UUID cardId, UUID userId, Role role);

    UUID createCard(CardRequestDto card);

    void block(UUID cardId);

    void activate(UUID cardId, LocalDate expiryDate);
}
