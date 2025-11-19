package com.project.healthsystem.validator;

import com.project.healthsystem.controller.dto.ServiceTypeRequestDTO;
import com.project.healthsystem.controller.mappers.ServiceTypeMapper;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.CategoryGroup;
import com.project.healthsystem.model.ServiceType;
import com.project.healthsystem.repository.CategoryGroupRepository;
import com.project.healthsystem.repository.ServiceTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceTypeValidator {

    private final ServiceTypeRepository serviceTypesRepository;
    private final CategoryGroupRepository categoryGroupRepository;

    private final ServiceTypeMapper serviceTypeMapper;

    public ServiceType validateSave(ServiceTypeRequestDTO serviceTypeRequestDTO){
        ServiceType serviceType = serviceTypeMapper.toEntity(serviceTypeRequestDTO);
        CategoryGroup categoryGroup = categoryGroupRepository
            .findById(serviceTypeRequestDTO.getCategoryGroupId())
            .orElse(null);
        serviceType.setCategoryGroup(categoryGroup);
        return serviceType;
    }

    public ServiceType validateUpdate(ServiceTypeRequestDTO serviceTypeRequestDTO, long id){
        ServiceType serviceType = serviceTypesRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Tipo de serviço não encontrado!"));
        CategoryGroup categoryGroup = categoryGroupRepository
            .findById(serviceTypeRequestDTO.getCategoryGroupId())
            .orElse(null);
        serviceType = serviceTypeMapper.toEntityWhenHasId(serviceType, serviceTypeRequestDTO);
        serviceType.setCategoryGroup(categoryGroup);
        return serviceType;
    }

    public ServiceType validateFindById(long id){
        return serviceTypesRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Tipo de serviço não encontrado!"));
    }

    public ServiceType validateDelete(long id){
        return serviceTypesRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Tipo de serviço não encontrado!"));
    }
}
