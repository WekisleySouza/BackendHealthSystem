package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.StatusDTO;
import com.project.healthsystem.controller.mappers.StatusMapper;
import com.project.healthsystem.model.Status;
import com.project.healthsystem.repository.StatusRepository;
import com.project.healthsystem.validator.StatusValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository repository;
    private final StatusValidator statusValidator;
    private final StatusMapper statusMapper;

    public Status save(StatusDTO statusDTO){
        Status status = statusValidator.validateSave(statusDTO);
        return repository.save(status);
    }

    public void update(StatusDTO statusDTO, long id){
        Status status = statusValidator.validateUpdate(statusDTO, id);
        repository.save(status);
    }

    public List<StatusDTO> getAll(){
        return repository.findAll()
            .stream()
            .map(statusMapper::toDto)
            .collect(Collectors.toList());
    }

    public Status findById(long id){
        return this.statusValidator.validateFindById(id);
    }

    public void delete(long id){
        Status status = statusValidator.validateDelete(id);
        repository.delete(status);
    }
}
