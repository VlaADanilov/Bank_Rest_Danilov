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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockServiceImpl implements BlockService {
    private final BlockRepository blockRepository;
    private final CardRepository cardRepository;
    private final BlockMapper blockMapper;

    @Override
    public UUID blockCard(UUID id, UUID userId) {
        log.info("User: %s requesting to block card: %s".formatted(userId, id));

        Card card = cardRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.warn("Block request failed: card not found with ID: %s".formatted(id));
                    return new CardNotFoundException(id);
                });

        if (!card.getUser().getId().equals(userId)) {
            log.warn("Block request denied: user: %s doesn't have permission to block card: %s".formatted(userId, id));
            throw new AccessDeniedException("You don't have permission to block this card");
        }

        if (blockRepository.existsByCard(card)) {
            log.warn("Block request failed: request already exists for card: %s".formatted(id));
            throw new RequestAlreadyExistsException();
        }

        RequestToBlock block = RequestToBlock.builder()
                .card(cardRepository.getReferenceById(id))
                .build();

        blockRepository.save(block);
        log.info("Successfully created block request: %s for card: %s by user: %s".formatted(block.getId(), id, userId));

        return block.getId();
    }

    @Override
    public Page<RequestToBlockResponseDto> getAll(Pageable of) {
        log.debug("Getting all block requests - page: %s, size: %s".formatted(of.getPageNumber(), of.getPageSize()));

        Page<RequestToBlockResponseDto> requests = blockRepository.findAll(of)
                .map(blockMapper::toBlockDto);

        log.debug("Successfully retrieved %s block requests".formatted(requests.getTotalElements()));
        return requests;
    }
}