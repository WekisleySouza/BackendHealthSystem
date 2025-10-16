package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.ServiceTypeDTO;
import com.project.healthsystem.controller.mappers.ServiceTypeMapper;
import com.project.healthsystem.model.ServiceType;
import com.project.healthsystem.repository.CategoryGroupRepository;
import com.project.healthsystem.repository.ServiceTypeRepository;
import com.project.healthsystem.validator.ServiceTypeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceTypeService {

    private final ServiceTypeRepository repository;
    private final ServiceTypeValidator serviceTypeValidator;
    private final ServiceTypeMapper serviceTypeMapper;

    public ServiceType save(ServiceTypeDTO serviceTypeDTO){
        ServiceType serviceType = serviceTypeValidator.validateSave(serviceTypeDTO);
        return repository.save(serviceType);
    }

    public void update(ServiceTypeDTO serviceTypeDTO, long id){
        ServiceType serviceType = serviceTypeValidator.validateUpdate(serviceTypeDTO, id);
        repository.save(serviceType);
    }

    public List<ServiceTypeDTO> getAll(){
        return repository
            .findAll()
            .stream()
            .map(serviceTypeMapper::toDto)
            .collect(Collectors.toList());
    }

    public ServiceType findById(long id){
        return this.serviceTypeValidator.validateFindById(id);
    }

    public void delete(long id){
        ServiceType serviceTypes = serviceTypeValidator.validateDelete(id);;
        repository.delete(serviceTypes);
    }
}