package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.SurgeryTypeRequestDTO;
import com.project.healthsystem.controller.dto.SurgeryTypeResponseDTO;
import com.project.healthsystem.controller.mappers.SurgeryTypeMapper;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.model.SurgeryType;
import com.project.healthsystem.repository.SurgeryTypeRepository;
import com.project.healthsystem.repository.specs.SpecsCommon;
import com.project.healthsystem.repository.specs.SurgeryTypeSpecs;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.validator.SurgeryTypeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SurgeryTypeService {
    private final SurgeryTypeRepository repository;
    private final SurgeryTypeValidator surgeryTypeValidator;
    private final SurgeryTypeMapper surgeryTypeMapper;

    private final JwtTokenProvider jwtTokenProvider;

    public SurgeryType save(SurgeryTypeRequestDTO surgeryTypeRequestDTO, String token){
        SurgeryType surgeryType = surgeryTypeValidator.validateSave(surgeryTypeRequestDTO);
        Person currentEditor = jwtTokenProvider.getPerson(token);
        surgeryType.createdNow();
        surgeryType.setCreatedBy(currentEditor);
        surgeryType.setLastModifiedBy(currentEditor);
        return repository.save(surgeryType);
    }

    public void update(SurgeryTypeRequestDTO surgeryTypeRequestDTO, long id, String token){
        SurgeryType surgeryType = this.surgeryTypeValidator.validateUpdate(surgeryTypeRequestDTO, id);
        Person currentEditor = jwtTokenProvider.getPerson(token);
        surgeryType.setLastModifiedBy(currentEditor);
        surgeryType.updatedNow();
        repository.save(surgeryType);
    }

    public Page<SurgeryTypeResponseDTO> getAll(Integer pageNumber, Integer pageLength, String type){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        Specification<SurgeryType> specs = null;
        specs = SpecsCommon.addSpec(specs, SurgeryTypeSpecs.typeEqual(type));
        return repository
            .findAll(specs, pageRequest)
            .map(surgeryTypeMapper::toDto);
    }

    public SurgeryTypeResponseDTO findById(long id){
        SurgeryType surgeryType = this.surgeryTypeValidator.validateFindById(id);
        return surgeryTypeMapper.toDto(surgeryType);
    }

    public void delete(long id){
        SurgeryType surgeryType = surgeryTypeValidator.validateDelete(id);
        repository.delete(surgeryType);
    }
}
