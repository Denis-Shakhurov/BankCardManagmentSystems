package org.example.app.mapper;

import org.example.app.dto.user.UserCreateDTO;
import org.example.app.dto.user.UserDTO;
import org.example.app.dto.user.UserUpdateDTO;
import org.example.app.exception.ResourceNotFoundException;
import org.example.app.model.Card;
import org.example.app.model.User;
import org.example.app.repository.CardRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {CardMapper.class}
)
public abstract class UserMapper {
    @Autowired
    private CardRepository cardRepository;

    public abstract User map(UserDTO dto);

    @Mapping(target = "cardIds", source = "cards")
    public abstract UserDTO map(User model);

    public abstract User map(UserCreateDTO dto);

    public abstract void update(UserUpdateDTO dto, @MappingTarget User model);

    public List<Card> longToCards(List<Long> ids) {
        return ids == null || ids.isEmpty()
                ? new ArrayList<>()
                : ids.stream()
                .map(id -> cardRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Card not found")))
                .collect(Collectors.toList());
    }

    public List<Long> cardsToLong(List<Card> cards) {
        return cards == null
                ? new ArrayList<>()
                : cards.stream()
                .map(Card::getId)
                .collect(Collectors.toList());
    }
}
