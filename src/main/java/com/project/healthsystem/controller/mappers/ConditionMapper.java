package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.ConditionRequestDTO;
import com.project.healthsystem.controller.dto.ConditionResponseDTO;
import com.project.healthsystem.model.Condition;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ConditionMapper {

    public abstract Condition toEntity(ConditionRequestDTO dto);

    public abstract ConditionResponseDTO toDto(Condition entity);

    public Condition toEntityWhenHasId(Condition entity, ConditionRequestDTO dto){
        entity.setSpecification(dto.getSpecification());
        return entity;
    }
}
