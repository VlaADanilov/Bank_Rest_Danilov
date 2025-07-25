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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;
    private final BlockRepository blockRepository;

    @Override
    public Page<CardSmallResponseDto> getCards(Pageable of, List<CardStatus> filter, UUID userId) {
        Page<Card> cards;
        if (userId == null) {
            cards = cardRepository.findByStatusIn(filter, of);
        } else {
            if (!userRepository.existsById(userId)) {
                throw new UserNotFoundException(userId);
            }
            cards = cardRepository.findAll(
                    CardSpecifications.byUserIdAndStatusIn(userId, filter),
                    of);
        }
        return cards.map(cardMapper::toCardSmallResponseDto);
    }

    @Override
    public CardHugeResponseDto getById(UUID cardId, UUID userId, Role userRole) {
        Card card = getCardWithCheckAccessWithAdmin(cardId, userId, userRole);
        return cardMapper.toCardHugeResponseDto(card);
    }

    /**
     * Здесь обязательно нужно работать в рамках транзакции
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void transferMoney(CardTransferRequestDto requestDto, UUID userId) {
        Card fromCard = getCardWithCheckAccessWithoutAdmin(requestDto.fromCard(), userId);
        Card toCard = getCardWithCheckAccessWithoutAdmin(requestDto.toCard(), userId);
        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            throw new CardNotActiveException(
                    !fromCard.getStatus().equals(CardStatus.ACTIVE) ?
                            fromCard.getId() :
                            toCard.getId()
            );
        }
        if (fromCard.getBalance() < requestDto.money()) {
            throw new InsufficientFundsException();
        }

        fromCard.setBalance(fromCard.getBalance() - requestDto.money());
        toCard.setBalance(toCard.getBalance() + requestDto.money());
    }

    @Override
    public void deleteById(UUID cardId, UUID userId, Role userRole) {
        Card card = getCardWithCheckAccessWithAdmin(cardId, userId, userRole);
        cardRepository.delete(card);
    }

    @Override
    public UUID createCard(CardRequestDto card) {
        if (cardRepository.existsByCardNumber(card.cardNumber())) {
            throw new CardAlreadyExistsException(card.cardNumber());
        }
        Card build = Card.builder()
                .cardNumber(card.cardNumber())
                .expiryDate(card.expiryDate())
                .user(userRepository.findById(card.userId()).orElseThrow(
                        () -> new UserNotFoundException(card.userId())
                ))
                .status(CardStatus.ACTIVE)
                .balance(0)
                .build();
        cardRepository.save(build);
        return build.getId();
    }

    @Override
    public void block(UUID cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
        card.setStatus(CardStatus.BLOCKED);
        blockRepository.deleteByCard(card);
        cardRepository.save(card);
    }

    @Override
    public void activate(UUID cardId, LocalDate expiryDate) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
    }


    private Card getCardWithCheckAccessWithAdmin(UUID cardId, UUID userId, Role userRole) {
        Card card = cardRepository.findById(cardId).orElseThrow(() ->
                new CardNotFoundException(cardId));
        if (!card.getUser().getId().equals(userId) && !userRole.equals(Role.ADMIN)) {
            throw new AccessDeniedException(
                    "You do not have permission to do this action with this card"
            );
        }
        return card;
    }


    private Card getCardWithCheckAccessWithoutAdmin(UUID cardId, UUID userId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() ->
                new CardNotFoundException(cardId));
        if (!card.getUser().getId().equals(userId)) {
            throw new AccessDeniedException(
                    "Card with id %s not your card!".formatted(cardId.toString())
            );
        }
        return card;
    }
}
