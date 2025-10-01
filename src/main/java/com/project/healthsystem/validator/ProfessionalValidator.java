package com.project.healthsystem.validator;

import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Professional;
import com.project.healthsystem.repository.ProfessionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProfessionalValidator {
    @Autowired
    private ProfessionalRepository professionalRepository;

    public void validate(Professional professional){

    }

    public void validate(long id){
        if(!exists(id)){
            throw new NotFoundException("Não foi encontrado professional com este id!");
        }
    }

    public boolean exists(long id){
        return professionalRepository.existsById(id);
    }
}
