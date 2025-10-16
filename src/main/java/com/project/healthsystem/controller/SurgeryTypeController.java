package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.SurgeryTypeDTO;
import com.project.healthsystem.controller.mappers.SurgeryTypeMapper;
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
    private final SurgeryTypeMapper surgeryTypeMapper;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody SurgeryTypeDTO surgeryTypeDTO){
        SurgeryType surgeryTypeEntity = surgeryTypeMapper.toEntity(surgeryTypeDTO);
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
        surgeryTypeService.update(surgeryTypeDTO, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SurgeryTypeDTO>> readAll(){
        List<SurgeryTypeDTO> surgeryTypeDTOS = surgeryTypeService.getAll();
        return ResponseEntity.ok(surgeryTypeDTOS);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        surgeryTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
