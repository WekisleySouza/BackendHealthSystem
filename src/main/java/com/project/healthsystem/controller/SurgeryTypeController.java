package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.controller.dto.SurgeryTypeDTO;
import com.project.healthsystem.exceptions.NotFoundException;
import com.project.healthsystem.model.SurgeryType;
import com.project.healthsystem.service.SurgeryTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/surgery-type")
@RequiredArgsConstructor
public class SurgeryTypeController {
    private final SurgeryTypeService surgeryTypeService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody SurgeryTypeDTO surgeryTypeDTO){
        SurgeryType surgeryTypeEntity = surgeryTypeDTO.mappingToSurgeryType();
        surgeryTypeService.save(surgeryTypeEntity);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(surgeryTypeEntity.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody SurgeryTypeDTO surgeryTypeDTO){
        try{
            surgeryTypeService.update(surgeryTypeDTO, id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException err){
            ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.notFound(err.getMessage());
            return ResponseEntity.status(errorResponseDTO.status()).body(errorResponseDTO);
        }
    }

    @GetMapping
    public ResponseEntity<List<SurgeryTypeDTO>> readAll(){
        List<SurgeryTypeDTO> surgeryTypeDTOS = surgeryTypeService.getAll();
        return ResponseEntity.ok(surgeryTypeDTOS);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        try{
            surgeryTypeService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException err){
            ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.notFound(err.getMessage());
            return ResponseEntity.status(errorResponseDTO.status()).body(errorResponseDTO);
        }
    }
}
