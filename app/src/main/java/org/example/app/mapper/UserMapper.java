package org.example.app.mapper;

import org.example.app.dto.user.UserCreateDTO;
import org.example.app.dto.user.UserDTO;
import org.example.app.dto.user.UserUpdateDTO;
import org.example.app.model.Card;
import org.example.app.model.User;
import org.example.app.repository.CardRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class UserMapper {
    @Autowired
    private CardRepository cardRepository;

    public abstract User map(UserDTO dto);

    public abstract UserDTO map(User model);

    public abstract User map(UserCreateDTO dto);

    public abstract void update(UserUpdateDTO dto, @MappingTarget User model);

    public List<Card> longToCards(List<Long> ids) {
        return ids.isEmpty()
                ? new ArrayList<>()
                : ids.stream()
                .map(id -> cardRepository.findById(id)
                        .orElseThrow())
                .collect(Collectors.toList());
    }

    public List<Long> cardsToLong(List<Card> cards) {
        return cards.stream()
                .map(Card::getId)
                .collect(Collectors.toList());
    }
}
