package com.example.bankcards.service.impl;

import com.example.bankcards.dto.response.RequestToBlockResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.RequestToBlock;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.RequestAlreadyExistsException;
import com.example.bankcards.mapper.BlockMapper;
import com.example.bankcards.repository.BlockRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlockServiceImpl implements BlockService {
    private final BlockRepository blockRepository;
    private final CardRepository cardRepository;
    private final BlockMapper blockMapper;

    @Override
    public UUID blockCard(UUID id, UUID userId) {
        Card card = cardRepository
                .findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
        if (!card.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You don't have permission to block this card");
        }
        if (blockRepository.existsByCard(card)) {
            throw new RequestAlreadyExistsException();
        }
        RequestToBlock block = RequestToBlock.builder()
                .card(cardRepository.getReferenceById(id))
                .build();
        blockRepository.save(block);
        return block.getId();
    }

    @Override
    public Page<RequestToBlockResponseDto> getAll(Pageable of) {
        return
                blockRepository.findAll(of)
                        .map(blockMapper::toBlockDto);
    }
}
