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
    @Mapping(target = "cardId", source = "user.id")
    public abstract TransactionDTO map(Transaction model);

    @Mapping(target = "user.id", source = "userId")
    public abstract Transaction map(TransactionDTO dto);

    public abstract Transaction map(TransactionCreateDTO dto);

    public abstract void update(TransactionUpdateDTO dto, @MappingTarget Transaction model);
}
