package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.basic_responses.Cid10ResponseDTO;
import com.project.healthsystem.model.datasus_data.cid.Cid10;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class Cid10Mapper {
    public abstract Cid10ResponseDTO toDto(Cid10 entity);
}
