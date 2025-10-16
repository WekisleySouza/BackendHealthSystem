package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.AppointmentDTO;
import com.project.healthsystem.controller.mappers.AppointmentsMapper;
import com.project.healthsystem.model.*;
import com.project.healthsystem.repository.*;
import com.project.healthsystem.validator.AppointmentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;

    private final AppointmentValidator appointmentValidator;
    private final AppointmentsMapper appointmentsMapper;

    public Appointment save(AppointmentDTO appointmentDTO){
        Appointment appointment = appointmentValidator.validateSave(appointmentDTO);
        return this.repository.save(appointment);
    }

    public Appointment update(AppointmentDTO appointmentDTO, long id){
        Appointment appointment = appointmentValidator.validateUpdate(appointmentDTO, id);
        return repository.save(appointment);
    }

    public List<AppointmentDTO> getAll(){
        return repository
            .findAll()
            .stream()
            .map(appointmentsMapper::toDto)
            .collect(Collectors.toList());
    }

    public Appointment findById(long id){
        return appointmentValidator.validateFindById(id);
    }

    public void delete(long id){
        Appointment apointment = appointmentValidator.validateDelete(id);
        repository.delete(apointment);
    }
}
