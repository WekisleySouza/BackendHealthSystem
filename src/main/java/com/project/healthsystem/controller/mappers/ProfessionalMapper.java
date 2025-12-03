package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.ProfessionalRequestDTO;
import com.project.healthsystem.controller.dto.ProfessionalResponseDTO;
import com.project.healthsystem.model.Professional;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ProfessionalMapper {

    public abstract Professional toEntity(ProfessionalRequestDTO dto);

    public abstract ProfessionalResponseDTO toDto(Professional entity);

    public Professional toEntityWhenHasId(Professional entity, ProfessionalRequestDTO dto){
        entity.setName(dto.getName());
        entity.setBirthday(dto.getBirthday());
        entity.setCpf(dto.getCpf());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        return entity;
    }
}