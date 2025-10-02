package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.ServiceTypeDTO;
import com.project.healthsystem.model.CategoryGroup;
import com.project.healthsystem.model.ServiceType;
import com.project.healthsystem.repository.CategoryGroupRepository;
import com.project.healthsystem.repository.ServiceTypeRepository;
import com.project.healthsystem.validator.ServiceTypeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceTypeService {

    private final ServiceTypeRepository repository;
    private final CategoryGroupRepository categoryGroupRepository;
    private final ServiceTypeValidator serviceTypeValidator;

    public ServiceType save(ServiceTypeDTO serviceTypeDTO){
        ServiceType serviceType = serviceTypeDTO.mappingToServiceType();
        Optional<CategoryGroup> categoryGroup = categoryGroupRepository.findById(serviceTypeDTO.getCategoryGroupId());

        if(categoryGroup.isPresent()){
            serviceType.setCategoryGroup(categoryGroup.get());
        }

        return repository.save(serviceType);
    }

    public void update(ServiceTypeDTO serviceTypeDTO, long id){
        serviceTypeValidator.validate(id);
        Optional<ServiceType> serviceTypesOptional = repository.findById(id);
        Optional<CategoryGroup> categoryGroup = categoryGroupRepository.findById(serviceTypeDTO.getCategoryGroupId());

        ServiceType serviceType = serviceTypesOptional.get();
        serviceType.coppingFromServiceTypesDTO(serviceTypeDTO);

        serviceTypeValidator.validate(serviceType);

        if(categoryGroup.isPresent()){
            serviceType.setCategoryGroup(categoryGroup.get());
        }

        repository.save(serviceType);
    }

    public List<ServiceTypeDTO> getAll(){
        return repository
            .findAll()
            .stream()
            .map(ServiceTypeDTO::new)
            .collect(Collectors.toList());
    }

    public Optional<ServiceType> findById(long id){
        serviceTypeValidator.validate(id);
        return this.repository.findById(id);
    }

    public void delete(long id){
        serviceTypeValidator.validate(id);
        Optional<ServiceType> serviceTypesOptional = repository.findById(id);
        repository.delete(serviceTypesOptional.get());
    }
}