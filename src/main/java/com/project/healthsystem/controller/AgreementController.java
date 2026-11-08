package com.project.healthsystem.controller;

import com.project.healthsystem.controller.docs.AgreementApiDocs;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.common.Permissions;
import com.project.healthsystem.controller.dto.basic_requests.AgentRequestDTO;
import com.project.healthsystem.controller.dto.basic_requests.AgreementRequestDTO;
import com.project.healthsystem.controller.dto.basic_responses.AgreementResponseDTO;
import com.project.healthsystem.controller.dto.simplified_info.AgentSimplifiedResponseDTO;
import com.project.healthsystem.service.AgreementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/agreements")
@RequiredArgsConstructor

public class AgreementController implements AgreementApiDocs {

    private final AgreementService agreementService;

    @PostMapping
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER)
    
    public ResponseEntity<AgreementResponseDTO> save(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody @Valid AgreementRequestDTO agreementRequestDTO
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        AgreementResponseDTO agreementResponseDTO = agreementService.save(agreementRequestDTO, accessToken);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(agreementResponseDTO.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    
    public ResponseEntity<Object> read(@PathVariable("id") long id){
        return ResponseEntity.ok(agreementService.getById(id));
    }

    @GetMapping
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    
    public ResponseEntity<Page<AgreementResponseDTO>> readAll(
            @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "page-length", defaultValue = "20") Integer pageLength,
            @RequestParam(value = "name", required = false) String name
    ){
        return ResponseEntity.ok(agreementService.getAll(
            pageNumber,
            pageLength,
            name
        ));
    }

    @PutMapping("/{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER)
    
    public ResponseEntity<Object> update(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("id") long id,
            @RequestBody @Valid AgreementRequestDTO agreementRequestDTO
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        agreementService.update(agreementRequestDTO, id, accessToken);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize(Permissions.ADMIN_OR_MANAGER)
    @DeleteMapping("/{id}")
    
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        agreementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
