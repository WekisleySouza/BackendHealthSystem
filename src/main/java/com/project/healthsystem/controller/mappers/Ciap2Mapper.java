package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.basic_responses.Ciap2ResponseDTO;
import com.project.healthsystem.model.datasus_data.ciap.Ciap2;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class Ciap2Mapper {
    public abstract Ciap2ResponseDTO toDto(Ciap2 entity);
}
