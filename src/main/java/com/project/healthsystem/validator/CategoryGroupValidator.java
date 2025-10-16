package com.project.healthsystem.validator;

import com.project.healthsystem.controller.dto.CategoryGroupDTO;
import com.project.healthsystem.controller.mappers.CategoryGroupMapper;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.CategoryGroup;
import com.project.healthsystem.repository.CategoryGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryGroupValidator {

    private final CategoryGroupRepository categoryGroupRepository;
    private final CategoryGroupMapper categoryGroupMapper;

    public CategoryGroup validateSave(CategoryGroupDTO categoryGroupDTO){
        return categoryGroupMapper.toEntity(categoryGroupDTO);
    }

    public CategoryGroup validateUpdate(CategoryGroupDTO categoryGroupDTO, long id){
        CategoryGroup categoryGroup = categoryGroupRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("CategoryGroup não encontrado!"));
        categoryGroup = categoryGroupMapper.toEntityWhenHasId(categoryGroup, categoryGroupDTO);
        return categoryGroup;
    }

    public CategoryGroup validateFindById(long id){
        return categoryGroupRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("CategoryGroup não encontrado!"));
    }

    public CategoryGroup validateDelete(long id){
        return categoryGroupRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("CategoryGroup não encontrado!"));
    }
}
