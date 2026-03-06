package com.project.healthsystem.controller.mappers;

import com.project.healthsystem.controller.dto.CategoryGroupResponseDTO;
import com.project.healthsystem.controller.dto.basic_requests.CategoryGroupRequestDTO;
import com.project.healthsystem.model.CategoryGroup;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-06T18:01:48-0300",
    comments = "version: 1.6.0, compiler: javac, environment: Java 21.0.10 (Ubuntu)"
)
@Component
public class CategoryGroupMapperImpl extends CategoryGroupMapper {

    @Override
    public CategoryGroup toEntity(CategoryGroupRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        CategoryGroup categoryGroup = new CategoryGroup();

        categoryGroup.setName( dto.getName() );

        return categoryGroup;
    }

    @Override
    public CategoryGroupResponseDTO toDto(CategoryGroup entity) {
        if ( entity == null ) {
            return null;
        }

        CategoryGroupResponseDTO categoryGroupResponseDTO = new CategoryGroupResponseDTO();

        categoryGroupResponseDTO.setId( entity.getId() );
        categoryGroupResponseDTO.setName( entity.getName() );

        return categoryGroupResponseDTO;
    }
}
