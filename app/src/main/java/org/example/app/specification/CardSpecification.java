package org.example.app.specification;

import org.example.app.dto.card.CardParamDTO;
import org.example.app.model.Card;
import org.example.app.model.StatusCard;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;

@Component
public class CardSpecification {

    public Specification<Card> build(CardParamDTO params) {
        return withUserId(params.getUserId())
                .and(withCardStatus(params.getStatus()))
                .and(withCardNumber(params.getCardNumber()))
                .and(withCardHolderName(params.getCardHolderName()))
                .and(withCardDate(params.getCreatedAt()));
    }

    private Specification<Card> withUserId(Long userId) {
        return (root, query, cb) -> userId == null
                ? cb.conjunction()
                : cb.equal(root.get("user").get("id"), userId);
    }

    private Specification<Card> withCardStatus(StatusCard status) {
        return (root, query, cb) -> status == null
                ? cb.conjunction()
                : cb.equal(root.get("status"), status);
    }

    private Specification<Card> withCardNumber(String cardNumber) {
        return (root, query, cb) -> cardNumber == null
                ? cb.conjunction()
                : cb.like(root.get("cardNumber"), "%" + cardNumber + "%");
    }

    private Specification<Card> withCardHolderName(String cardHolderName) {
        return (root, query, cb) -> cardHolderName == null
                ? cb.conjunction()
                : cb.like(root.get("cardHolderName"),"%" + cardHolderName + "%");
    }

    private Specification<Card> withCardDate(LocalDate date) {
        return (root, query, cb) -> date == null
                ? cb.conjunction()
                : cb.equal(root.get("createdAt").as(Date.class), date);
    }
}
