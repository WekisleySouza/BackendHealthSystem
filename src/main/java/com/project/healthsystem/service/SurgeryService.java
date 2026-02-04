package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.SurgeryRequestDTO;
import com.project.healthsystem.controller.dto.SurgeryResponseDTO;
import com.project.healthsystem.controller.mappers.SurgeryMapper;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.model.Surgery;
import com.project.healthsystem.repository.SurgeryRepository;
import com.project.healthsystem.repository.specs.SpecsCommon;
import com.project.healthsystem.repository.specs.SurgerySpecs;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.validator.SurgeryValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SurgeryService {

    private final SurgeryRepository repository;
    private final SurgeryValidator surgeryValidator;
    private final SurgeryMapper surgeryMapper;

    private final JwtTokenProvider jwtTokenProvider;

    public Surgery save(SurgeryRequestDTO surgeryRequestDTO, String token){
        Surgery surgery = surgeryValidator.validateSave(surgeryRequestDTO);
        Person currentEditor = jwtTokenProvider.getPerson(token);
        surgery.createdNow();
        surgery.setCreatedBy(currentEditor);
        surgery.setLastModifiedBy(currentEditor);
        return repository.save(surgery);
    }

    public void update(SurgeryRequestDTO surgeryRequestDTO, long id, String token){
        Surgery surgery = surgeryValidator.validateUpdate(surgeryRequestDTO, id);
        Person currentEditor = jwtTokenProvider.getPerson(token);
        surgery.setLastModifiedBy(currentEditor);
        surgery.updatedNow();
        repository.save(surgery);
    }

    public Page<SurgeryResponseDTO> getAll(
            Integer pageNumber,
            Integer pageLength,
            LocalDateTime dateTime,
            String personName,
            String surgeryRisk,
            String location,
            String conclusion,
            String susEasy,
            String sesap,
            LocalDate procedureDate,
            String anesthesicRisk,
            String observation,
            Long surgeryTypeId
    ) {
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        Specification<Surgery> specs = null;
        specs = SpecsCommon.addSpec(specs, SurgerySpecs.dateTimeEqual(dateTime));
        specs = SpecsCommon.addSpec(specs, SurgerySpecs.personNameLike(personName));
        specs = SpecsCommon.addSpec(specs, SurgerySpecs.surgeryRiskLike(surgeryRisk));
        specs = SpecsCommon.addSpec(specs, SurgerySpecs.locationLike(location));
        specs = SpecsCommon.addSpec(specs, SurgerySpecs.conclusionLike(conclusion));
        specs = SpecsCommon.addSpec(specs, SurgerySpecs.susEasyLike(susEasy));
        specs = SpecsCommon.addSpec(specs, SurgerySpecs.sesapLike(sesap));
        specs = SpecsCommon.addSpec(specs, SurgerySpecs.anesthesicRiskLike(anesthesicRisk));
        specs = SpecsCommon.addSpec(specs, SurgerySpecs.procedureDateEqual(procedureDate));
        specs = SpecsCommon.addSpec(specs, SurgerySpecs.observationLike(observation));
        specs = SpecsCommon.addSpec(specs, SurgerySpecs.surgeryTypeIdEqual(surgeryTypeId));

        return repository
            .findAll(specs, pageRequest)
            .map(surgeryMapper::toDto);
    }

    public SurgeryResponseDTO findById(long id){
        Surgery surgery = this.surgeryValidator.validateFindById(id);
        return surgeryMapper.toDto(surgery);
    }

    public void delete(long id){
        Surgery surgery = this.surgeryValidator.validateDelete(id);
        repository.delete(surgery);
    }
}
