package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.dto.transaction.TransactionCreateDTO;
import org.example.app.dto.transaction.TransactionDTO;
import org.example.app.dto.transaction.TransactionUpdateDTO;
import org.example.app.exception.ResourceNotFoundException;
import org.example.app.mapper.TransactionMapper;
import org.example.app.model.Card;
import org.example.app.model.Transaction;
import org.example.app.model.TransactionType;
import org.example.app.repository.CardRepository;
import org.example.app.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final CardRepository cardRepository;

    public TransactionDTO findById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id " + id ));
        return transactionMapper.map(transaction);
    }

    public List<TransactionDTO> findAll() {
        List<Transaction> transactions = transactionRepository.findAll();
        return transactions.stream()
                .map(transactionMapper::map)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findALLByCardId(Long id) {
        List<Transaction> transactions = transactionRepository.findAllByCardId(id);
        return transactions.stream()
                .map(transactionMapper::map)
                .collect(Collectors.toList());
    }

    public TransactionDTO save(TransactionCreateDTO createDTO, Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElse(null);

        Transaction transaction = transactionMapper.map(createDTO);
        transaction.setCard(card);
        transaction.setTransactionDate(LocalDateTime.now());

        Long id = transactionRepository.save(transaction).getId();
        transaction.setId(id);

        return transactionMapper.map(transaction);
    }

    public TransactionDTO update(Long id, TransactionUpdateDTO updateDTO) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id " + id ));

        transactionMapper.update(updateDTO, transaction);

        transactionRepository.save(transaction);

        return transactionMapper.map(transaction);
    }

    public void delete(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id " + id ));

        transactionRepository.delete(transaction);
    }

    public BigDecimal getDailyTransactionSum(Long cardId,
                                             TransactionType transactionType,
                                             LocalDate date) {
        return transactionRepository.getDailyTransactionsSum(cardId, transactionType, date);
    }

    public BigDecimal getTransactionSumForPeriod(Long cardId,
                                                 TransactionType transactionType,
                                                 LocalDate startDate,
                                                 LocalDate endDate) {
        return transactionRepository.getTransactionsSumForPeriod(cardId, transactionType, startDate, endDate);
    }
}
