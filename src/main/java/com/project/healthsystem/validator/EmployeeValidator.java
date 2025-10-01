package com.project.healthsystem.validator;

import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmployeeValidator {
    @Autowired
    private EmployeeRepository repository;

    public void validate(Employee employee){

    }

    public void validate(long id){
        if (!exists(id)){
            throw new NotFoundException("Não encontrado employee com este id!");
        }
    }

    private boolean exists(long id){
        return repository.existsById(id);
    }
}
