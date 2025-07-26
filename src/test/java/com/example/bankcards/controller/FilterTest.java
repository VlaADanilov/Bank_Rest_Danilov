package com.example.bankcards.controller;

import com.example.bankcards.AbstractTest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FilterTest extends AbstractTest {

    @BeforeEach
    public void clearBefore() {
        clearAfter();
    }

    @AfterEach
    public void clearAfter() {
        blockRepository.deleteAll();
        cardRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void getCardInfo_WithMyCardId_ReturnsOk () throws Exception {
        UUID userId = createSomeUsersInDB(1).getFirst();
        Card card = addActualCardsToUser(userId).getFirst();
        Role role = Role.USER;
        String accessToken = workWithJwt.generateAccessToken(userId, role);

        mvc.perform(get("/api/v1/card/" + card.getId())
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    public void getCardInfo_WithNotMyCardId_ReturnsForbidden () throws Exception {
        UUID userId = createSomeUsersInDB(1).getFirst();
        Card card = addActualCardsToUser(userId).getFirst();
        Role role = Role.USER;
        String accessToken = workWithJwt.generateAccessToken(UUID.randomUUID(), role);

        mvc.perform(get("/api/v1/card/" + card.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
    }

    @Test
    public void getCardInfo_WithNotMyCardIdButMyRoleIsAdmin_ReturnsOk () throws Exception {
        UUID userId = createSomeUsersInDB(1).getFirst();
        Card card = addActualCardsToUser(userId).getFirst();
        Role role = Role.ADMIN;
        String accessToken = workWithJwt.generateAccessToken(UUID.randomUUID(), role);

        mvc.perform(get("/api/v1/card/" + card.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    public void getCardInfo_WithIncorrectJWT_ReturnsForbidden () throws Exception {
        UUID userId = createSomeUsersInDB(1).getFirst();
        Card card = addActualCardsToUser(userId).getFirst();
        Role role = Role.ADMIN;
        String accessToken = "12345";

        mvc.perform(get("/api/v1/card/" + card.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
    }
}
