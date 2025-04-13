package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.dto.TransferDTO;
import org.example.app.exception.InsufficientFundsException;
import org.example.app.exception.ResourceNotFoundException;
import org.example.app.model.Card;
import org.example.app.model.Transaction;
import org.example.app.model.TransactionType;
import org.example.app.repository.CardRepository;
import org.example.app.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final LimitService limitService;

    @Transactional
    public void transferBetweenOwnCards(Long userId, TransferDTO transferDTO) {
        Card fromCard = cardRepository.findByIdAndUserId(transferDTO.getFromCardId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found for id "
                        + transferDTO.getFromCardId()));
        Card toCard = cardRepository.findByIdAndUserId(transferDTO.getToCardId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found for id "
                        + transferDTO.getToCardId()));

        // Проверяем, что это не одна и та же карта
        if (fromCard.getId().equals(toCard.getId())) {
            throw new IllegalArgumentException("Cannot transfer to the same card");
        }

        // Проверяем достаточность средств
        if (fromCard.getBalance().compareTo(transferDTO.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds on source card");
        }

        // Проверяем лимиты (если есть)
        limitService.checkLimit(fromCard.getId(), transferDTO.getAmount());

        // Создаём транзакции
        Transaction withdrawal = new Transaction();
        withdrawal.setCard(fromCard);
        withdrawal.setAmount(transferDTO.getAmount());
        withdrawal.setDescription("Transfer to card " + toCard.getCardNumber());
        withdrawal.setType(TransactionType.TRANSFER_OUT);
        withdrawal.setTransactionDate(LocalDateTime.now());

        Transaction deposit = new Transaction();
        deposit.setCard(toCard);
        deposit.setAmount(transferDTO.getAmount());
        deposit.setDescription("Transfer from card " + fromCard.getCardNumber());
        deposit.setType(TransactionType.TRANSFER_IN);
        deposit.setTransactionDate(LocalDateTime.now());

        // Обновляем балансы
        fromCard.setBalance(fromCard.getBalance().subtract(transferDTO.getAmount()));
        toCard.setBalance(toCard.getBalance().add(transferDTO.getAmount()));

        // Сохраняем изменения
        cardRepository.save(fromCard);
        cardRepository.save(toCard);
        transactionRepository.save(withdrawal);
        transactionRepository.save(deposit);
    }
}
