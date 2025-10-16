package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.ProfessionalDTO;
import com.project.healthsystem.model.Professional;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ProfessionalMapper {

    public abstract Professional toEntity(ProfessionalDTO dto);

    public abstract ProfessionalDTO toDto(Professional entity);

    public Professional toEntityWhenHasId(Professional entity, ProfessionalDTO dto){
        Professional newEntity = toEntity(dto);
        newEntity.setId(entity.getId());
        return newEntity;
    }
}