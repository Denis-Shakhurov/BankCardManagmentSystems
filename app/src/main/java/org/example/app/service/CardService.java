package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.dto.card.CardCreateDTO;
import org.example.app.dto.card.CardDTO;
import org.example.app.dto.card.CardUpdateDTO;
import org.example.app.exception.ResourceNotFoundException;
import org.example.app.mapper.CardMapper;
import org.example.app.model.Card;
import org.example.app.model.User;
import org.example.app.repository.CardRepository;
import org.example.app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;

    public CardDTO findById(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id " + id));
        return cardMapper.map(card);
    }

    public List<CardDTO> findAllByUserId(Long userId) {
        List<Card> cards = cardRepository.findAllByUserId(userId);
        return cards.stream()
                .map(cardMapper::map)
                .collect(Collectors.toList());
    }

    public CardDTO create(CardCreateDTO createDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElse(null);

        Card card = cardMapper.map(createDTO);
        card.setUser(user);

        Long id = cardRepository.save(card).getId();
        card.setId(id);

        return cardMapper.map(card);
    }

    public CardDTO update(CardUpdateDTO updateDTO, Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id " + id));

        cardMapper.update(updateDTO, card);
        cardRepository.save(card);
        return cardMapper.map(card);
    }

    public void delete(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id " + id));
        cardRepository.delete(card);
    }
}
