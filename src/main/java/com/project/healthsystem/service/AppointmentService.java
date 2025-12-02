package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.AppointmentRequestDTO;
import com.project.healthsystem.controller.mappers.AppointmentsMapper;
import com.project.healthsystem.model.*;
import com.project.healthsystem.repository.*;
import com.project.healthsystem.repository.specs.AppointmentSpecs;
import com.project.healthsystem.repository.specs.SpecsCommon;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.validator.AppointmentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;

    private final AppointmentValidator appointmentValidator;
    private final AppointmentsMapper appointmentsMapper;

    private final JwtTokenProvider jwtTokenProvider;

    public Appointment save(AppointmentRequestDTO appointmentRequestDTO, String token){
        Appointment appointment = appointmentValidator.validateSave(appointmentRequestDTO);
        Employee currentEditor = jwtTokenProvider.getEmployee(token);
        appointment.createdNow();
        appointment.setCreatedBy(currentEditor);
        appointment.setLastModifiedBy(currentEditor);
        return this.repository.save(appointment);
    }

    public Appointment update(AppointmentRequestDTO appointmentRequestDTO, long id, String token){
        Appointment appointment = appointmentValidator.validateUpdate(appointmentRequestDTO, id);
        Employee currentEditor = jwtTokenProvider.getEmployee(token);
        currentEditor.setLastModifiedBy(currentEditor);
        currentEditor.updatedNow();
        return repository.save(appointment);
    }

    public Page<AppointmentRequestDTO> getAll(
            Integer pageNumber,
            Integer pageLength,
            String notes,
            LocalDateTime scheduledAt,
            LocalDateTime createdAt,
            String priorit,
            String status
        ){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        Specification<Appointment> specification = null;
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.notesEqual(notes));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.scheduledAtEqual(scheduledAt));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.createdAtEqual(createdAt));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.prioritEqual(priorit));
        specification = SpecsCommon.addSpec(specification, AppointmentSpecs.statusEqual(status));
        return repository
            .findAll(specification, pageRequest)
            .map(appointmentsMapper::toDto);
    }

    public Appointment findById(long id){
        return appointmentValidator.validateFindById(id);
    }

    public void delete(long id){
        Appointment apointment = appointmentValidator.validateDelete(id);
        repository.delete(apointment);
    }
}
