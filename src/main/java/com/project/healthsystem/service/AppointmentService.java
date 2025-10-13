package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.AppointmentDTO;
import com.project.healthsystem.model.*;
import com.project.healthsystem.repository.*;
import com.project.healthsystem.validator.AppointmentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository repository;
    private final StatusRepository statusRepository;
    private final ProfessionalRepository professionalRepository;
    private final EmployeeRepository employeeRepository;
    private final PersonRepository personRepository;
    private final ServiceTypeRepository specialityRepository;
    private final AppointmentValidator appointmentValidator;

    public Appointment save(AppointmentDTO appointmentDTO){
        Optional<Status> statusOptional = statusRepository.findById(appointmentDTO.getStatusId());
        Optional<Professional> professionalOptional = professionalRepository.findById(appointmentDTO.getProfessionalId());
        Optional<Employee> employeeOptional = employeeRepository.findById(appointmentDTO.getEmployeeId());
        Optional<Person> personOptional = personRepository.findById(appointmentDTO.getPersonId());
        Optional<ServiceType> serviceTypeOptional = specialityRepository.findById(appointmentDTO.getServiceTypeId());

        if(statusOptional.isPresent() && personOptional.isPresent() && serviceTypeOptional.isPresent() && employeeOptional.isPresent()){
            Appointment appointment = appointmentDTO.mappingToConsultation(
                    statusOptional.get(),
                    serviceTypeOptional.get(),
                    personOptional.get(),
                    employeeOptional.get()
            );

            appointment.createdNow();

            if(professionalOptional.isPresent()){
                appointment.setProfessional(professionalOptional.get());
            }

            return appointment;
        }

        return null;
    }

    public boolean update(AppointmentDTO appointmentDTO, long id){
        appointmentValidator.validate(id);
        Optional<Appointment> consultationOptional = repository.findById(id);
        Optional<Status> statusOptional = statusRepository.findById(appointmentDTO.getStatusId());
        Optional<Professional> professionalOptional = professionalRepository.findById(appointmentDTO.getProfessionalId());
        Optional<Employee> employeeOptional = employeeRepository.findById(appointmentDTO.getEmployeeId());
        Optional<Person> personOptional = personRepository.findById(appointmentDTO.getPersonId());
        Optional<ServiceType> serviceTypeOptional = specialityRepository.findById(appointmentDTO.getServiceTypeId());

        if(
            consultationOptional.isEmpty() ||
            statusOptional.isEmpty() ||
            personOptional.isEmpty() ||
            serviceTypeOptional.isEmpty() ||
            employeeOptional.isEmpty()
        ){
            return false;
        }

        Appointment appointment = consultationOptional.get();
        appointmentValidator.validate(appointment);
        appointment.coppingFromConsultationDTO(appointmentDTO);
        appointment.setStatus(statusOptional.get());
        appointment.setPerson(personOptional.get());
        appointment.setEmployee(employeeOptional.get());
        appointment.setServiceType(serviceTypeOptional.get());

        if(professionalOptional.isPresent()){
            appointment.setProfessional(professionalOptional.get());
        }

        repository.save(appointment);
        return true;
    }

    public List<AppointmentDTO> getAll(){
        List<Appointment> appointments = repository.findAll();
        return appointments.stream()
            .map(AppointmentDTO::new)
            .collect(Collectors.toList());
    }

    public Optional<Appointment> findById(long id){
        appointmentValidator.validate(id);
        return this.repository.findById(id);
    }

    public void delete(long id){
        appointmentValidator.validate(id);
        Optional<Appointment> consultationOptional = repository.findById(id);
        repository.delete(consultationOptional.get());
    }
}
