package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.CategoryGroupDTO;
import com.project.healthsystem.controller.mappers.CategoryGroupMapper;
import com.project.healthsystem.model.CategoryGroup;
import com.project.healthsystem.repository.CategoryGroupRepository;
import com.project.healthsystem.validator.CategoryGroupValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryGroupService {

    private final CategoryGroupRepository repository;
    private final CategoryGroupValidator categoryGroupValidator;
    private final CategoryGroupMapper categoryGroupMapper;

    public CategoryGroup save(CategoryGroupDTO categoryGroupDTO){
        return repository.save(categoryGroupValidator.validateSave(categoryGroupDTO));
    }

    public void update(CategoryGroupDTO categoryGroupDTO, long id){
        CategoryGroup categoryGroup = categoryGroupValidator.validateUpdate(categoryGroupDTO, id);
        repository.save(categoryGroup);
    }

    public List<CategoryGroupDTO> getAll(){
        List<CategoryGroup> categoryGroups = repository.findAll();
        return categoryGroups.stream()
            .map(categoryGroupMapper::toDto)
            .collect(Collectors.toList());
    }

    public CategoryGroup findById(long id){
        return categoryGroupValidator.validateFindById(id);
    }

    public void delete(long id){
        CategoryGroup categoryGroup = categoryGroupValidator.validateDelete(id);
        repository.delete(categoryGroup);
    }
}