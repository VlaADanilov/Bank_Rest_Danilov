package com.example.bankcards.controller;

import com.example.bankcards.AbstractTest;
import com.example.bankcards.dto.request.CardRequestDto;
import com.example.bankcards.dto.response.CardSmallResponseDto;
import com.example.bankcards.dto.response.RequestToBlockResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.RequestToBlock;
import com.example.bankcards.entity.enums.CardStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminControllerTest extends AbstractTest {

    @Autowired
    private AdminController adminController;

    @BeforeEach
    public void clearBefore() {
        authentificateAsAdmin();
        clearAfter();
    }

    @AfterEach
    public void clearAfter() {
        blockRepository.deleteAll();
        cardRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getRequests_ReturnsAllRequests() {
        UUID userId = createSomeUsersInDB(1).get(0);
        Card card = addActualCardsToUser(userId).get(0);
        RequestToBlock requestToBlock = createRequestToBlock(card.getId());

        Page<RequestToBlockResponseDto> requests = adminController.getRequests(0, 1);
        assertEquals(1, requests.getTotalElements());
        assertEquals(requestToBlock.getId(), requests.getContent().get(0).id());
    }

    @Test
    @Transactional
    public void blockCard_WithCorrectId_returnsOkAndBlockedCardAndDeleteRequestToBlock() throws Exception {
        UUID userId = createSomeUsersInDB(1).get(0);
        Card card = addActualCardsToUser(userId).get(0);
        RequestToBlock requestToBlock = createRequestToBlock(card.getId());

        mvc.perform(post("/api/v1/admin/block/" + card.getId()))
                .andExpect(status().isOk());

        Card resultCard = cardRepository.findById(card.getId()).orElseThrow();
        assertEquals(CardStatus.BLOCKED, resultCard.getStatus());
        assertFalse(blockRepository.existsById(requestToBlock.getId()));
    }

    @Test
    @Transactional
    public void blockCard_WithCorrectIdWithoutRequestToBlock_returnsOkAndBlockedCard() throws Exception {
        UUID userId = createSomeUsersInDB(1).get(0);
        Card card = addActualCardsToUser(userId).get(0);

        mvc.perform(post("/api/v1/admin/block/" + card.getId()))
                .andExpect(status().isOk());

        Card resultCard = cardRepository.findById(card.getId()).orElseThrow();
        assertEquals(CardStatus.BLOCKED, resultCard.getStatus());
        assertTrue(blockRepository.findAll().isEmpty());
    }

    @Test
    @Transactional
    public void blockCard_WithBlockedCard_returnsOkAndBlockedCard() throws Exception {
        UUID userId = createSomeUsersInDB(1).get(0);
        Card card = addBlockedCardsToUser(userId).get(0);

        mvc.perform(post("/api/v1/admin/block/" + card.getId()))
                .andExpect(status().isOk());

        Card resultCard = cardRepository.findById(card.getId()).orElseThrow();
        assertEquals(CardStatus.BLOCKED, resultCard.getStatus());
    }

    @Test
    @Transactional
    public void blockCard_WithExpiredCard_returnsOkAndBlockedCard() throws Exception {
        UUID userId = createSomeUsersInDB(1).get(0);
        Card card = addExpiresCardsToUser(userId).get(0);

        mvc.perform(post("/api/v1/admin/block/" + card.getId()))
                .andExpect(status().isOk());

        Card resultCard = cardRepository.findById(card.getId()).orElseThrow();
        assertEquals(CardStatus.BLOCKED, resultCard.getStatus());
    }

    @Test
    public void blockCard_WithNotExistsId_returnsNotFound() throws Exception {
        mvc.perform(post("/api/v1/admin/block/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void activateCard_WithBlockedCard_returnsOkAndActivateCard() throws Exception {
        UUID userId = createSomeUsersInDB(1).get(0);
        Card card = addBlockedCardsToUser(userId).get(0);
        LocalDate localDate = LocalDate.now().plusMonths(2);
        mvc.perform(post("/api/v1/admin/active/" + card.getId())
                        .param("expiryDate", localDate.toString()))
                .andExpect(status().isOk());

        Card resultCard = cardRepository.findById(card.getId()).orElseThrow();
        assertEquals(CardStatus.ACTIVE, resultCard.getStatus());
        assertEquals(localDate, resultCard.getExpiryDate());
    }

    @Test
    @Transactional
    public void activateCard_WithExpiresCard_returnsOkAndActivateCard() throws Exception {
        UUID userId = createSomeUsersInDB(1).get(0);
        Card card = addExpiresCardsToUser(userId).get(0);
        LocalDate localDate = LocalDate.now().plusMonths(2);
        mvc.perform(post("/api/v1/admin/active/" + card.getId())
                        .param("expiryDate", localDate.toString()))
                .andExpect(status().isOk());

        Card resultCard = cardRepository.findById(card.getId()).orElseThrow();
        assertEquals(CardStatus.ACTIVE, resultCard.getStatus());
        assertEquals(localDate, resultCard.getExpiryDate());
    }

    @Test
    @Transactional
    public void activateCard_WithActiveCard_returnsOkAndActivateCard() throws Exception {
        UUID userId = createSomeUsersInDB(1).get(0);
        Card card = addActualCardsToUser(userId).get(0);
        LocalDate localDate = LocalDate.now().plusMonths(2);
        mvc.perform(post("/api/v1/admin/active/" + card.getId())
                        .param("expiryDate", localDate.toString()))
                .andExpect(status().isOk());

        Card resultCard = cardRepository.findById(card.getId()).orElseThrow();
        assertEquals(CardStatus.ACTIVE, resultCard.getStatus());
        assertEquals(localDate, resultCard.getExpiryDate());
    }

    @Test
    public void activateCard_WithNotExistsId_returnsOkAndActivateCard() throws Exception {

        LocalDate localDate = LocalDate.now().plusMonths(2);
        mvc.perform(post("/api/v1/admin/active/" + UUID.randomUUID())
                        .param("expiryDate", localDate.toString()))
                .andExpect(status().isNotFound());

    }

    @Test
    public void activateCard_WithPrevData_returnsOkAndActivateCard() throws Exception {
        LocalDate localDate = LocalDate.now().minusMonths(2);
        mvc.perform(post("/api/v1/admin/active/" + UUID.randomUUID())
                        .param("expiryDate", localDate.toString()))
                .andExpect(status().isBadRequest());

    }

    @Test
    @Transactional
    public void createCard_WithCorrectData_returnsCreatedAndSavedCard() throws Exception {
        UUID userId = createSomeUsersInDB(1).get(0);

        CardRequestDto cardRequestDto = new CardRequestDto(
                "1234567890123456",
                LocalDate.now().plusDays(3),
                userId
        );

        String result = mvc.perform(post("/api/v1/admin/card")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UUID cardId = UUID.fromString(result.replaceAll("\"", ""));
        assertTrue(cardRepository.existsById(cardId));
        Card resultCard = cardRepository.findById(cardId).orElseThrow();
        assertEquals(CardStatus.ACTIVE, resultCard.getStatus());
        assertEquals(cardRequestDto.expiryDate(), resultCard.getExpiryDate());
        assertEquals(cardRequestDto.cardNumber(), resultCard.getCardNumber());
        assertEquals(cardRequestDto.userId(), resultCard.getUser().getId());
    }

    @Test
    public void createCard_WithInCorrectCardNumber_returnsBadRequest() throws Exception {
        UUID userId = createSomeUsersInDB(1).get(0);

        CardRequestDto cardRequestDto = new CardRequestDto(
                "1234567890123456youl",
                LocalDate.now().plusDays(3),
                userId
        );

        mvc.perform(post("/api/v1/admin/card")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createCard_WithInCorrectExpiryDate_returnsBadRequest() throws Exception {
        UUID userId = createSomeUsersInDB(1).get(0);

        CardRequestDto cardRequestDto = new CardRequestDto(
                "1234567890123456",
                LocalDate.now().minusMonths(3),
                userId
        );

        mvc.perform(post("/api/v1/admin/card")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createCard_WithExistsCardNumber_returnsBadRequest() throws Exception {
        UUID userId = createSomeUsersInDB(1).get(0);
        Card card = addActualCardsToUser(userId).get(0);
        CardRequestDto cardRequestDto = new CardRequestDto(
                card.getCardNumber(),
                LocalDate.now().minusDays(3),
                userId
        );

        mvc.perform(post("/api/v1/admin/card")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createCard_WithNotExistsUserId_returnsNotFound() throws Exception {
        CardRequestDto cardRequestDto = new CardRequestDto(
                "1234567890123456",
                LocalDate.now().plusDays(3),
                UUID.randomUUID()
        );

        mvc.perform(post("/api/v1/admin/card")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getCards_ReturnsAllCards() {
        List<UUID> someUsersInDB = createSomeUsersInDB(3);
        Set<UUID> allCardsInDB = new HashSet<>();
        for (UUID userId : someUsersInDB) {
            allCardsInDB.addAll(addActualCardsToUser(userId).stream().map(Card::getId).toList());
            allCardsInDB.addAll(addBlockedCardsToUser(userId).stream().map(Card::getId).toList());
            allCardsInDB.addAll(addExpiresCardsToUser(userId).stream().map(Card::getId).toList());
        }

        Page<CardSmallResponseDto> cards = adminController.getCards(0, 27, getAllCardStatuses(), null);
        assertEquals(27, cards.getContent().size());
        assertEquals(1, cards.getTotalPages());

        for (CardSmallResponseDto card : cards.getContent()) {
            allCardsInDB.remove(card.cardId());
        }
        assertTrue(allCardsInDB.isEmpty());
    }

    @Test
    public void getCards_WithFilter_ReturnsAllCards() {
        List<UUID> someUsersInDB = createSomeUsersInDB(3);
        Set<UUID> allCardsInDB = new HashSet<>();
        for (UUID userId : someUsersInDB) {
            allCardsInDB.addAll(addActualCardsToUser(userId).stream().map(Card::getId).toList());
            allCardsInDB.addAll(addBlockedCardsToUser(userId).stream().map(Card::getId).toList());
            allCardsInDB.addAll(addExpiresCardsToUser(userId).stream().map(Card::getId).toList());
        }

        Page<CardSmallResponseDto> cards = adminController.getCards(0, 27, List.of(CardStatus.ACTIVE), null);
        assertEquals(9, cards.getContent().size());
        assertEquals(1, cards.getTotalPages());

        for (CardSmallResponseDto card : cards.getContent()) {
            if (card.cardStatus() != CardStatus.ACTIVE) {
                fail();
            }
        }
    }

    @Test
    public void getCards_WithMultiFilter_ReturnsAllCards() {
        List<UUID> someUsersInDB = createSomeUsersInDB(3);
        Set<UUID> allCardsInDB = new HashSet<>();
        for (UUID userId : someUsersInDB) {
            allCardsInDB.addAll(addActualCardsToUser(userId).stream().map(Card::getId).toList());
            allCardsInDB.addAll(addBlockedCardsToUser(userId).stream().map(Card::getId).toList());
            allCardsInDB.addAll(addExpiresCardsToUser(userId).stream().map(Card::getId).toList());
        }

        Page<CardSmallResponseDto> cards = adminController.getCards(0, 27, List.of(CardStatus.ACTIVE, CardStatus.BLOCKED), null);
        assertEquals(18, cards.getContent().size());
        assertEquals(1, cards.getTotalPages());

        for (CardSmallResponseDto card : cards.getContent()) {
            if (card.cardStatus() == CardStatus.EXPIRED) {
                fail();
            }
        }
    }

    @Test
    public void getCards_WithUserFilter_ReturnsAllHidCards() {
        List<UUID> someUsersInDB = createSomeUsersInDB(3);
        Set<UUID> allCardsInDB = new HashSet<>();
        for (UUID userId : someUsersInDB) {
            allCardsInDB.addAll(addActualCardsToUser(userId).stream().map(Card::getId).toList());
            allCardsInDB.addAll(addBlockedCardsToUser(userId).stream().map(Card::getId).toList());
            allCardsInDB.addAll(addExpiresCardsToUser(userId).stream().map(Card::getId).toList());
        }

        Page<CardSmallResponseDto> cards = adminController.getCards(0, 27, getAllCardStatuses(), someUsersInDB.get(0));
        assertEquals(9, cards.getContent().size());
        assertEquals(1, cards.getTotalPages());

        for (CardSmallResponseDto card : cards.getContent()) {
            if (!card.userId().equals(someUsersInDB.get(0))) {
                fail();
            }
        }
    }

    @Test
    public void getCards_WithUserAndStatusFilter_ReturnsAllHidCards() {
        List<UUID> someUsersInDB = createSomeUsersInDB(3);
        Set<UUID> allCardsInDB = new HashSet<>();
        for (UUID userId : someUsersInDB) {
            allCardsInDB.addAll(addActualCardsToUser(userId).stream().map(Card::getId).toList());
            allCardsInDB.addAll(addBlockedCardsToUser(userId).stream().map(Card::getId).toList());
            allCardsInDB.addAll(addExpiresCardsToUser(userId).stream().map(Card::getId).toList());
        }

        Page<CardSmallResponseDto> cards = adminController.getCards(0, 27, List.of(CardStatus.ACTIVE), someUsersInDB.get(0));
        assertEquals(3, cards.getContent().size());
        assertEquals(1, cards.getTotalPages());

        for (CardSmallResponseDto card : cards.getContent()) {
            if (!card.userId().equals(someUsersInDB.get(0)) || card.cardStatus() != CardStatus.ACTIVE) {
                fail();
            }
        }
    }
}
