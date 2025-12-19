package com.project.healthsystem.validator;

import com.project.healthsystem.controller.dto.AppointmentRequestDTO;
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
    private final PersonRepository personRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final EmployeeRepository employeeRepository;
    private final ProfessionalRepository professionalRepository;

    private final AppointmentsMapper appointmentsMapper;

    public Appointment validateSave(AppointmentRequestDTO appointmentRequestDTO){
        Person person = personRepository.findById(appointmentRequestDTO.getPersonId())
            .orElseThrow(() -> new InvalidDataException("Person inválido!"));
        ServiceType serviceType = serviceTypeRepository.findById(appointmentRequestDTO.getServiceTypeId())
            .orElseThrow(() -> new InvalidDataException("ServiceType inválido!"));
        Employee employee = employeeRepository.findById(appointmentRequestDTO.getEmployeeId())
            .orElseThrow(() -> new InvalidDataException("Employee inválido!"));
        Professional professional = professionalRepository.findById(appointmentRequestDTO.getProfessionalId())
            .orElse(null);

        Appointment appointment = appointmentsMapper.toEntity(appointmentRequestDTO);
        appointment.setServiceType(serviceType);
        appointment.setPerson(person);
        appointment.setEmployee(employee);
        appointment.setProfessional(professional);
        appointment.createdNow();

        return appointment;
    }

    public Appointment validateUpdate(AppointmentRequestDTO appointmentRequestDTO, long id){
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Não foi encontrado um agent com este id!"));
        Person person = personRepository.findById(appointmentRequestDTO.getPersonId())
            .orElseThrow(() -> new InvalidDataException("Person inválido!"));
        ServiceType serviceType = serviceTypeRepository.findById(appointmentRequestDTO.getServiceTypeId())
            .orElseThrow(() -> new InvalidDataException("ServiceType inválido!"));
        Employee employee = employeeRepository.findById(appointmentRequestDTO.getEmployeeId())
            .orElseThrow(() -> new InvalidDataException("Employee inválido!"));
        Professional professional = professionalRepository.findById(appointmentRequestDTO.getProfessionalId())
            .orElse(null);

        appointment = appointmentsMapper.toEntityWhenHasId(appointment, appointmentRequestDTO);
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
