package org.example.app.dto.card;

import org.example.app.model.Card;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardMapper {
    Card toEntity(CardCreateDTO cardCreateDTO);

    CardCreateDTO toCardCreateDTO(Card card);
}