package com.project.healthsystem.validator;

import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Condition;
import com.project.healthsystem.repository.ConditionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConditionValidator {

    @Autowired
    private ConditionRepository repository;

    public void validate(Condition condition){

    }

    public void validate(long id){
        if(!exists(id)){
            throw new NotFoundException("Não existe condição com este id!");
        }
    }

    private boolean exists(long id){
        return this.repository.existsById(id);
    }
}
