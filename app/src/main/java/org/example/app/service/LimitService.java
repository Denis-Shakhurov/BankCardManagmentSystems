package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.dto.limit.LimitCreateDTO;
import org.example.app.dto.limit.LimitDTO;
import org.example.app.dto.limit.LimitUpdateDTO;
import org.example.app.exception.ResourceNotFoundException;
import org.example.app.mapper.LimitMapper;
import org.example.app.model.Card;
import org.example.app.model.Limit;
import org.example.app.model.PeriodType;
import org.example.app.repository.CardRepository;
import org.example.app.repository.LimitRepository;
import org.example.app.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LimitService {
    private final TransactionRepository transactionRepository;
    private final LimitRepository limitRepository;
    private final CardRepository cardRepository;
    private final LimitMapper limitMapper;

    public LimitDTO findById(Long id) {
        Limit limit = limitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Limit not found with id: " + id));
        return limitMapper.map(limit);
    }

    public List<LimitDTO> findAll(Long cardId) {
        List<Limit> limits = limitRepository.findByCardId(cardId);
        return limits.stream()
                .map(limitMapper::map)
                .collect(Collectors.toList());
    }

    @Transactional
    public LimitDTO save(LimitCreateDTO createDTO, Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElse(null);

        Limit limit = limitMapper.map(createDTO);
        limit.setCard(card);

        limitRepository.save(limit);
        return limitMapper.map(limit);
    }

    @Transactional
    public LimitDTO update(Long id, LimitUpdateDTO updateDTO) {
        Limit limit = limitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Limit not found with id: " + id));

        limitMapper.update(updateDTO, limit);

        limitRepository.save(limit);
        return limitMapper.map(limit);
    }

    @Transactional
    public void delete(Long id) {
        Limit limit = limitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Limit not found with id: " + id));
        limitRepository.delete(limit);
    }

    @Transactional
    public void checkLimit(Long cardId, BigDecimal amount) {
        List<Limit> limits = limitRepository.findByCardId(cardId);
        if (!limits.isEmpty()) {
            for (Limit limit : limits) {
                if (!checkLimitForCard(limit, cardId, amount)) {
                    throw new RuntimeException("Exceeding the limit");
                }
            }
        }
    }

    private boolean checkLimitForCard(Limit limit, Long cardId, BigDecimal amount) {
        LocalDate startDate = limit.getPeriodStartDate();
        LocalDate endDate = startDate.plusDays(30);
        BigDecimal sum;

        if (limit.isActive() && limit.getPeriodType().equals(PeriodType.DAILY)) {
            sum = transactionRepository.getDailyTransactionsSum(cardId, startDate).add(amount);
        } else {
            sum = transactionRepository.getTransactionsSumForPeriod(cardId, startDate, endDate).add(amount);
        }

        return sum.compareTo(limit.getLimitAmount()) < 0;
    }
}
