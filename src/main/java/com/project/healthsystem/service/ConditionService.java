package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.ConditionDTO;
import com.project.healthsystem.model.Condition;
import com.project.healthsystem.repository.ConditionRepository;
import com.project.healthsystem.validator.ConditionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConditionService {

    private final ConditionRepository repository;
    private final ConditionValidator conditionValidator;

    public Condition save(Condition condition){
        this.conditionValidator.validate(condition);
        return repository.save(condition);
    }

    public void update(ConditionDTO conditionDTO, long id){
        this.conditionValidator.validate(id);

        Optional<Condition> conditionOptional = repository.findById(id);

        var condition = conditionOptional.get();

        condition.coppingFromConditionDTO(conditionDTO);

        repository.save(condition);
    }

    public List<ConditionDTO> getAll(){
        List<Condition> conditions = repository.findAll();
        return conditions.stream()
                .map(ConditionDTO::new)
                .collect(Collectors.toList());
    }

    public Optional<Condition> findById(long id){
        this.conditionValidator.validate(id);

        return this.repository.findById(id);
    }

    public List<Condition> findByIds(List<Long> ids){
        List<Condition> conditions = new ArrayList<>();
        for(long id : ids){
            conditionValidator.validate(id);
            Optional<Condition> condition = repository.findById(id);
            conditions.add(condition.get());
        }
        return conditions;
    }

    public void delete(long id){
        conditionValidator.validate(id);
        Optional<Condition> conditionOptional = repository.findById(id);
        repository.delete(conditionOptional.get());
    }
}
