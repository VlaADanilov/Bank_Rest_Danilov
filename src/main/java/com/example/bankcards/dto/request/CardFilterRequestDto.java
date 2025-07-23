package com.example.bankcards.dto.request;

import com.example.bankcards.entity.enums.CardStatus;

import java.util.List;

public record CardFilterRequestDto(
        List<CardStatus> statusList
) {
}
