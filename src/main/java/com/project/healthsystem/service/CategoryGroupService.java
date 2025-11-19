package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.CategoryGroupRequestDTO;
import com.project.healthsystem.controller.mappers.CategoryGroupMapper;
import com.project.healthsystem.model.CategoryGroup;
import com.project.healthsystem.repository.CategoryGroupRepository;
import com.project.healthsystem.validator.CategoryGroupValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryGroupService {

    private final CategoryGroupRepository repository;
    private final CategoryGroupValidator categoryGroupValidator;
    private final CategoryGroupMapper categoryGroupMapper;

    public CategoryGroup save(CategoryGroupRequestDTO categoryGroupRequestDTO){
        return repository.save(categoryGroupValidator.validateSave(categoryGroupRequestDTO));
    }

    public void update(CategoryGroupRequestDTO categoryGroupRequestDTO, long id){
        CategoryGroup categoryGroup = categoryGroupValidator.validateUpdate(categoryGroupRequestDTO, id);
        repository.save(categoryGroup);
    }

    public Page<CategoryGroupRequestDTO> getAll(Integer pageNumber, Integer pageLength){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        return repository
            .findAll(pageRequest)
            .map(categoryGroupMapper::toDto);
    }

    public CategoryGroup findById(long id){
        return categoryGroupValidator.validateFindById(id);
    }

    public void delete(long id){
        CategoryGroup categoryGroup = categoryGroupValidator.validateDelete(id);
        repository.delete(categoryGroup);
    }
}