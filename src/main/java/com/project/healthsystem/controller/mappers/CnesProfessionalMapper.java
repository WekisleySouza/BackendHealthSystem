package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.basic_responses.CnesProfessionalResponseDTO;
import com.project.healthsystem.model.datasus_data.cnes.CnesProfessional;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CnesProfessionalMapper {
    CnesProfessionalResponseDTO toDto(CnesProfessional entity);
}
