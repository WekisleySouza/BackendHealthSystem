package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.ServiceTypeRequestDTO;
import com.project.healthsystem.controller.mappers.ServiceTypeMapper;
import com.project.healthsystem.model.ServiceType;
import com.project.healthsystem.repository.ServiceTypeRepository;
import com.project.healthsystem.validator.ServiceTypeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceTypeService {

    private final ServiceTypeRepository repository;
    private final ServiceTypeValidator serviceTypeValidator;
    private final ServiceTypeMapper serviceTypeMapper;

    public ServiceType save(ServiceTypeRequestDTO serviceTypeRequestDTO){
        ServiceType serviceType = serviceTypeValidator.validateSave(serviceTypeRequestDTO);
        return repository.save(serviceType);
    }

    public void update(ServiceTypeRequestDTO serviceTypeRequestDTO, long id){
        ServiceType serviceType = serviceTypeValidator.validateUpdate(serviceTypeRequestDTO, id);
        repository.save(serviceType);
    }

    public Page<ServiceTypeRequestDTO> getAll(Integer pageNumber, Integer pageLength){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        return repository
            .findAll(pageRequest)
            .map(serviceTypeMapper::toDto);
    }

    public ServiceType findById(long id){
        return this.serviceTypeValidator.validateFindById(id);
    }

    public void delete(long id){
        ServiceType serviceTypes = serviceTypeValidator.validateDelete(id);;
        repository.delete(serviceTypes);
    }
}