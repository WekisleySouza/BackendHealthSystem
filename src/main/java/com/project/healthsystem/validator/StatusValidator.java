package com.project.healthsystem.validator;

import com.project.healthsystem.controller.dto.StatusDTO;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatusValidator {

    private final StatusRepository statusRepository;
    private final StatusMapper statusMapper;

    public Status validateSave(StatusDTO statusDTO){
        return statusMapper.toEntity(statusDTO);
    }

    public Status validateUpdate(StatusDTO statusDTO, long id){
        Status status = statusRepository.findById(id)
            .orElseThrow(()  -> new NotFoundException("Status não encontrado!"));
        status = statusMapper.toEntityWhenHasId(status, statusDTO);
        return status;
    }

    public Status validateFindById(long id){
        return statusRepository.findById(id)
            .orElseThrow(()  -> new NotFoundException("Status não encontrado!"));
    }

    public Status validateDelete(long id){
        return statusRepository.findById(id)
            .orElseThrow(()  -> new NotFoundException("Status não encontrado!"));
    }
}
