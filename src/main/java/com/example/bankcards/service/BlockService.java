package com.example.bankcards.service;

import com.example.bankcards.dto.response.RequestToBlockResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BlockService {
    UUID blockCard(UUID id, UUID userId);

    Page<RequestToBlockResponseDto> getAll(Pageable of);
}
