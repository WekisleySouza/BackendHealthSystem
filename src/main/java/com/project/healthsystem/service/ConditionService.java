package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.ConditionDTO;
import com.project.healthsystem.controller.mappers.ConditionMapper;
import com.project.healthsystem.model.Condition;
import com.project.healthsystem.repository.ConditionRepository;
import com.project.healthsystem.validator.ConditionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConditionService {

    private final ConditionRepository repository;
    private final ConditionValidator conditionValidator;
    private final ConditionMapper conditionMapper;

    public Condition save(ConditionDTO conditionDTO){
        Condition condition = this.conditionValidator.validateSave(conditionDTO);
        return repository.save(condition);
    }

    public void update(ConditionDTO conditionDTO, long id){
        Condition condition = this.conditionValidator.validateUpdate(conditionDTO, id);
        repository.save(condition);
    }

    public List<ConditionDTO> getAll(){
        List<Condition> conditions = repository.findAll();
        return conditions.stream()
                .map(conditionMapper::toDto)
                .collect(Collectors.toList());
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
