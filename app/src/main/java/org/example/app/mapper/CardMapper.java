package org.example.app.mapper;

import org.example.app.component.CryptoEncoder;
import org.example.app.dto.card.CardCreateDTO;
import org.example.app.dto.card.CardDTO;
import org.example.app.dto.card.CardUpdateDTO;
import org.example.app.exception.ResourceNotFoundException;
import org.example.app.model.Card;
import org.example.app.model.Limit;
import org.example.app.model.Transaction;
import org.example.app.repository.LimitRepository;
import org.example.app.repository.TransactionRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CardMapper {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private LimitRepository limitRepository;

    @Autowired
    private CryptoEncoder encoder;

    @Mapping(target = "user.id", source = "userId")
    public abstract Card map(CardDTO dto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "transactionIds", source = "transactions")
    @Mapping(target = "limitIds", source = "limits")
    public abstract CardDTO map(Card model);

    public abstract Card map(CardCreateDTO dto);

    public abstract void update(CardUpdateDTO dto, @MappingTarget Card model);

    public List<Transaction> longToTransactions(List<Long> ids) {
        return ids.stream()
                .map(id -> transactionRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Transaction not found")))
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
                        .orElseThrow(() -> new ResourceNotFoundException("Limit not found")))
                .collect(Collectors.toList());
    }

    public List<Long> limitsToLongs(List<Limit> limits) {
        return limits.stream()
                .map(Limit::getId)
                .collect(Collectors.toList());
    }

    @BeforeMapping
    public void encryptCardNumber(CardCreateDTO dto) {
        String cardNumber = dto.getCardNumber();
        dto.setCardNumber(encoder.encrypt(cardNumber));
    }

    @AfterMapping
    public void maskCardNumber(Card model, @MappingTarget CardDTO dto) {
        String encrypted = model.getCardNumber();
        String decrypted = encoder.decrypt(encrypted);
        dto.setCardNumber(maskNumber(decrypted));
    }

    private String maskNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) return "****";
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
