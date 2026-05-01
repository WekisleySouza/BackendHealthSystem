package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.basic_requests.InstituitionRequestDTO;
import com.project.healthsystem.controller.dto.basic_responses.InstituitionResponseDTO;
import com.project.healthsystem.model.Instituition;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class InstituitionMapper {

    public abstract Instituition toEntity(InstituitionRequestDTO dto);

    public abstract InstituitionResponseDTO toDto(Instituition entity);

    public Instituition toEntityWhenHasId(Instituition entity, InstituitionRequestDTO dto){
        entity.setName(dto.getName());
        entity.setCep(dto.getCep());
        entity.setCityName(dto.getCityName());
        entity.setAddress(dto.getAddress());
        entity.setPhone(dto.getPhone());
        entity.setLinkLogo(dto.getLinkLogo());
        return entity;
    }
}
