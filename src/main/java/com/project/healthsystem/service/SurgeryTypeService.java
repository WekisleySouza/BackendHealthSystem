package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.SurgeryTypeRequestDTO;
import com.project.healthsystem.controller.mappers.SurgeryTypeMapper;
import com.project.healthsystem.model.SurgeryType;
import com.project.healthsystem.repository.SurgeryTypeRepository;
import com.project.healthsystem.validator.SurgeryTypeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SurgeryTypeService {
    private final SurgeryTypeRepository repository;
    private final SurgeryTypeValidator surgeryTypeValidator;
    private final SurgeryTypeMapper surgeryTypeMapper;

    public SurgeryType save(SurgeryTypeRequestDTO surgeryTypeRequestDTO){
        SurgeryType surgeryType = surgeryTypeValidator.validateSave(surgeryTypeRequestDTO);
        return repository.save(surgeryType);
    }

    public void update(SurgeryTypeRequestDTO surgeryTypeRequestDTO, long id){
        SurgeryType surgeryType = this.surgeryTypeValidator.validateUpdate(surgeryTypeRequestDTO, id);
        repository.save(surgeryType);
    }

    public Page<SurgeryTypeRequestDTO> getAll(Integer pageNumber, Integer pageLength){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        return repository
            .findAll(pageRequest)
            .map(surgeryTypeMapper::toDto);
    }

    public SurgeryType findById(long id){
        return this.surgeryTypeValidator.validateFindById(id);
    }

    public void delete(long id){
        SurgeryType surgeryType = surgeryTypeValidator.validateDelete(id);
        repository.delete(surgeryType);
    }
}
