package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.controller.dto.ServiceTypeDTO;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.ServiceType;
import com.project.healthsystem.service.ServiceTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/service-type")
@RequiredArgsConstructor
public class ServiceTypeController {

    private final ServiceTypeService serviceTypeService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody ServiceTypeDTO serviceTypeDTO){
        ServiceType serviceType = serviceTypeService.save(serviceTypeDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(serviceType.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody ServiceTypeDTO serviceTypeDTO){
        try{
            serviceTypeService.update(serviceTypeDTO, id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException err){
            ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.notFound(err.getMessage());
            return ResponseEntity.status(errorResponseDTO.status()).body(errorResponseDTO);
        }
    }

    @GetMapping
    public ResponseEntity<List<ServiceTypeDTO>> readAll(){
        List<ServiceTypeDTO> serviceTypeDTOS = serviceTypeService.getAll();
        return ResponseEntity.ok(serviceTypeDTOS);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        try{
            serviceTypeService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException err){
            ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.notFound(err.getMessage());
            return ResponseEntity.status(errorResponseDTO.status()).body(errorResponseDTO);
        }

    }
}