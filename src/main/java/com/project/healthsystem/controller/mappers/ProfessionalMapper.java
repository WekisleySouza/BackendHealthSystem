package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.ProfessionalRequestDTO;
import com.project.healthsystem.model.Professional;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ProfessionalMapper {

    public abstract Professional toEntity(ProfessionalRequestDTO dto);

    public abstract ProfessionalRequestDTO toDto(Professional entity);

    public Professional toEntityWhenHasId(Professional entity, ProfessionalRequestDTO dto){
        Professional newEntity = toEntity(dto);
        newEntity.setId(entity.getId());
        return newEntity;
    }
}