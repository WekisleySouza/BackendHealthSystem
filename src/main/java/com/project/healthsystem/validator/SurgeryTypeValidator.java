package com.project.healthsystem.validator;

import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.SurgeryType;
import com.project.healthsystem.repository.SurgeryTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SurgeryTypeValidator {
    @Autowired
    private SurgeryTypeRepository surgeryTypeRepository;

    public void validate(SurgeryType surgeryType){

    }

    public void validate(long id){
        if(!exists(id)){
            throw new NotFoundException("Não foi encontrado surgeryType com este id!");
        }
    }

    public boolean exists(long id){
        return surgeryTypeRepository.existsById(id);
    }
}
