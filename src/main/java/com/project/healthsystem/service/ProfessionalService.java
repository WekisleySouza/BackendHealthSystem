package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.ProfessionalRequestDTO;
import com.project.healthsystem.controller.mappers.ProfessionalMapper;
import com.project.healthsystem.model.Professional;
import com.project.healthsystem.repository.ProfessionalRepository;
import com.project.healthsystem.validator.ProfessionalValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfessionalService {

    private final ProfessionalRepository repository;
    private final ProfessionalValidator professionalValidator;
    private final ProfessionalMapper professionalMapper;

    public Professional save(ProfessionalRequestDTO professionalRequestDTO){
        return repository.save(professionalValidator.validateSave(professionalRequestDTO));
    }

    public void update(ProfessionalRequestDTO professionalRequestDTO, long id){
        Professional professional = professionalValidator.validateUpdate(professionalRequestDTO, id);
        repository.save(professional);
    }

    public Page<ProfessionalRequestDTO> getAll(Integer pageNumber, Integer pageLength){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        return repository
            .findAll(pageRequest)
            .map(professionalMapper::toDto);
    }

    public Professional findById(long id){
        return professionalValidator.validateFindById(id);
    }

    public void delete(long id){
        Professional professional = professionalValidator.validateDelete(id);
        repository.delete(professional);
    }
}
