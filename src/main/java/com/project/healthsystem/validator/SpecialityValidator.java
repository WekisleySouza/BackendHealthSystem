package com.project.healthsystem.validator;

import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Speciality;
import com.project.healthsystem.repository.SpecialityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpecialityValidator {
    @Autowired
    private SpecialityRepository specialityRepository;

    public void validate(Speciality speciality){

    }

    public void validate(long id){
        if(!exists(id)){
            throw new NotFoundException("Não foi encontrado speciality com este id!");
        }
    }

    public boolean exists(long id){
        return specialityRepository.existsById(id);
    }
}
