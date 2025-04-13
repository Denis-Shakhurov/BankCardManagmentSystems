package org.example.app.specification;

import org.example.app.dto.transaction.TransactionParamDTO;
import org.example.app.model.Transaction;
import org.example.app.model.TransactionType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class TransactionSpecification {

    public Specification<Transaction> build(TransactionParamDTO params) {
        return withCardId(params.getCardId())
                .and(withAmount(params.getAmount()))
                .and(withType(params.getType()))
                .and(withDescription(params.getDescription()))
                .and(withTransactionDateAfter(params.getDateFrom()))
                .and(withTransactionDateBefore(params.getDateTo()));
    }

    private Specification<Transaction> withCardId(Long cardId) {
        return (root, query, cb) -> cardId == null
                ? cb.conjunction()
                : cb.equal(root.get("card").get("id"), cardId);
    }

    private Specification<Transaction> withAmount(BigDecimal amount) {
        return (root, query, cb) -> amount == null
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("amount"), amount);
    }

    private Specification<Transaction> withType(TransactionType type) {
        return (root, query, cb) -> type == null
                ? cb.conjunction()
                : cb.equal(root.get("type"), type);
    }

    private Specification<Transaction> withDescription(String description) {
        return (root, query, cb) -> description == null
                ? cb.conjunction()
                : cb.like(root.get("description"), "%" + description + "%");
    }

    private Specification<Transaction> withTransactionDateAfter(LocalDateTime dateFrom) {
        return (root, query, cb) -> dateFrom == null
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("transactionDate"), dateFrom);
    }

    private Specification<Transaction> withTransactionDateBefore(LocalDateTime dateTo) {
        return (root, query, cb) -> dateTo == null
                ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("transactionDate"), dateTo);
    }

    private Specification<Transaction> withTransactionDate(LocalDate date) {
        return (root, query, cb) -> date == null
                ? cb.conjunction()
                : cb.equal(root.get("transactionDate").as(Date.class), date);
    }
}
