package com.example.bankcards.mapper.impl;

import com.example.bankcards.dto.response.CardHugeResponseDto;
import com.example.bankcards.dto.response.CardSmallResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.mapper.CardMapper;
import org.springframework.stereotype.Component;

@Component
public class CardMapperImpl implements CardMapper {
    @Override
    public CardHugeResponseDto toCardHugeResponseDto(Card card) {
        return CardHugeResponseDto.builder()
                .cardNumb(getEncryptedCardNumber(card.getCardNumber()))
                .money(card.getBalance())
                .status(card.getStatus())
                .expiresAt(card.getExpiryDate())
                .userId(card.getUser().getId())
                .build();
    }

    private static String getEncryptedCardNumber(String number) {
        String lastFour =
                number.substring(
                        number.length() - 4);
        return "**** **** **** %s".formatted(lastFour);
    }

    @Override
    public CardSmallResponseDto toCardSmallResponseDto(Card card) {
        return CardSmallResponseDto.builder()
                .cardNumber(getEncryptedCardNumber(card.getCardNumber()))
                .userId(card.getUser().getId())
                .cardStatus(card.getStatus())
                .build();
    }
}
