package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.SurgeryDTO;
import com.project.healthsystem.model.Surgery;
import com.project.healthsystem.model.SurgeryType;
import com.project.healthsystem.repository.SurgeryRepository;
import com.project.healthsystem.repository.SurgeryTypeRepository;
import com.project.healthsystem.validator.SurgeryValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurgeryService {

    private final SurgeryRepository repository;
    private final SurgeryTypeRepository surgeryTypeRepository;
    private final SurgeryValidator surgeryValidator;

    public Surgery save(SurgeryDTO surgeryDTO){
        Optional<SurgeryType> surgeryTypeOptional = surgeryTypeRepository.findById(surgeryDTO.getSurgeryTypeId());

        if(surgeryTypeOptional.isEmpty()){
            return null;
        }

        return repository.save(surgeryDTO.mappingToSurgery(surgeryTypeOptional.get()));
    }

    public void update(SurgeryDTO surgeryDTO, long id){
        surgeryValidator.validate(id);
        Optional<Surgery> surgeryOptional = repository.findById(id);
        Optional<SurgeryType> surgeryTypeOptional = surgeryTypeRepository.findById(surgeryDTO.getSurgeryTypeId());

        var surgery = surgeryOptional.get();
        surgery.coppingFromSurgeryDTO(surgeryDTO);
        surgery.setSurgeryType(surgeryTypeOptional.get());

        surgeryValidator.validate(surgery);
        repository.save(surgery);
    }

    public List<SurgeryDTO> getAll(){
        return repository
            .findAll()
            .stream()
            .map(SurgeryDTO::new)
            .collect(Collectors.toList());
    }

    public Optional<Surgery> findById(long id){
        return this.repository.findById(id);
    }

    public void delete(long id){
        surgeryValidator.validate(id);
        Optional<Surgery> surgeryOptional = repository.findById(id);
        repository.delete(surgeryOptional.get());
    }
}
