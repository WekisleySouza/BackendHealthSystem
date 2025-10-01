package com.project.healthsystem.validator;

import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Surgery;
import com.project.healthsystem.repository.SurgeryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SurgeryValidator {
    @Autowired
    private SurgeryRepository surgeryRepository;

    public void validate(Surgery surgery){

    }

    public void validate(long id){
        if(!exists(id)){
            throw new NotFoundException("Não foi encontrado surgery com este id!");
        }
    }

    public boolean exists(long id){
        return surgeryRepository.existsById(id);
    }
}
