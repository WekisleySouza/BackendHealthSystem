package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.basic_requests.ServiceTypeRequestDTO;
import com.project.healthsystem.controller.dto.ServiceTypeResponseDTO;
import com.project.healthsystem.controller.dto.simplified_info.ServiceTypeSimplifiedResponseDTO;
import com.project.healthsystem.controller.mappers.ServiceTypeMapper;
import com.project.healthsystem.model.Person;
import com.project.healthsystem.model.ServiceType;
import com.project.healthsystem.model.ServiceTypes;
import com.project.healthsystem.repository.ServiceTypeRepository;
import com.project.healthsystem.repository.specs.ServiceTypeSpecs;
import com.project.healthsystem.repository.specs.SpecsCommon;
import com.project.healthsystem.security.JwtTokenProvider;
import com.project.healthsystem.validator.ServiceTypeValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ServiceTypeService {

    private final ServiceTypeRepository repository;
    private final ServiceTypeValidator serviceTypeValidator;
    private final ServiceTypeMapper serviceTypeMapper;

    private final JwtTokenProvider jwtTokenProvider;
    private final AppointmentService appointmentService;

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

    public Page<ServiceTypeSimplifiedResponseDTO> getAllSimplified(
        Integer pageNumber,
        Integer pageLength
    ){
        Sort sort = Sort.by("name").ascending();
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength, sort);
        return repository
            .getAllBy(pageRequest)
            .map(projection -> new ServiceTypeSimplifiedResponseDTO(
                projection.getId(),
                projection.getName()
            ));
    }

    public Page<ServiceTypeResponseDTO> getAll(
        Integer pageNumber,
        Integer pageLength,
        String code,
        String name,
        String type,
        String categoryGroupName,
        BigDecimal value
    ){
        Sort sort = Sort.by("name").ascending();
        Pageable pageRequest = PageRequest.of(pageNumber, pageLength, sort);
        Specification<ServiceType> specs = null;
        specs = SpecsCommon.addSpec(specs, ServiceTypeSpecs.codeLike(code));
        specs = SpecsCommon.addSpec(specs, ServiceTypeSpecs.nameLike(name));
        specs = SpecsCommon.addSpec(specs, ServiceTypeSpecs.categoryGroupNameLike(categoryGroupName));
        specs = SpecsCommon.addSpec(specs, ServiceTypeSpecs.typeLike(type));
        specs = SpecsCommon.addSpec(specs, ServiceTypeSpecs.valueEqual(value));
        return repository
            .findAll(specs, pageRequest)
            .map(serviceTypeMapper::toDto);
    }

    public ServiceTypeResponseDTO findById(long id){
        ServiceType serviceType = this.serviceTypeValidator.validateFindById(id);
        return serviceTypeMapper.toDto(serviceType);
    }

    public int deleteWhenNewServiceType(long id, ServiceTypeRequestDTO serviceTypeRequestDTO, String token){
        ServiceType serviceType = this.save(serviceTypeRequestDTO, token);
        int updatedAppointmentsNumber = this.appointmentService.switchServiceTypeId(id, serviceType.getId());
        ServiceType serviceTypes = serviceTypeValidator.validateDelete(id);
        repository.delete(serviceTypes);
        return updatedAppointmentsNumber;
    }

    public int deleteWhenInnered(long id, long newId){
        ServiceType serviceType = serviceTypeValidator.validateDeleteWhenInnered(id, newId);
        int updatedAppointmentsNumber = this.appointmentService.switchServiceTypeId(id, newId);
        repository.delete(serviceType);
        return updatedAppointmentsNumber;
    }

    public void delete(long id){
        ServiceType serviceTypes = serviceTypeValidator.validateDelete(id);;
        repository.delete(serviceTypes);
    }

    @Transactional
    public void importCsv(MultipartFile file){
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;

            // Pular header
            reader.readLine();

            while((line = reader.readLine()) != null){
                String[] columns = line.split(";");

                String sigtapCode = columns[0];
                String name = columns[1];
                BigDecimal price = new BigDecimal(columns[2]);
                String type = columns[3];

                ServiceType serviceType = new ServiceType();
                serviceType.setSigtapCode(sigtapCode);
                serviceType.setName(name);
                serviceType.setType(ServiceTypes.fromLabel(type));
                serviceType.setValue(price);
                serviceType.createdNow();

                repository.save(serviceType);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao importar CSV" + e.getMessage());
        }
    }
}