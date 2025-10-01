package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.ConsultationDTO;
import com.project.healthsystem.model.*;
import com.project.healthsystem.repository.*;
import com.project.healthsystem.validator.ConsultationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultationService {

    private final ConsultationRepository repository;
    private final StatusRepository statusRepository;
    private final ProfessionalRepository professionalRepository;
    private final PersonRepository personRepository;
    private final SpecialityRepository specialityRepository;
    private final ConsultationValidator consultationValidator;

    public Consultation save(ConsultationDTO consultationDTO){
        Optional<Status> statusOptional = statusRepository.findById(consultationDTO.getStatusId());
        Optional<Professional> professionalOptional = professionalRepository.findById(consultationDTO.getProfessionalId());
        Optional<Person> personOptional = personRepository.findById(consultationDTO.getPersonId());
        Optional<Speciality> specialtyOptional = specialityRepository.findById(consultationDTO.getSpecialtyId());

        if(statusOptional.isPresent() && personOptional.isPresent() && specialtyOptional.isPresent()){
            if(professionalOptional.isPresent()){
                return repository.save(consultationDTO.mappingToConsultation(
                        statusOptional.get(),
                        specialtyOptional.get(),
                        professionalOptional.get(),
                        personOptional.get()
                ));
            }
            return repository.save(consultationDTO.mappingToConsultation(
                    statusOptional.get(),
                    specialtyOptional.get(),
                    personOptional.get()
            ));
        }

        return null;
    }

    public boolean update(ConsultationDTO consultationDTO, long id){
        consultationValidator.validate(id);
        Optional<Consultation> consultationOptional = repository.findById(id);
        Optional<Status> statusOptional = statusRepository.findById(consultationDTO.getStatusId());
        Optional<Professional> professionalOptional = professionalRepository.findById(consultationDTO.getProfessionalId());
        Optional<Person> personOptional = personRepository.findById(consultationDTO.getPersonId());
        Optional<Speciality> specialtyOptional = specialityRepository.findById(consultationDTO.getSpecialtyId());

        if(
            consultationOptional.isEmpty() ||
            statusOptional.isEmpty() ||
            personOptional.isEmpty() ||
            specialtyOptional.isEmpty()
        ){
            return false;
        }

        Consultation consultation = consultationOptional.get();
        consultationValidator.validate(consultation);
        consultation.coppingFromConsultationDTO(consultationDTO);
        consultation.setStatus(statusOptional.get());
        consultation.setPerson(personOptional.get());
        consultation.setSpecialty(specialtyOptional.get());

        if(professionalOptional.isPresent()){
            consultation.setProfessional(professionalOptional.get());
        }

        repository.save(consultation);
        return true;
    }

    public List<ConsultationDTO> getAll(){
        List<Consultation> consultations = repository.findAll();
        return consultations.stream()
            .map(ConsultationDTO::new)
            .collect(Collectors.toList());
    }

    public Optional<Consultation> findById(long id){
        consultationValidator.validate(id);
        return this.repository.findById(id);
    }

    public void delete(long id){
        consultationValidator.validate(id);
        Optional<Consultation> consultationOptional = repository.findById(id);
        repository.delete(consultationOptional.get());
    }
}
