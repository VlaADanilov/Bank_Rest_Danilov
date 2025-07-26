package com.example.bankcards.service.impl;

import com.example.bankcards.dto.request.CardRequestDto;
import com.example.bankcards.dto.request.CardTransferRequestDto;
import com.example.bankcards.dto.response.CardHugeResponseDto;
import com.example.bankcards.dto.response.CardSmallResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.exception.*;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.BlockRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.repository.specification.CardSpecifications;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;
    private final BlockRepository blockRepository;

    @Override
    public Page<CardSmallResponseDto> getCards(Pageable of, List<CardStatus> filter, UUID userId) {
        log.debug("Getting cards - page: %s, size: %s, filter: %s, userId: %s".formatted(of.getPageNumber(), of.getPageSize(), filter, userId));

        Page<Card> cards;
        if (userId == null) {
            cards = cardRepository.findByStatusIn(filter, of);
            log.debug("Retrieved cards by status filter, total: %s".formatted(cards.getTotalElements()));
        } else {
            if (!userRepository.existsById(userId)) {
                log.warn("User not found with ID: %s".formatted(userId));
                throw new UserNotFoundException(userId);
            }
            cards = cardRepository.findAll(
                    CardSpecifications.byUserIdAndStatusIn(userId, filter),
                    of);
            log.debug("Retrieved cards for user: %s, total: %s".formatted(userId, cards.getTotalElements()));
        }

        Page<CardSmallResponseDto> result = cards.map(cardMapper::toCardSmallResponseDto);
        log.debug("Successfully mapped cards to response DTOs, total: %s".formatted(result.getTotalElements()));
        return result;
    }

    @Override
    public CardHugeResponseDto getById(UUID cardId, UUID userId, Role userRole) {
        log.debug("Getting card by ID: %s for user: %s with role: %s".formatted(cardId, userId, userRole));
        Card card = getCardWithCheckAccessWithAdmin(cardId, userId, userRole);
        CardHugeResponseDto result = cardMapper.toCardHugeResponseDto(card);
        log.debug("Successfully retrieved card details for ID: %s".formatted(cardId));
        return result;
    }

    /**
     * Здесь обязательно нужно работать в рамках транзакции
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void transferMoney(CardTransferRequestDto requestDto, UUID userId) {
        log.info("Initiating money transfer - from card: %s to card: %s, amount: %s by user: %s".formatted(
                requestDto.fromCard(), requestDto.toCard(), requestDto.money(), userId));

        Card fromCard = getCardWithCheckAccessWithoutAdmin(requestDto.fromCard(), userId);
        Card toCard = getCardWithCheckAccessWithoutAdmin(requestDto.toCard(), userId);

        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            UUID inactiveCardId = !fromCard.getStatus().equals(CardStatus.ACTIVE) ? fromCard.getId() : toCard.getId();
            log.warn("Transfer failed: card not active, card ID: %s, status: %s".formatted(
                    inactiveCardId,
                    !fromCard.getStatus().equals(CardStatus.ACTIVE) ? fromCard.getStatus() : toCard.getStatus()));
            throw new CardNotActiveException(inactiveCardId);
        }

        if (fromCard.getBalance() < requestDto.money()) {
            log.warn("Transfer failed: insufficient funds on card: %s, balance: %s, requested: %s".formatted(
                    fromCard.getId(), fromCard.getBalance(), requestDto.money()));
            throw new InsufficientFundsException();
        }

        fromCard.setBalance(fromCard.getBalance() - requestDto.money());
        toCard.setBalance(toCard.getBalance() + requestDto.money());

        log.info("Successfully completed money transfer - from card: %s (new balance: %s) to card: %s (new balance: %s)".formatted(
                fromCard.getId(), fromCard.getBalance() + requestDto.money(),
                toCard.getId(), toCard.getBalance() - requestDto.money()));
    }

    @Override
    public void deleteById(UUID cardId, UUID userId, Role userRole) {
        log.info("Deleting card: %s by user: %s with role: %s".formatted(cardId, userId, userRole));
        Card card = getCardWithCheckAccessWithAdmin(cardId, userId, userRole);
        cardRepository.delete(card);
        log.info("Successfully deleted card: %s".formatted(cardId));
    }

    @Override
    public UUID createCard(CardRequestDto card) {
        log.info("Creating new card for user: %s with card number: %s".formatted(card.userId(), card.cardNumber()));

        if (cardRepository.existsByCardNumber(card.cardNumber())) {
            log.warn("Card creation failed: card number already exists: %s".formatted(card.cardNumber()));
            throw new CardAlreadyExistsException(card.cardNumber());
        }

        Card build = Card.builder()
                .cardNumber(card.cardNumber())
                .expiryDate(card.expiryDate())
                .user(userRepository.findById(card.userId()).orElseThrow(
                        () -> {
                            log.warn("Card creation failed: user not found with ID: %s".formatted(card.userId()));
                            return new UserNotFoundException(card.userId());
                        }
                ))
                .status(CardStatus.ACTIVE)
                .balance(0)
                .build();

        cardRepository.save(build);
        log.info("Successfully created card with ID: %s for user: %s".formatted(build.getId(), card.userId()));
        return build.getId();
    }

    @Override
    public void block(UUID cardId) {
        log.info("Blocking card: %s".formatted(cardId));

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    log.warn("Block operation failed: card not found with ID: %s".formatted(cardId));
                    return new CardNotFoundException(cardId);
                });

        card.setStatus(CardStatus.BLOCKED);
        blockRepository.deleteByCard(card);
        cardRepository.save(card);

        log.info("Successfully blocked card: %s".formatted(cardId));
    }

    @Override
    public void activate(UUID cardId, LocalDate expiryDate) {
        log.info("Activating card: %s with new expiry date: %s".formatted(cardId, expiryDate));

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    log.warn("Activate operation failed: card not found with ID: %s".formatted(cardId));
                    return new CardNotFoundException(cardId);
                });

        card.setStatus(CardStatus.ACTIVE);
        card.setExpiryDate(expiryDate);
        cardRepository.save(card);

        log.info("Successfully activated card: %s".formatted(cardId));
    }


    private Card getCardWithCheckAccessWithAdmin(UUID cardId, UUID userId, Role userRole) {
        log.debug("Checking access to card: %s for user: %s with role: %s".formatted(cardId, userId, userRole));

        Card card = cardRepository.findById(cardId).orElseThrow(() ->
        {
            log.warn("Access check failed: card not found with ID: %s".formatted(cardId));
            return new CardNotFoundException(cardId);
        });

        if (!card.getUser().getId().equals(userId) && !userRole.equals(Role.ADMIN)) {
            log.warn("Access denied: user: %s with role: %s cannot access card: %s".formatted(userId, userRole, cardId));
            throw new AccessDeniedException(
                    "You do not have permission to do this action with this card"
            );
        }

        log.debug("Access granted to card: %s for user: %s".formatted(cardId, userId));
        return card;
    }


    private Card getCardWithCheckAccessWithoutAdmin(UUID cardId, UUID userId) {
        log.debug("Checking access to card: %s for user: %s (no admin check)".formatted(cardId, userId));

        Card card = cardRepository.findById(cardId).orElseThrow(() ->
        {
            log.warn("Access check failed: card not found with ID: %s".formatted(cardId));
            return new CardNotFoundException(cardId);
        });

        if (!card.getUser().getId().equals(userId)) {
            log.warn("Access denied: user: %s cannot access card: %s".formatted(userId, cardId));
            throw new AccessDeniedException(
                    "Card with id %s not your card!".formatted(cardId.toString())
            );
        }

        log.debug("Access granted to card: %s for user: %s".formatted(cardId, userId));
        return card;
    }
}