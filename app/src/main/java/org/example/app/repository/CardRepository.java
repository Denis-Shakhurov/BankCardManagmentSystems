package org.example.app.repository;

import org.example.app.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {
    List<Card> findAllByUserId(Long userId);

    Optional<Card> findByIdAndUserId(Long cardId, Long userId);

    @Query("SELECT c FROM Card c WHERE c.user.id = :userId AND c.status = 'ACTIVE'")
    List<Card> findAllActiveByUserId(@Param("userId") Long userId);
}
