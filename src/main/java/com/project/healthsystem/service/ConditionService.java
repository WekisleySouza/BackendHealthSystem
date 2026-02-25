package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.ConditionRequestDTO;
import com.project.healthsystem.controller.dto.ConditionResponseDTO;
import com.project.healthsystem.controller.dto.simplified_info.ConditionSimplifiedResponseDTO;
import com.project.healthsystem.controller.mappers.ConditionMapper;
import com.project.healthsystem.model.Condition;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.repository.ConditionRepository;
import com.project.healthsystem.repository.projections.PatientInfoConditionProjection;
import com.project.healthsystem.repository.specs.ConditionSpecs;
import com.project.healthsystem.repository.specs.SpecsCommon;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.validator.ConditionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConditionService {

    private final ConditionRepository repository;
    private final ConditionValidator conditionValidator;
    private final ConditionMapper conditionMapper;

    private final JwtTokenProvider jwtTokenProvider;

    public Condition save(ConditionRequestDTO conditionRequestDTO, String token){
        Condition condition = this.conditionValidator.validateSave(conditionRequestDTO);
        Person currentEditor = jwtTokenProvider.getPerson(token);
        condition.createdNow();
        condition.setCreatedBy(currentEditor);
        condition.setLastModifiedBy(currentEditor);
        return repository.save(condition);
    }

    public void update(ConditionRequestDTO conditionRequestDTO, long id, String token){
        Condition condition = this.conditionValidator.validateUpdate(conditionRequestDTO, id);
        Person currentEditor = jwtTokenProvider.getPerson(token);
        condition.setLastModifiedBy(currentEditor);
        condition.updatedNow();
        repository.save(condition);
    }

    public Page<ConditionSimplifiedResponseDTO> getAllSimplified(Integer pageNumber, Integer pageLength){
        Sort sort = Sort.by("specification").ascending();
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength, sort);
        return repository
            .getAllBy(pageRequest)
            .map(projection -> new ConditionSimplifiedResponseDTO(
                projection.getId(),
                projection.getSpecification()
            ));
    }

    public Page<ConditionResponseDTO> getAll(Integer pageNumber, Integer pageLength, String specification){
        Sort sort = Sort.by("specification").ascending();
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength, sort);
        Specification<Condition> specs = null;
        specs = SpecsCommon.addSpec(specs, ConditionSpecs.specificationLike(specification));
        return repository
            .findAll(specs, pageRequest)
            .map(conditionMapper::toDto);
    }

    public ConditionResponseDTO findById(long id){
        Condition condition = this.conditionValidator.validateFindById(id);
        return conditionMapper.toDto(condition);
    }

    public List<Condition> findByIds(List<Long> ids){
        List<Condition> conditions = new ArrayList<>();
        for(long id : ids){
            Condition condition = this.conditionValidator.validateFindById(id);
            conditions.add(condition);
        }
        return conditions;
    }

    public void delete(long id){
        Condition condition = conditionValidator.validateDelete(id);
        repository.delete(condition);
    }
}
