package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.dto.limit.LimitCreateDTO;
import org.example.app.dto.limit.LimitDTO;
import org.example.app.dto.limit.LimitUpdateDTO;
import org.example.app.exception.ResourceNotFoundException;
import org.example.app.mapper.LimitMapper;
import org.example.app.model.Card;
import org.example.app.model.Limit;
import org.example.app.repository.CardRepository;
import org.example.app.repository.LimitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LimitService {
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

    public LimitDTO save(LimitCreateDTO createDTO, Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElse(null);

        Limit limit = limitMapper.map(createDTO);
        limit.setCard(card);

        limitRepository.save(limit);
        return limitMapper.map(limit);
    }

    public LimitDTO update(Long id, LimitUpdateDTO updateDTO) {
        Limit limit = limitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Limit not found with id: " + id));

        limitMapper.update(updateDTO, limit);

        limitRepository.save(limit);
        return limitMapper.map(limit);
    }

    public void delete(Long id) {
        Limit limit = limitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Limit not found with id: " + id));
        limitRepository.delete(limit);
    }
}
