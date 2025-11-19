package com.project.healthsystem.validator;

import com.project.healthsystem.controller.dto.CategoryGroupRequestDTO;
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

    public CategoryGroup validateSave(CategoryGroupRequestDTO categoryGroupRequestDTO){
        return categoryGroupMapper.toEntity(categoryGroupRequestDTO);
    }

    public CategoryGroup validateUpdate(CategoryGroupRequestDTO categoryGroupRequestDTO, long id){
        CategoryGroup categoryGroup = categoryGroupRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("CategoryGroup não encontrado!"));
        categoryGroup = categoryGroupMapper.toEntityWhenHasId(categoryGroup, categoryGroupRequestDTO);
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
