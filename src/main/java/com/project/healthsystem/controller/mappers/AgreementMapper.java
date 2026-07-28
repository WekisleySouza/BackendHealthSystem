package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.basic_requests.AgreementRequestDTO;
import com.project.healthsystem.controller.dto.basic_responses.AgreementResponseDTO;
import com.project.healthsystem.model.Agreement;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class AgreementMapper {

    public abstract Agreement toEntity(AgreementRequestDTO dto);

    public abstract AgreementResponseDTO toDto(Agreement entity);

    public Agreement toEntityWhenHasId(Agreement entity, AgreementRequestDTO dto){
        if(!dto.getName().isBlank()) entity.setName(dto.getName());
        return entity;
    }
}
