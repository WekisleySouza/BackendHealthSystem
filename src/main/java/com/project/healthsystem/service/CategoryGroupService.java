package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.CategoryGroupRequestDTO;
import com.project.healthsystem.controller.dto.CategoryGroupResponseDTO;
import com.project.healthsystem.controller.dto.simplified_info.CategoryGroupSimplifiedResponseDTO;
import com.project.healthsystem.controller.mappers.CategoryGroupMapper;
import com.project.healthsystem.model.CategoryGroup;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.repository.CategoryGroupRepository;
import com.project.healthsystem.repository.specs.CategoryGroupSpecs;
import com.project.healthsystem.repository.specs.SpecsCommon;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.validator.CategoryGroupValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        Person currentEditor = jwtTokenProvider.getPerson(token);
        categoryGroup.createdNow();
        categoryGroup.setCreatedBy(currentEditor);
        categoryGroup.setLastModifiedBy(currentEditor);
        return repository.save(categoryGroup);
    }

    public void update(CategoryGroupRequestDTO categoryGroupRequestDTO, long id, String token){
        CategoryGroup categoryGroup = categoryGroupValidator.validateUpdate(categoryGroupRequestDTO, id);
        Person currentEditor = jwtTokenProvider.getPerson(token);
        categoryGroup.setLastModifiedBy(currentEditor);
        categoryGroup.updatedNow();
        repository.save(categoryGroup);
    }

    public Page<CategoryGroupSimplifiedResponseDTO> getAllSimplified(
            Integer pageNumber,
            Integer pageLength
    ){
        Sort sort = Sort.by("name").ascending();
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength, sort);
        return repository
            .getAllBy(pageRequest)
            .map(projection -> new CategoryGroupSimplifiedResponseDTO(
                projection.getId(),
                projection.getName()
            ));
    }

    public Page<CategoryGroupResponseDTO> getAll(
        Integer pageNumber,
        Integer pageLength,
        String name
    ){
        Sort sort = Sort.by("name").ascending();
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength, sort);
        Specification<CategoryGroup> specs = null;
        specs = SpecsCommon.addSpec(specs, CategoryGroupSpecs.nameLike(name));
        return repository
            .findAll(specs, pageRequest)
            .map(categoryGroupMapper::toDto);
    }

    public CategoryGroupResponseDTO findById(long id){
        CategoryGroup categoryGroup = categoryGroupValidator.validateFindById(id);
        return categoryGroupMapper.toDto(categoryGroup);
    }

    public void delete(long id){
        CategoryGroup categoryGroup = categoryGroupValidator.validateDelete(id);
        repository.delete(categoryGroup);
    }
}