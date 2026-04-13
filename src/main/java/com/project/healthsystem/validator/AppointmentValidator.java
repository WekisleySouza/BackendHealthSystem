package com.project.healthsystem.validator;

import com.project.healthsystem.controller.dto.basic_requests.AppointmentRequestDTO;
import com.project.healthsystem.controller.mappers.AppointmentsMapper;
import com.project.healthsystem.exceptions.InvalidDataException;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.*;
import com.project.healthsystem.repository.*;
import com.project.healthsystem.repository.projections.AppointmentGetByIdProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentValidator {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final EmployeeRepository employeeRepository;
    private final ProfessionalRepository professionalRepository;

    private final AppointmentsMapper appointmentsMapper;

    public Appointment validateSave(AppointmentRequestDTO appointmentRequestDTO){
        Patient patient = patientRepository.findById(appointmentRequestDTO.getPatientId())
            .orElseThrow(() -> new InvalidDataException("Patient inválido!"));
        ServiceType serviceType = serviceTypeRepository.findById(appointmentRequestDTO.getServiceTypeId())
            .orElseThrow(() -> new InvalidDataException("ServiceType inválido!"));
        Employee employee = employeeRepository.findById(appointmentRequestDTO.getEmployeeId())
            .orElseThrow(() -> new InvalidDataException("Employee inválido!"));
        Professional professional = professionalRepository.findById(appointmentRequestDTO.getProfessionalId())
            .orElse(null);

        Appointment appointment = appointmentsMapper.toEntity(appointmentRequestDTO);
        appointment.setServiceType(serviceType);
        appointment.setPatient(patient);
        appointment.setEmployee(employee);
        appointment.setProfessional(professional);
        appointment.createdNow();

        return appointment;
    }

    public Appointment validateUpdate(AppointmentRequestDTO appointmentRequestDTO, long id){
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Não foi encontrado um appointment com este id!"));
        Patient patient = patientRepository.findById(appointmentRequestDTO.getPatientId())
            .orElseThrow(() -> new InvalidDataException("Patient inválido!"));
        ServiceType serviceType = serviceTypeRepository.findById(appointmentRequestDTO.getServiceTypeId())
            .orElseThrow(() -> new InvalidDataException("ServiceType inválido!"));
        Employee employee = employeeRepository.findById(appointmentRequestDTO.getEmployeeId())
            .orElseThrow(() -> new InvalidDataException("Employee inválido!"));
        Professional professional = professionalRepository.findById(appointmentRequestDTO.getProfessionalId())
            .orElse(null);

        appointment = appointmentsMapper.toEntityWhenHasId(appointment, appointmentRequestDTO);
        appointment.setServiceType(serviceType);
        appointment.setPatient(patient);
        appointment.setEmployee(employee);
        appointment.setProfessional(professional);

        return appointment;
    }

    public AppointmentGetByIdProjection validateFindById(long id){
        return appointmentRepository.findAppointmentById(id)
            .orElseThrow(() -> new NotFoundException("Não foi encontrado um appointment com este id!"));
    }

    public Appointment validateDelete(long id){
        return appointmentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Não foi encontrado um appointment com este id!"));
    }

    public Appointment validateSetPreSchedulingTo(long id){
        return appointmentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Um dos exames/especialidades não foi encontrado!"));
    }
}
