package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.CategoryGroupRequestDTO;
import com.project.healthsystem.controller.dto.CategoryGroupResponseDTO;
import com.project.healthsystem.controller.mappers.CategoryGroupMapper;
import com.project.healthsystem.model.CategoryGroup;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.repository.CategoryGroupRepository;
import com.project.healthsystem.repository.specs.CategoryGroupSpecs;
import com.project.healthsystem.repository.specs.SpecsCommon;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.validator.CategoryGroupValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryGroupService {

    private final CategoryGroupRepository repository;
    private final CategoryGroupValidator categoryGroupValidator;
    private final CategoryGroupMapper categoryGroupMapper;

    private final JwtTokenProvider jwtTokenProvider;

    public CategoryGroup save(CategoryGroupRequestDTO categoryGroupRequestDTO, String token){
        CategoryGroup categoryGroup = categoryGroupValidator.validateSave(categoryGroupRequestDTO);
        Employee currentEditor = jwtTokenProvider.getEmployee(token);
        categoryGroup.createdNow();
        categoryGroup.setCreatedBy(currentEditor);
        categoryGroup.setLastModifiedBy(currentEditor);
        return repository.save(categoryGroup);
    }

    public void update(CategoryGroupRequestDTO categoryGroupRequestDTO, long id, String token){
        CategoryGroup categoryGroup = categoryGroupValidator.validateUpdate(categoryGroupRequestDTO, id);
        Employee currentEditor = jwtTokenProvider.getEmployee(token);
        categoryGroup.setLastModifiedBy(currentEditor);
        categoryGroup.updatedNow();
        repository.save(categoryGroup);
    }

    public Page<CategoryGroupResponseDTO> getAll(
        Integer pageNumber,
        Integer pageLength,
        String name
    ){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        Specification<CategoryGroup> specs = null;
        specs = SpecsCommon.addSpec(specs, CategoryGroupSpecs.nameEqual(name));
        return repository
            .findAll(specs, pageRequest)
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