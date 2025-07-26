package com.example.bankcards.controller;

import com.example.bankcards.AbstractTest;
import com.example.bankcards.dto.request.CardTransferRequestDto;
import com.example.bankcards.dto.response.CardHugeResponseDto;
import com.example.bankcards.dto.response.CardSmallResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.RequestToBlock;
import com.example.bankcards.entity.enums.CardStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CardControllerTest extends AbstractTest {

    @Autowired
    private CardController cardController;

    private UUID userId;

    @BeforeEach
    public void clearBefore() {
        clearAfter();
        List<UUID> someUsersInDB = createSomeUsersInDB(1);
        userId = someUsersInDB.getFirst();
        authentificateAsUser(userId);
    }

    @AfterEach
    public void clearAfter() {
        blockRepository.deleteAll();
        cardRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getById_WithMyCardId_ReturnsCorrectInformation() throws Exception {
        List<Card> cards = addActualCardsToUser(userId);
        String result = mvc.perform(get("/api/v1/card/" + cards.getFirst().getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        CardHugeResponseDto cardHugeResponseDto = objectMapper.readValue(result, CardHugeResponseDto.class);
        assertEquals(cardHugeResponseDto.status(), CardStatus.ACTIVE);
        assertTrue(cardHugeResponseDto.cardNumb().startsWith("**** **** ****"));
    }

    @Test
    public void getById_WithNotMyCardId_ReturnsForbidden() throws Exception {
        List<Card> cards = addActualCardsToUser(userId);
        authentificateAsUser(UUID.randomUUID());
        mvc.perform(get("/api/v1/card/" + cards.getFirst().getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getById_WithNotExistsdId_ReturnsForbidden() throws Exception {
        mvc.perform(get("/api/v1/card/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getById_WithNotMyCardIdButMyRoleIsAdmin_ReturnsCorrectInformation() throws Exception {
        List<Card> cards = addActualCardsToUser(userId);
        authentificateAsAdmin(UUID.randomUUID());
        mvc.perform(get("/api/v1/card/" + cards.getFirst().getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void requestToBlock_WithMyCardId_ReturnsOkAndAddRequestToBlockToDB() throws Exception {
        List<Card> cards = addActualCardsToUser(userId);
        String result = mvc.perform(post("/api/v1/card/block/" + cards.getFirst().getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        UUID requestToBlockId = UUID.fromString(result.replaceAll("\"", ""));

        Optional<RequestToBlock> byId = blockRepository.findById(requestToBlockId);
        assertTrue(byId.isPresent());
        assertEquals(cards.getFirst().getId(), byId.get().getCard().getId());
    }

    @Test
    public void requestToBlock_WithNotExistedCardId_ReturnsNotFound() throws Exception {
        mvc.perform(post("/api/v1/card/block/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void requestToBlock_WithNotMyCardId_ReturnsForbidden() throws Exception {
        List<Card> cards = addActualCardsToUser(userId);
        authentificateAsUser(UUID.randomUUID());
        mvc.perform(post("/api/v1/card/block/" + cards.getFirst().getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    public void deleteCard_WithMyCardId_ReturnsOkAndDeletedCardInDB() throws Exception {
        List<Card> cards = addActualCardsToUser(userId);

        mvc.perform(delete("/api/v1/card/" + cards.getFirst().getId()))
                .andExpect(status().isOk());

        assertFalse(blockRepository.existsById(cards.getFirst().getId()));

    }

    @Test
    public void deleteCard_WithNotMyCardId_ReturnsForbiddenAndNotDeletedCardFromDB() throws Exception {
        List<Card> cards = addActualCardsToUser(userId);
        authentificateAsUser();
        mvc.perform(delete("/api/v1/card/" + cards.getFirst().getId()))
                .andExpect(status().isForbidden());

        assertTrue(cardRepository.existsById(cards.getFirst().getId()));
    }

    @Test
    public void deleteCard_WithNotExistsCardId_ReturnsNotFound() throws Exception {
        mvc.perform(delete("/api/v1/card/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteCard_WithNotMyCardIdButMyRoleIsAdmin_ReturnsOkAndDeletedCardFromDB() throws Exception {
        List<Card> cards = addActualCardsToUser(userId);
        authentificateAsAdmin();
        mvc.perform(delete("/api/v1/card/" + cards.getFirst().getId()))
                .andExpect(status().isOk());

        assertFalse(blockRepository.existsById(cards.getFirst().getId()));
    }

    @Test
    public void getMyCards_WithNotHavingCards_ReturnsEmptyContent() {
        Page<CardSmallResponseDto> myCards = cardController.getMyCards(0, 1, getAllCardStatuses());
        assertTrue(myCards.getContent().isEmpty());
    }

    private void addAllTypeOfCards() {
        addActualCardsToUser(userId);
        addBlockedCardsToUser(userId);
        addExpiresCardsToUser(userId);
    }

    @Test
    public void getMyCards_WithHavingCards_ReturnsAll() {
        addAllTypeOfCards();
        Page<CardSmallResponseDto> myCards = cardController.getMyCards(0, 9, getAllCardStatuses());
        assertEquals(myCards.getContent().size(), 9);

        Map<CardStatus, Integer> cardStatusIntegerMap = new HashMap<>();

        for (CardSmallResponseDto card : myCards.getContent()) {
            cardStatusIntegerMap.put(card.cardStatus(),
                    cardStatusIntegerMap.getOrDefault(card.cardStatus(),0) + 1);
        }
        for (Map.Entry<CardStatus, Integer> entry : cardStatusIntegerMap.entrySet()) {
            assertEquals(3, entry.getValue());
        }
    }

    @Test
    public void getMyCards_WithFilterOnActive_ReturnsAll() {
        addAllTypeOfCards();
        Page<CardSmallResponseDto> myCards = cardController.getMyCards(0, 9, List.of(CardStatus.ACTIVE));
        assertEquals(myCards.getContent().size(), 3);

        for (CardSmallResponseDto card : myCards.getContent()) {
            assertEquals(CardStatus.ACTIVE, card.cardStatus());
        }
    }

    @Test
    public void getMyCards_WithMultiFilterOnActive_ReturnsAll() {
        addAllTypeOfCards();
        Page<CardSmallResponseDto> myCards = cardController.getMyCards(0, 9, List.of(CardStatus.ACTIVE, CardStatus.BLOCKED));
        assertEquals(myCards.getContent().size(), 6);

        for (CardSmallResponseDto card : myCards.getContent()) {
            assertTrue(List.of(CardStatus.ACTIVE, CardStatus.BLOCKED).contains(card.cardStatus()));
        }
    }

    @Test
    public void getMyCards_WithNullFilterOnActive_ReturnsNothing() {
        addAllTypeOfCards();
        Page<CardSmallResponseDto> myCards = cardController.getMyCards(0, 9, List.of());
        assertEquals(myCards.getContent().size(), 0);
    }

    @Test
    public void transfer_WithMyCards_ReturnsOkAndMoneyTransfered() throws Exception {
        List<Card> cards = addActualCardsToUser(userId);
        Card fromCard = cards.getFirst();
        Card toCard = cards.get(1);
        int transferedMoney = 500;
        Integer fromCardStartBalance = fromCard.getBalance();
        Integer toCardStartBalance = toCard.getBalance();

        CardTransferRequestDto transfer = new CardTransferRequestDto(
                fromCard.getId(), toCard.getId(), transferedMoney
        );

        mvc.perform(post("/api/v1/card/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transfer)))
                .andExpect(status().isOk());

        fromCard = cardRepository.findById(fromCard.getId()).orElseThrow();
        assertEquals(fromCardStartBalance - transferedMoney, fromCard.getBalance());
        toCard = cardRepository.findById(toCard.getId()).orElseThrow();
        assertEquals(toCardStartBalance + transferedMoney, toCard.getBalance());
    }

    @Test
    public void transfer_WithMyCardsButHaventMoney_ReturnsIAmATeapot() throws Exception {
        List<Card> cards = addActualCardsToUser(userId);
        Card fromCard = cards.getFirst();
        Card toCard = cards.get(1);
        int transferedMoney = 5000;
        Integer fromCardStartBalance = fromCard.getBalance();
        Integer toCardStartBalance = toCard.getBalance();

        CardTransferRequestDto transfer = new CardTransferRequestDto(
                fromCard.getId(), toCard.getId(), transferedMoney
        );

        mvc.perform(post("/api/v1/card/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transfer)))
                .andExpect(status().isIAmATeapot());

        fromCard = cardRepository.findById(fromCard.getId()).orElseThrow();
        assertEquals(fromCardStartBalance, fromCard.getBalance());
        toCard = cardRepository.findById(toCard.getId()).orElseThrow();
        assertEquals(toCardStartBalance, toCard.getBalance());
    }

    @Test
    public void transfer_WithMyCardsButOneCardNotExists_ReturnsNotFound() throws Exception {
        List<Card> cards = addActualCardsToUser(userId);
        Card fromCard = cards.getFirst();
        int transferedMoney = 500;
        Integer fromCardStartBalance = fromCard.getBalance();

        CardTransferRequestDto transfer = new CardTransferRequestDto(
                fromCard.getId(), UUID.randomUUID(), transferedMoney
        );

        mvc.perform(post("/api/v1/card/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transfer)))
                .andExpect(status().isNotFound());

        fromCard = cardRepository.findById(fromCard.getId()).orElseThrow();
        assertEquals(fromCardStartBalance, fromCard.getBalance());
    }

    @Test
    public void transfer_WithMyCardsButOneCardBlocked_ReturnsIAmATeapot() throws Exception {
        List<Card> cards = addActualCardsToUser(userId);
        List<Card> cards1 = addBlockedCardsToUser(userId);
        Card fromCard = cards.getFirst();
        Card toCard = cards1.getFirst();
        int transferedMoney = 500;
        Integer fromCardStartBalance = fromCard.getBalance();
        Integer toCardStartBalance = toCard.getBalance();

        CardTransferRequestDto transfer = new CardTransferRequestDto(
                fromCard.getId(), toCard.getId(), transferedMoney
        );

        mvc.perform(post("/api/v1/card/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transfer)))
                .andExpect(status().isIAmATeapot());

        fromCard = cardRepository.findById(fromCard.getId()).orElseThrow();
        assertEquals(fromCardStartBalance, fromCard.getBalance());
        toCard = cardRepository.findById(toCard.getId()).orElseThrow();
        assertEquals(toCardStartBalance, toCard.getBalance());
    }

    @Test
    public void transfer_WithMyCardsButOneCardExpired_ReturnsIAmATeapot() throws Exception {
        List<Card> cards = addActualCardsToUser(userId);
        List<Card> cards1 = addExpiresCardsToUser(userId);
        Card fromCard = cards.getFirst();
        Card toCard = cards1.getFirst();
        int transferedMoney = 500;
        Integer fromCardStartBalance = fromCard.getBalance();
        Integer toCardStartBalance = toCard.getBalance();

        CardTransferRequestDto transfer = new CardTransferRequestDto(
                fromCard.getId(), toCard.getId(), transferedMoney
        );

        mvc.perform(post("/api/v1/card/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transfer)))
                .andExpect(status().isIAmATeapot());

        fromCard = cardRepository.findById(fromCard.getId()).orElseThrow();
        assertEquals(fromCardStartBalance, fromCard.getBalance());
        toCard = cardRepository.findById(toCard.getId()).orElseThrow();
        assertEquals(toCardStartBalance, toCard.getBalance());
    }

    @Test
    public void transfer_WithOneNotMyCard_ReturnsForbidden() throws Exception {
        List<Card> cards = addActualCardsToUser(userId);


        Card fromCard = cards.getFirst();
        Card toCard = getOtherUserCard();
        int transferedMoney = 500;
        Integer fromCardStartBalance = fromCard.getBalance();
        Integer toCardStartBalance = toCard.getBalance();

        CardTransferRequestDto transfer = new CardTransferRequestDto(
                fromCard.getId(), toCard.getId(), transferedMoney
        );

        mvc.perform(post("/api/v1/card/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transfer)))
                .andExpect(status().isForbidden());

        fromCard = cardRepository.findById(fromCard.getId()).orElseThrow();
        assertEquals(fromCardStartBalance, fromCard.getBalance());
        toCard = cardRepository.findById(toCard.getId()).orElseThrow();
        assertEquals(toCardStartBalance, toCard.getBalance());
    }

    private Card getOtherUserCard() {
        UUID someUserInDB = createSomeUsersInDB(1, "code").getFirst();
        List<Card> cards = addActualCardsToUser(someUserInDB);
        return cards.getFirst();
    }
}
