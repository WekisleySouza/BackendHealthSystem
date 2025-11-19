package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.ConditionRequestDTO;
import com.project.healthsystem.controller.mappers.ConditionMapper;
import com.project.healthsystem.model.Condition;
import com.project.healthsystem.repository.ConditionRepository;
import com.project.healthsystem.validator.ConditionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConditionService {

    private final ConditionRepository repository;
    private final ConditionValidator conditionValidator;
    private final ConditionMapper conditionMapper;

    public Condition save(ConditionRequestDTO conditionRequestDTO){
        Condition condition = this.conditionValidator.validateSave(conditionRequestDTO);
        return repository.save(condition);
    }

    public void update(ConditionRequestDTO conditionRequestDTO, long id){
        Condition condition = this.conditionValidator.validateUpdate(conditionRequestDTO, id);
        repository.save(condition);
    }

    public Page<ConditionRequestDTO> getAll(Integer pageNumber, Integer pageLength){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        return repository
            .findAll(pageRequest)
            .map(conditionMapper::toDto);
    }

    public Condition findById(long id){
        return this.conditionValidator.validateFindById(id);
    }

    public List<Condition> findByIds(List<Long> ids){
        List<Condition> conditions = new ArrayList<>();
        for(long id : ids){
            Condition condition = this.findById(id);
            conditions.add(condition);
        }
        return conditions;
    }

    public void delete(long id){
        Condition condition = conditionValidator.validateDelete(id);
        repository.delete(condition);
    }
}
