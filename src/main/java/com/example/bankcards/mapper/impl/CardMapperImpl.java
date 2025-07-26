package com.example.bankcards.mapper.impl;

import com.example.bankcards.dto.response.CardHugeResponseDto;
import com.example.bankcards.dto.response.CardSmallResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.mapper.CardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardMapperImpl implements CardMapper {

    @Override
    public CardHugeResponseDto toCardHugeResponseDto(Card card) {
        log.debug("Mapping Card entity to CardHugeResponseDto, ID: %s".formatted(card.getId()));

        CardHugeResponseDto dto = CardHugeResponseDto.builder()
                .cardId(card.getId())
                .cardNumb(getEncryptedCardNumber(card.getCardNumber()))
                .money(card.getBalance())
                .status(card.getStatus())
                .expiresAt(card.getExpiryDate())
                .userId(card.getUser().getId())
                .build();

        log.debug("Successfully mapped Card ID: %s to CardHugeResponseDto".formatted(card.getId()));
        return dto;
    }

    private String getEncryptedCardNumber(String number) {
        log.trace("Encrypting card number, last 4 digits: %s".formatted(number.substring(number.length() - 4)));

        String lastFour = number.substring(number.length() - 4);
        String encrypted = "**** **** **** %s".formatted(lastFour);

        log.trace("Card number encrypted to: %s".formatted(encrypted));
        return encrypted;
    }

    @Override
    public CardSmallResponseDto toCardSmallResponseDto(Card card) {
        log.debug("Mapping Card entity to CardSmallResponseDto, ID: %s".formatted(card.getId()));

        CardSmallResponseDto dto = CardSmallResponseDto.builder()
                .cardId(card.getId())
                .cardNumber(getEncryptedCardNumber(card.getCardNumber()))
                .userId(card.getUser().getId())
                .cardStatus(card.getStatus())
                .build();

        log.debug("Successfully mapped Card ID: %s to CardSmallResponseDto".formatted(card.getId()));
        return dto;
    }
}