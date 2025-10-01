package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.CategoryGroupDTO;
import com.project.healthsystem.model.CategoryGroup;
import com.project.healthsystem.repository.CategoryGroupRepository;
import com.project.healthsystem.validator.CategoryGroupValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryGroupService {

    private final CategoryGroupRepository repository;
    private final CategoryGroupValidator categoryGroupValidator;

    public CategoryGroup save(CategoryGroup categoryGroup){
        return repository.save(categoryGroup);
    }

    public void update(CategoryGroupDTO categoryGroupDTO, long id){
        categoryGroupValidator.validate(id);
        Optional<CategoryGroup> categoryGroupOptional = repository.findById(id);

        var categoryGroup = categoryGroupOptional.get();
        categoryGroup.coppingFromCategoryGroupDTO(categoryGroupDTO);
        categoryGroupValidator.validate(categoryGroup);

        repository.save(categoryGroup);
    }

    public List<CategoryGroupDTO> getAll(){
        List<CategoryGroup> categoryGroups = repository.findAll();
        return categoryGroups.stream()
                .map(CategoryGroupDTO::new)
                .collect(Collectors.toList());
    }

    public Optional<CategoryGroup> findById(long id){
        categoryGroupValidator.validate(id);
        return this.repository.findById(id);
    }

    public void delete(long id){
        categoryGroupValidator.validate(id);

        Optional<CategoryGroup> categoryGroupOptional = repository.findById(id);
        repository.delete(categoryGroupOptional.get());
    }
}