package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.basic_responses.SigtapProcedureResponseDTO;
import com.project.healthsystem.model.datasus_data.sigtap.SigtapProcedure;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class SigtapProcedureMapper {
    @Mapping(target = "groupCode", source = "organization.subGroup.group.code")
    @Mapping(target = "groupName", source = "organization.subGroup.group.name")
    @Mapping(target = "subGroupCode", source = "organization.subGroup.code")
    @Mapping(target = "subGroupName", source = "organization.subGroup.name")
    @Mapping(target = "organizationCode", source = "organization.code")
    @Mapping(target = "organizationName", source = "organization.name")
    public abstract SigtapProcedureResponseDTO toDto(SigtapProcedure entity);
}
