package com.project.healthsystem.validator;

import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Appointment;
import com.project.healthsystem.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentValidator {
    @Autowired
    private AppointmentRepository repository;

    public void validate(Appointment appointment){

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
