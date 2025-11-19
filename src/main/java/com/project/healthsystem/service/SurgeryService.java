package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.SurgeryRequestDTO;
import com.project.healthsystem.controller.mappers.SurgeryMapper;
import com.project.healthsystem.model.Surgery;
import com.project.healthsystem.repository.SurgeryRepository;
import com.project.healthsystem.validator.SurgeryValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SurgeryService {

    private final SurgeryRepository repository;
    private final SurgeryValidator surgeryValidator;
    private final SurgeryMapper surgeryMapper;

    public Surgery save(SurgeryRequestDTO surgeryRequestDTO){
        Surgery surgery = surgeryValidator.validateSave(surgeryRequestDTO);
        return repository.save(surgery);
    }

    public void update(SurgeryRequestDTO surgeryRequestDTO, long id){
        Surgery surgery = surgeryValidator.validateUpdate(surgeryRequestDTO, id);
        repository.save(surgery);
    }

    public Page<SurgeryRequestDTO> getAll(Integer pageNumber, Integer pageLength){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        return repository
            .findAll(pageRequest)
            .map(surgeryMapper::toDto);
    }

    public Surgery findById(long id){
        return this.surgeryValidator.validateFindById(id);
    }

    public void delete(long id){
        Surgery surgery = this.surgeryValidator.validateDelete(id);
        repository.delete(surgery);
    }
}
