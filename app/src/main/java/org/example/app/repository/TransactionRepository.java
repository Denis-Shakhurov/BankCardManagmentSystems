package org.example.app.repository;

import org.example.app.model.Transaction;
import org.example.app.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByCardId(Long cardId);

    // Сумма транзакций по карте за конкретный день
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.card.id = :cardId " +
            "AND t.type = :transactionType " +
            "AND CAST(t.transactionDate AS localdate) = :date")
    BigDecimal getDailyTransactionsSum(
            @Param("cardId") Long cardId,
            @Param("transactionType") TransactionType transactionType,
            @Param("date") LocalDate date);

    // Сумма транзакций по карте за период
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.card.id = :cardId " +
            "AND t.type = :transactionType " +
            "AND CAST(t.transactionDate AS localdate) BETWEEN :startDate AND :endDate")
    BigDecimal getTransactionsSumForPeriod(
            @Param("cardId") Long cardId,
            @Param("transactionType") TransactionType transactionType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
