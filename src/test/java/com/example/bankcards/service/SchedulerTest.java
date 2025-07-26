package com.example.bankcards.service;

import com.example.bankcards.AbstractTest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.scheduler.CardCheckerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SchedulerTest extends AbstractTest {
    @Autowired
    private CardCheckerService cardCheckerService;

    @Test
    @Transactional
    public void checkCardsForExpiration_ThenSetCardsStatusToExpired() {
        UUID uuid = createSomeUsersInDB(1).getFirst();
        Card card = Card.builder()
                .cardNumber("1234567812345678")
                .user(userRepository.getReferenceById(uuid))
                .balance(5)
                .expiryDate(LocalDate.now().minusDays(1))
                .status(CardStatus.ACTIVE)
                .build();
        cardRepository.save(card);

        cardCheckerService.checkCardsForExpiration();
        card = cardRepository.findById(card.getId()).get();
        assertEquals(CardStatus.EXPIRED, card.getStatus());
    }
}
