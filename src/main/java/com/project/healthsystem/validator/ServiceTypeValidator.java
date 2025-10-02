package com.project.healthsystem.validator;

import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.ServiceType;
import com.project.healthsystem.repository.ServiceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceTypeValidator {
    @Autowired
    private ServiceTypeRepository serviceTypesRepository;

    public void validate(ServiceType serviceType){

    }

    public void validate(long id){
        if(!exists(id)){
            throw new NotFoundException("Não foi encontrado serviceTypes com este id!");
        }
    }

    public boolean exists(long id){
        return serviceTypesRepository.existsById(id);
    }
}
