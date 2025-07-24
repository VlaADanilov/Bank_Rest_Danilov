package com.example.bankcards.mapper;

import com.example.bankcards.dto.response.CardHugeResponseDto;
import com.example.bankcards.dto.response.CardSmallResponseDto;
import com.example.bankcards.entity.Card;

public interface CardMapper {

    CardHugeResponseDto toCardHugeResponseDto(Card card);

    CardSmallResponseDto toCardSmallResponseDto(Card card);
}
