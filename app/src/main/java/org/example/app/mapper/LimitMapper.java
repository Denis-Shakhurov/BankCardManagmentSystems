package org.example.app.mapper;

import org.example.app.dto.limit.LimitCreateDTO;
import org.example.app.dto.limit.LimitDTO;
import org.example.app.dto.limit.LimitUpdateDTO;
import org.example.app.model.Limit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class LimitMapper {
    @Mapping(target = "user.id", source = "userId")
    public abstract Limit map(LimitDTO dto);

    @Mapping(target = "userId", source = "user.id")
    public abstract LimitDTO map(Limit model);

    public abstract Limit map(LimitCreateDTO dto);

    public abstract void update(LimitUpdateDTO dto,@MappingTarget Limit model);
}
