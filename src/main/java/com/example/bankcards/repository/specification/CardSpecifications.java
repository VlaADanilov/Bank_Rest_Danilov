package com.example.bankcards.repository.specification;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;
import java.util.UUID;

public class CardSpecifications {

    public static Specification<Card> byUserIdAndStatusIn(UUID userId, List<CardStatus> statuses) {
        return (Root<Card> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            Join<Card, User> userJoin = root.join("user");

            Predicate userIdPredicate = builder.equal(userJoin.get("id"), userId);
            Predicate statusPredicate = root.get("status").in(statuses);

            return builder.and(userIdPredicate, statusPredicate);
        };
    }
}
