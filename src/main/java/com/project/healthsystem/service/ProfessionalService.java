package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.ProfessionalDTO;
import com.project.healthsystem.model.Professional;
import com.project.healthsystem.repository.ProfessionalRepository;
import com.project.healthsystem.validator.ProfessionalValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfessionalService {

    private final ProfessionalRepository repository;
    private final ProfessionalValidator professionalValidator;

    public Professional save(Professional professional){
        return repository.save(professional);
    }

    public void update(ProfessionalDTO professionalDTO, long id){
        professionalValidator.validate(id);
        Optional<Professional> professionalOptional = repository.findById(id);
        var professional = professionalOptional.get();
        professional.coppingFromProfessionalDTO(professionalDTO);

        professionalValidator.validate(professional);
        repository.save(professional);
    }

    public List<ProfessionalDTO> getAll(){
        List<Professional> professionals = repository.findAll();
        return  professionals.stream()
            .map(ProfessionalDTO::new)
            .collect(Collectors.toList());
    }

    public Optional<Professional> findById(long id){
        professionalValidator.validate(id);
        return this.repository.findById(id);
    }

    public void delete(long id){
        professionalValidator.validate(id);
        Optional<Professional> professionalOptional = repository.findById(id);
        repository.delete(professionalOptional.get());
    }
}
