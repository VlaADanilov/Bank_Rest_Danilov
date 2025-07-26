package com.example.bankcards.service;

import com.example.bankcards.dto.response.CardHugeResponseDto;
import com.example.bankcards.dto.response.CardSmallResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.mapper.impl.CardMapperImpl;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCardEncrypting {
    @Test
    public void testEncryptHugeResponse() {
        CardMapper cardMapper = new CardMapperImpl();
        Card card = getCard();
        CardHugeResponseDto cardHugeResponseDto = cardMapper.toCardHugeResponseDto(card);
        assertEquals(19, cardHugeResponseDto.cardNumb().length());
        assertTrue(cardHugeResponseDto.cardNumb().matches("\\*{4} \\*{4} \\*{4} \\d{4}"));
    }

    @Test
    public void testEncryptSmallResponse() {
        CardMapper cardMapper = new CardMapperImpl();
        Card card = getCard();
        CardSmallResponseDto cardSmallResponseDto = cardMapper.toCardSmallResponseDto(card);
        assertEquals(19, cardSmallResponseDto.cardNumber().length());
        assertTrue(cardSmallResponseDto.cardNumber().matches("\\*{4} \\*{4} \\*{4} \\d{4}"));
    }

    private static Card getCard() {
        return Card.builder()
                .id(UUID.randomUUID())
                .cardNumber("1234567812345678")
                .status(CardStatus.ACTIVE)
                .balance(1)
                .expiryDate(LocalDate.now())
                .user(User.builder().id(UUID.randomUUID()).build())
                .build();
    }
}
