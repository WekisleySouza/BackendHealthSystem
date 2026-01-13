package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.ServiceTypeRequestDTO;
import com.project.healthsystem.controller.dto.ServiceTypeResponseDTO;
import com.project.healthsystem.controller.mappers.ServiceTypeMapper;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.model.ServiceType;
import com.project.healthsystem.repository.ServiceTypeRepository;
import com.project.healthsystem.repository.specs.ServiceTypeSpecs;
import com.project.healthsystem.repository.specs.SpecsCommon;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.validator.ServiceTypeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ServiceTypeService {

    private final ServiceTypeRepository repository;
    private final ServiceTypeValidator serviceTypeValidator;
    private final ServiceTypeMapper serviceTypeMapper;

    private final JwtTokenProvider jwtTokenProvider;

    public ServiceType save(ServiceTypeRequestDTO serviceTypeRequestDTO, String token){
        ServiceType serviceType = serviceTypeValidator.validateSave(serviceTypeRequestDTO);
        Person currentEditor = jwtTokenProvider.getPerson(token);
        serviceType.createdNow();
        serviceType.setCreatedBy(currentEditor);
        serviceType.setLastModifiedBy(currentEditor);
        return repository.save(serviceType);
    }

    public void update(ServiceTypeRequestDTO serviceTypeRequestDTO, long id, String token){
        ServiceType serviceType = serviceTypeValidator.validateUpdate(serviceTypeRequestDTO, id);
        Person currentEditor = jwtTokenProvider.getPerson(token);
        serviceType.setLastModifiedBy(currentEditor);
        serviceType.updatedNow();
        repository.save(serviceType);
    }

    public Page<ServiceTypeResponseDTO> getAll(
        Integer pageNumber,
        Integer pageLength,
        String name,
        String type,
        BigDecimal value
    ){
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength);
        Specification<ServiceType> specs = null;
        specs = SpecsCommon.addSpec(specs, ServiceTypeSpecs.nameEqual(name));
        specs = SpecsCommon.addSpec(specs, ServiceTypeSpecs.typeEqual(type));
        specs = SpecsCommon.addSpec(specs, ServiceTypeSpecs.valueEqual(value));
        return repository
            .findAll(specs, pageRequest)
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