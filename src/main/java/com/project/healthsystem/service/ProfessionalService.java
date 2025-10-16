package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.ProfessionalDTO;
import com.project.healthsystem.controller.mappers.ProfessionalMapper;
import com.project.healthsystem.model.Professional;
import com.project.healthsystem.repository.ProfessionalRepository;
import com.project.healthsystem.validator.ProfessionalValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfessionalService {

    private final ProfessionalRepository repository;
    private final ProfessionalValidator professionalValidator;
    private final ProfessionalMapper professionalMapper;

    public Professional save(ProfessionalDTO professionalDTO){
        return repository.save(professionalValidator.validateSave(professionalDTO));
    }

    public void update(ProfessionalDTO professionalDTO, long id){
        Professional professional = professionalValidator.validateUpdate(professionalDTO, id);
        repository.save(professional);
    }

    public List<ProfessionalDTO> getAll(){
        List<Professional> professionals = repository.findAll();
        return  professionals.stream()
            .map(professionalMapper::toDto)
            .collect(Collectors.toList());
    }

    public Professional findById(long id){
        return professionalValidator.validateFindById(id);
    }

    public void delete(long id){
        Professional professional = professionalValidator.validateDelete(id);
        repository.delete(professional);
    }
}
