package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.basic_responses.CnesEstabilishmentResponseDTO;
import com.project.healthsystem.model.datasus_data.cnes.Estabilishment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CnesEstabilishmentMapper {
    CnesEstabilishmentResponseDTO toDto(Estabilishment entity);
}
