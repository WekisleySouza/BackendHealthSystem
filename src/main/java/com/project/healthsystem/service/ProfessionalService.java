package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.ProfessionalRequestDTO;
import com.project.healthsystem.controller.mappers.ProfessionalMapper;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.model.Professional;
import com.project.healthsystem.repository.ProfessionalRepository;
import com.project.healthsystem.repository.specs.ProfessionalSpecs;
import com.project.healthsystem.repository.specs.SpecsCommon;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.validator.ProfessionalValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ProfessionalService {

    private final ProfessionalRepository repository;
    private final ProfessionalValidator professionalValidator;
    private final ProfessionalMapper professionalMapper;

    private final JwtTokenProvider jwtTokenProvider;

    public Professional save(ProfessionalRequestDTO professionalRequestDTO, String token){
        Professional professional = professionalValidator.validateSave(professionalRequestDTO);
        Employee currentEditor = jwtTokenProvider.getEmployee(token);
        professional.createdNow();
        professional.setCreatedBy(currentEditor);
        professional.setLastModifiedBy(currentEditor);
        return repository.save(professional);
    }

    public void update(ProfessionalRequestDTO professionalRequestDTO, long id, String token){
        Professional professional = professionalValidator.validateUpdate(professionalRequestDTO, id);
        Employee currentEditor = jwtTokenProvider.getEmployee(token);
        professional.setLastModifiedBy(currentEditor);
        professional.updatedNow();
        repository.save(professional);
    }

    public Page<ProfessionalRequestDTO> getAll(
            Integer pageNumber,
            Integer pageLength,
            String name,
            String cpf,
            String phone,
            LocalDate birthday,
            String email
    ){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        Specification<Professional> specs = null;
        specs = SpecsCommon.addSpec(specs, ProfessionalSpecs.nameEqual(name));
        specs = SpecsCommon.addSpec(specs, ProfessionalSpecs.cpfEqual(cpf));
        specs = SpecsCommon.addSpec(specs, ProfessionalSpecs.phoneEqual(phone));
        specs = SpecsCommon.addSpec(specs, ProfessionalSpecs.birthdayEqual(birthday));
        specs = SpecsCommon.addSpec(specs, ProfessionalSpecs.emailEqual(email));
        return repository
            .findAll(specs, pageRequest)
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
