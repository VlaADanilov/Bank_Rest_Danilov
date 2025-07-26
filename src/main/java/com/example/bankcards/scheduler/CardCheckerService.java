package com.example.bankcards.scheduler;

import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardCheckerService {
    private final CardRepository cardRepository;

    @Scheduled(cron = "0 1 0 * * ?")
    public void checkCardsForExpiration() {
        log.info("Starting scheduled check for expired cards");
        try {
            cardRepository.updateExpiredCardsStatus();
            log.info("Successfully completed scheduled check for expired cards");
        } catch (Exception e) {
            log.error("Error during scheduled check for expired cards", e);
        }
    }
}