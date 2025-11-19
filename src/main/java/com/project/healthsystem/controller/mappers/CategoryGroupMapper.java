package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.CategoryGroupRequestDTO;
import com.project.healthsystem.model.CategoryGroup;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class CategoryGroupMapper {

    public abstract CategoryGroup toEntity(CategoryGroupRequestDTO dto);

    public abstract CategoryGroupRequestDTO toDto(CategoryGroup entity);

    public CategoryGroup toEntityWhenHasId(CategoryGroup entity, CategoryGroupRequestDTO dto){
        CategoryGroup newEntity = toEntity(dto);
        newEntity.setId(entity.getId());
        return newEntity;
    }
}