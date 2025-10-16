package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.ConditionDTO;
import com.project.healthsystem.model.Condition;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ConditionMapper {

    public abstract Condition toEntity(ConditionDTO dto);

    public abstract ConditionDTO toDto(Condition entity);

    public Condition toEntityWhenHasId(Condition entity, ConditionDTO dto){
        Condition newEntity = toEntity(dto);
        newEntity.setId(entity.getId());
        return newEntity;
    }
}
