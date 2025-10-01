package com.project.healthsystem.validator;

import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Status;
import com.project.healthsystem.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StatusValidator {
    @Autowired
    private StatusRepository statusRepository;

    public void validate(Status status){

    }

    public void validate(long id){
        if(!exists(id)){
            throw new NotFoundException("Não foi encontrado status com este id!");
        }
    }

    public boolean exists(long id){
        return statusRepository.existsById(id);
    }
}
