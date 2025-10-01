package com.project.healthsystem.validator;

import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Consultation;
import com.project.healthsystem.repository.ConsultationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConsultationValidator {
    @Autowired
    private ConsultationRepository repository;

    public void validate(Consultation consultation){

    }

    public void validate(long id){
        if (!exists(id)){
            throw new NotFoundException("Não existe Condição com este id!");
        }
    }

    private boolean exists(long id){
        return repository.existsById(id);
    }
}
