package com.project.healthsystem.service;

import com.project.healthsystem.controller.dto.StatusDTO;
import com.project.healthsystem.model.Status;
import com.project.healthsystem.repository.StatusRepository;
import com.project.healthsystem.validator.StatusValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository repository;
    private final StatusValidator statusValidator;

    public Status save(Status status){
        return repository.save(status);
    }

    public void update(StatusDTO statusDTO, long id){
        statusValidator.validate(id);
        Optional<Status> statusOptional = repository.findById(id);
        var status = statusOptional.get();
        status.coppingFromStatusDTO(statusDTO);
        statusValidator.validate(id);
        repository.save(status);
    }

    public List<StatusDTO> getAll(){
        return repository.findAll()
            .stream()
            .map(StatusDTO::new)
            .collect(Collectors.toList());
    }

    public Optional<Status> findById(long id){
        statusValidator.validate(id);
        return this.repository.findById(id);
    }

    public void delete(long id){
        statusValidator.validate(id);
        Optional<Status> statusOptional = repository.findById(id);
        repository.delete(statusOptional.get());
    }
}
