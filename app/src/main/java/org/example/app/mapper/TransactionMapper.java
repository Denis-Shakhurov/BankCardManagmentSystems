package org.example.app.mapper;

import org.example.app.dto.transaction.TransactionCreateDTO;
import org.example.app.dto.transaction.TransactionDTO;
import org.example.app.dto.transaction.TransactionUpdateDTO;
import org.example.app.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class TransactionMapper {
    @Mapping(target = "cardId", source = "card.id")
    public abstract TransactionDTO map(Transaction model);

    @Mapping(target = "card.id", source = "cardId")
    public abstract Transaction map(TransactionDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "card", ignore = true)
    public abstract Transaction map(TransactionCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "card", ignore = true)
    public abstract void update(TransactionUpdateDTO dto, @MappingTarget Transaction model);
}
