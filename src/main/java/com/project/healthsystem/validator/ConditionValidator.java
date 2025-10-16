package com.project.healthsystem.validator;

import com.project.healthsystem.controller.dto.ConditionDTO;
import com.project.healthsystem.controller.mappers.ConditionMapper;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Condition;
import com.project.healthsystem.repository.ConditionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConditionValidator {

    private final ConditionRepository conditionRepository;
    private final ConditionMapper conditionMapper;

    public Condition validateSave(ConditionDTO conditionDTO){
        return conditionMapper.toEntity(conditionDTO);
    }

    public Condition validateUpdate(ConditionDTO conditionDTO, long id){
        Condition condition = conditionRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Condition não encontrada!"));
        condition = conditionMapper.toEntityWhenHasId(condition, conditionDTO);
        return condition;
    }

    public Condition validateFindById(long id){
        return conditionRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Condition não encontrada!"));
    }

    public Condition validateDelete(long id){
        return conditionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Condition não encontrada!"));
    }
}
