package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.CategoryGroupDTO;
import com.project.healthsystem.model.CategoryGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class CategoryGroupMapper {

    public abstract CategoryGroup toEntity(CategoryGroupDTO dto);

    public abstract CategoryGroupDTO toDto(CategoryGroup entity);

    public CategoryGroup toEntityWhenHasId(CategoryGroup entity, CategoryGroupDTO dto){
        CategoryGroup newEntity = toEntity(dto);
        newEntity.setId(entity.getId());
        return newEntity;
    }
}