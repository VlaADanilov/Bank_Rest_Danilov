package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID>, JpaSpecificationExecutor<Card> {

    Boolean existsByCardNumber(String cardNumber);

    Page<Card> findByStatusIn(List<CardStatus> status, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Card c SET c.status = 'EXPIRED' WHERE c.expiryDate < CURRENT_DATE AND c.status = 'ACTIVE'")
    void updateExpiredCardsStatus();
}
