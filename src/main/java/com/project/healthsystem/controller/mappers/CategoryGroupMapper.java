package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.CategoryGroupRequestDTO;
import com.project.healthsystem.controller.dto.CategoryGroupResponseDTO;
import com.project.healthsystem.model.CategoryGroup;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class CategoryGroupMapper {

    public abstract CategoryGroup toEntity(CategoryGroupRequestDTO dto);

    public abstract CategoryGroupResponseDTO toDto(CategoryGroup entity);

    public CategoryGroup toEntityWhenHasId(CategoryGroup entity, CategoryGroupRequestDTO dto){
        entity.setName(dto.getName());
        return entity;
    }
}