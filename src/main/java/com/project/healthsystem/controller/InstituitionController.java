package com.project.healthsystem.controller;

import com.project.healthsystem.controller.docs.InstituitionApiDocs;

import com.project.healthsystem.controller.common.Permissions;
import com.project.healthsystem.controller.dto.basic_requests.InstituitionRequestDTO;
import com.project.healthsystem.controller.dto.basic_responses.InstituitionResponseDTO;
import com.project.healthsystem.service.InstituitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/instituition")
@RequiredArgsConstructor

public class InstituitionController implements InstituitionApiDocs {
    private final InstituitionService instituitionService;

    @PostMapping
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    public ResponseEntity<InstituitionResponseDTO> save(@RequestBody InstituitionRequestDTO instituitionRequestDTO){
        InstituitionResponseDTO instituitionResponseDTO = this.instituitionService.save(instituitionRequestDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(instituitionResponseDTO.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    public ResponseEntity<Object> update(
        @PathVariable("id") long id,
        @RequestBody InstituitionRequestDTO instituitionRequestDTO
    ){
        this.instituitionService.update(id, instituitionRequestDTO);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    public ResponseEntity<InstituitionResponseDTO> findById(
        @PathVariable("id") long id
    ){
        return ResponseEntity.ok(
            this.instituitionService.findById(id)
        );
    }

    @GetMapping
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    public ResponseEntity<Page<InstituitionResponseDTO>> findAll(
        @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
        @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength,
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "cep", required = false) String cep,
        @RequestParam(value = "cityName", required = false) String cityName,
        @RequestParam(value = "address", required = false) String address,
        @RequestParam(value = "phone", required = false) String phone
    ){
        return ResponseEntity.ok(
            this.instituitionService.findAll(
                pageNumber,
                pageLength,
                name,
                cep,
                cityName,
                address,
                phone
            )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER)
    public ResponseEntity<InstituitionResponseDTO> delete(
            @PathVariable("id") long id
    ){
        instituitionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
