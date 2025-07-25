package com.example.bankcards;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.repository.BlockRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractTest {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected BlockRepository blockRepository;

    @Autowired
    protected CardRepository cardRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected UUID userId;

    protected void authentificateAsUser() {
        userId = UUID.randomUUID();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                null, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        authentication.setDetails(userId);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    protected void authentificateAsAdmin() {
        userId = UUID.randomUUID();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                null, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        authentication.setDetails(userId);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    protected List<UUID> createSomeUsersInDB(int count) {
        List<UUID> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            User build = User.builder()
                    .username("username" + i)
                    .password(passwordEncoder.encode("password" + i))
                    .firstName("first" + i)
                    .lastName("last" + i)
                    .patronymicName("patronymic" + i)
                    .build();
            userRepository.save(build);
            users.add(build.getId());
        }
        return users;
    }

    protected void authentificateAsUser(UUID id) {
        userId = id;
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                null, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        authentication.setDetails(userId);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    protected void authentificateAsAdmin(UUID id) {
        userId = id;
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                null, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        authentication.setDetails(userId);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    protected List<Card> addActualCardsToUser(UUID userId) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Card build = getCard(userId, i, CardStatus.ACTIVE, "123456781234567");
            cards.add(build);
            cardRepository.save(build);
        }
        return cards;
    }

    protected List<Card> addBlockedCardsToUser(UUID userId) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Card build = getCard(userId, i, CardStatus.BLOCKED, "223456781234567");
            cards.add(build);
            cardRepository.save(build);
        }
        return cards;
    }

    protected List<Card> addExpiresCardsToUser(UUID userId) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Card build = getCard(userId, i, CardStatus.EXPIRED, "323456781234567");
            cards.add(build);
            cardRepository.save(build);
        }
        return cards;
    }

    private Card getCard(UUID userId, int i, CardStatus status, String startCardNumber) {
        return Card.builder()
                .user(userRepository.getReferenceById(userId))
                .cardNumber(startCardNumber + i)
                .balance(1000)
                .status(status)
                .expiryDate(LocalDate.now().plusDays(5))
                .build();
    }

    protected List<CardStatus> getAllCardStatuses() {
        return List.of(CardStatus.ACTIVE, CardStatus.BLOCKED, CardStatus.EXPIRED);
    }
}

