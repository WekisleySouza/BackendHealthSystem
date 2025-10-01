package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.SurgeryTypeDTO;
import com.project.healthsystem.model.SurgeryType;
import com.project.healthsystem.repository.SurgeryTypeRepository;
import com.project.healthsystem.validator.SurgeryTypeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurgeryTypeService {
    private final SurgeryTypeRepository repository;
    private final SurgeryTypeValidator surgeryTypeValidator;

    public SurgeryType save(SurgeryType surgeryType){
        return repository.save(surgeryType);
    }

    public void update(SurgeryTypeDTO surgeryTypeDTO, long id){
        surgeryTypeValidator.validate(id);

        Optional<SurgeryType> surgeryTypeOptional = repository.findById(id);
        var surgeryType = surgeryTypeOptional.get();
        surgeryType.coppingFromSurgeryTypeDTO(surgeryTypeDTO);

        surgeryTypeValidator.validate(surgeryType);
        repository.save(surgeryType);
    }

    public List<SurgeryTypeDTO> getAll(){
        return repository
            .findAll()
            .stream()
            .map(SurgeryTypeDTO::new)
            .collect(Collectors.toList());
    }

    public Optional<SurgeryType> findById(long id){
        surgeryTypeValidator.validate(id);
        return this.repository.findById(id);
    }

    public void delete(long id){
        surgeryTypeValidator.validate(id);
        Optional<SurgeryType> surgeryTypeOptional = repository.findById(id);
        repository.delete(surgeryTypeOptional.get());
    }
}
