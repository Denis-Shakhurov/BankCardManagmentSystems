package org.example.app.repository;

import org.example.app.model.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LimitRepository extends JpaRepository<Limit, Long> {
    List<Limit> findByCardId(Long cardId);
}
