package com.project.healthsystem.validator;

import com.project.healthsystem.controller.dto.basic_requests.ServiceTypeRequestDTO;
import com.project.healthsystem.controller.mappers.ServiceTypeMapper;
import com.project.healthsystem.exceptions.CantDeleteException;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.CategoryGroup;
import com.project.healthsystem.model.ServiceType;
import com.project.healthsystem.repository.AppointmentRepository;
import com.project.healthsystem.repository.CategoryGroupRepository;
import com.project.healthsystem.repository.ServiceTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceTypeValidator {

    private final ServiceTypeRepository serviceTypesRepository;
    private final AppointmentRepository appointmentRepository;
    private final CategoryGroupRepository categoryGroupRepository;

    private final ServiceTypeMapper serviceTypeMapper;

    public ServiceType validateSave(ServiceTypeRequestDTO serviceTypeRequestDTO){
        ServiceType serviceType = serviceTypeMapper.toEntity(serviceTypeRequestDTO);
        if(serviceTypeRequestDTO.getCategoryGroupId() != null && serviceTypeRequestDTO.getCategoryGroupId() != -1){
            CategoryGroup categoryGroup = categoryGroupRepository
                .findById(serviceTypeRequestDTO.getCategoryGroupId())
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada!"));
            serviceType.setCategoryGroup(categoryGroup);
        }
        return serviceType;
    }

    public ServiceType validateUpdate(ServiceTypeRequestDTO serviceTypeRequestDTO, long id){
        ServiceType serviceType = serviceTypesRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Tipo de serviço não encontrado!"));
        serviceType = serviceTypeMapper.toEntityWhenHasId(serviceType, serviceTypeRequestDTO);
        if(serviceTypeRequestDTO.getCategoryGroupId() != null) {
            CategoryGroup categoryGroup = categoryGroupRepository
                .findById(serviceTypeRequestDTO.getCategoryGroupId())
                .orElse(null);
            serviceType.setCategoryGroup(categoryGroup);
        }
        return serviceType;
    }

    public ServiceType validateFindById(long id){
        return serviceTypesRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Tipo de serviço não encontrado!"));
    }

    public ServiceType validateDeleteWhenInnered(long oldId, long newId){
        serviceTypesRepository
            .findById(newId)
            .orElseThrow(() -> new NotFoundException("Novo tipo de serviço não encontrado!"));
        return serviceTypesRepository
            .findById(oldId)
            .orElseThrow(() -> new NotFoundException("Tipo de serviço atual não encontrado!"));
    }

    public ServiceType validateDelete(long id){
        ServiceType serviceType = serviceTypesRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Tipo de serviço não encontrado!"));

        if(appointmentRepository.existsByServiceTypeId(id)){
            throw new CantDeleteException("Operação não permitida. Este tipo de serviço tem consultas atreladas a ele!");
        }

        return serviceType;
    }
}
