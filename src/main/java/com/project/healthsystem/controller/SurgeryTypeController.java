package com.project.healthsystem.controller;

import com.project.healthsystem.controller.dto.SurgeryTypeRequestDTO;
import com.project.healthsystem.model.SurgeryType;
import com.project.healthsystem.service.SurgeryTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/surgery-type")
@RequiredArgsConstructor
public class SurgeryTypeController {
    private final SurgeryTypeService surgeryTypeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> save(@RequestBody @Valid SurgeryTypeRequestDTO surgeryTypeRequestDTO){
        SurgeryType surgeryTypeEntity = surgeryTypeService.save(surgeryTypeRequestDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(surgeryTypeEntity.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Object> update(@PathVariable("id") long id, @RequestBody @Valid SurgeryTypeRequestDTO surgeryTypeRequestDTO){
        surgeryTypeService.update(surgeryTypeRequestDTO, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Page<SurgeryTypeRequestDTO>> readAll(
        @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
        @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength
    ){
        return ResponseEntity.ok(surgeryTypeService.getAll(pageNumber, pageLength));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        surgeryTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
