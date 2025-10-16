package com.project.healthsystem.validator;

import com.project.healthsystem.controller.dto.AppointmentDTO;
import com.project.healthsystem.controller.mappers.AppointmentsMapper;
import com.project.healthsystem.exceptions.InvalidDataException;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.*;
import com.project.healthsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentValidator {

    private final AppointmentRepository appointmentRepository;
    private final StatusRepository statusRepository;
    private final PersonRepository personRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final EmployeeRepository employeeRepository;
    private final ProfessionalRepository professionalRepository;

    private final AppointmentsMapper appointmentsMapper;

    public Appointment validateSave(AppointmentDTO appointmentDTO){
        Status status = statusRepository.findById(appointmentDTO.getStatusId())
            .orElseThrow(() -> new InvalidDataException("Status inválido!"));
        Person person = personRepository.findById(appointmentDTO.getPersonId())
            .orElseThrow(() -> new InvalidDataException("Person inválido!"));
        ServiceType serviceType = serviceTypeRepository.findById(appointmentDTO.getServiceTypeId())
            .orElseThrow(() -> new InvalidDataException("ServiceType inválido!"));
        Employee employee = employeeRepository.findById(appointmentDTO.getEmployeeId())
            .orElseThrow(() -> new InvalidDataException("Employee inválido!"));
        Professional professional = professionalRepository.findById(appointmentDTO.getProfessionalId())
        .orElse(null);

        Appointment appointment = appointmentsMapper.toEntity(appointmentDTO);
        appointment.setStatus(status);
        appointment.setServiceType(serviceType);
        appointment.setPerson(person);
        appointment.setEmployee(employee);
        appointment.setProfessional(professional);
        appointment.createdNow();

        return appointment;
    }

    public Appointment validateUpdate(AppointmentDTO appointmentDTO, long id){
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Não foi encontrado um agent com este id!"));
        Status status = statusRepository.findById(appointmentDTO.getStatusId())
            .orElseThrow(() -> new InvalidDataException("Status inválido!"));
        Person person = personRepository.findById(appointmentDTO.getPersonId())
            .orElseThrow(() -> new InvalidDataException("Person inválido!"));
        ServiceType serviceType = serviceTypeRepository.findById(appointmentDTO.getServiceTypeId())
            .orElseThrow(() -> new InvalidDataException("ServiceType inválido!"));
        Employee employee = employeeRepository.findById(appointmentDTO.getEmployeeId())
            .orElseThrow(() -> new InvalidDataException("Employee inválido!"));
        Professional professional = professionalRepository.findById(appointmentDTO.getProfessionalId())
            .orElse(null);

        appointment = appointmentsMapper.toEntityWhenHasId(appointment, appointmentDTO);
        appointment.setStatus(status);
        appointment.setServiceType(serviceType);
        appointment.setPerson(person);
        appointment.setEmployee(employee);
        appointment.setProfessional(professional);

        return appointment;
    }

    public Appointment validateFindById(long id){
        return appointmentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Não foi encontrado um agent com este id!"));
    }

    public Appointment validateDelete(long id){
        return appointmentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Não foi encontrado um agent com este id!"));
    }
}
