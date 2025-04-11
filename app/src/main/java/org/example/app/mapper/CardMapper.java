package org.example.app.mapper;

import org.example.app.dto.card.CardCreateDTO;
import org.example.app.dto.card.CardDTO;
import org.example.app.model.Card;
import org.example.app.model.Limit;
import org.example.app.model.Transaction;
import org.example.app.repository.LimitRepository;
import org.example.app.repository.TransactionRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class CardMapper {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private LimitRepository limitRepository;

    public abstract Card map(CardDTO dto);

    public abstract CardDTO map(Card model);

    public abstract Card map(CardCreateDTO dto);

    @Mapping(target = "user.id", source = "userId")
    public abstract void update(CardCreateDTO dto, @MappingTarget Card model);

    public List<Transaction> longToTransactions(List<Long> ids) {
        return ids.stream()
                .map(id -> transactionRepository.findById(id)
                        .orElseThrow())
                .collect(Collectors.toList());
    }

    public List<Long> transactionsToLongs(List<Transaction> transactions) {
        return transactions.stream()
                .map(Transaction::getId)
                .collect(Collectors.toList());
    }

    public List<Limit> longToLimits(List<Long> ids) {
        return ids.stream()
                .map(id -> limitRepository.findById(id)
                        .orElseThrow())
                .collect(Collectors.toList());
    }

    public List<Long> limitsToLongs(List<Limit> limits) {
        return limits.stream()
                .map(Limit::getId)
                .collect(Collectors.toList());
    }
}
