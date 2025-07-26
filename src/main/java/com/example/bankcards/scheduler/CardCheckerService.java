package com.example.bankcards.scheduler;

import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardCheckerService {
    private final CardRepository cardRepository;

    @Scheduled(cron = "0 1 0 * * ?")
    public void checkCardsForExpiration() {
        cardRepository.updateExpiredCardsStatus();
    }
}
