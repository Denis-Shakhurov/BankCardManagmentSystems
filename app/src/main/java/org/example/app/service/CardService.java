package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.component.UserUtils;
import org.example.app.dto.card.CardCreateDTO;
import org.example.app.dto.card.CardDTO;
import org.example.app.dto.card.CardParamDTO;
import org.example.app.dto.card.CardUpdateDTO;
import org.example.app.exception.ResourceNotFoundException;
import org.example.app.mapper.CardMapper;
import org.example.app.model.Card;
import org.example.app.model.Role;
import org.example.app.model.User;
import org.example.app.repository.CardRepository;
import org.example.app.repository.UserRepository;
import org.example.app.specification.CardSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardSpecification specBuilder;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;
    private final UserUtils userUtils;

    public CardDTO findById(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id " + id));
        return cardMapper.map(card);
    }

    public List<CardDTO> findAll(CardParamDTO params) {
        Specification<Card> spec = specBuilder.build(params);
        List<Card> cards = new ArrayList<>();
        User user = userUtils.getCurrentUser();

        if (user != null && user.getRole().equals(Role.USER)) {
            cards.addAll(cardRepository.findAllByUserId(user.getId()));
        } else {
            cards.addAll(cardRepository.findAll(spec));
        }

        return cards.stream()
                .map(cardMapper::map)
                .collect(Collectors.toList());
    }

    @Transactional
    public CardDTO create(CardCreateDTO createDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElse(null);

        Card card = cardMapper.map(createDTO);
        card.setUser(user);

        Long id = cardRepository.save(card).getId();
        card.setId(id);

        return cardMapper.map(card);
    }

    @Transactional
    public CardDTO update(CardUpdateDTO updateDTO, Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id " + id));

        cardMapper.update(updateDTO, card);
        cardRepository.save(card);
        return cardMapper.map(card);
    }

    @Transactional
    public void delete(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id " + id));
        cardRepository.delete(card);
    }
}
