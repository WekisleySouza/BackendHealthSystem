package com.project.healthsystem.controller;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.common.Permissions;
import com.project.healthsystem.controller.dto.ProfessionalRequestDTO;
import com.project.healthsystem.controller.dto.ProfessionalResponseDTO;
import com.project.healthsystem.model.Professional;
import com.project.healthsystem.service.ProfessionalService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/professionals")
@RequiredArgsConstructor
@Tag(name = "Professional")
public class ProfessionalController {
    private final ProfessionalService professionalService;

    @PostMapping
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER)
    public ResponseEntity<Void> save(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody @Valid ProfessionalRequestDTO professionalRequestDto
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        Professional professionalEntity = professionalService.save(professionalRequestDto, accessToken);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(professionalEntity.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER)
    public ResponseEntity<Object> update(
        @RequestHeader("Authorization") String authHeader,
        @PathVariable("id") long id,
        @RequestBody @Valid ProfessionalRequestDTO professionalRequestDto
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        professionalService.update(professionalRequestDto, id, accessToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    public ResponseEntity<Object> read(@PathVariable("id") long id){
        return ResponseEntity.ok(professionalService.findById(id));
    }

    @GetMapping
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    public ResponseEntity<Page<ProfessionalResponseDTO>> readAll(
        @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
        @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength,
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "cpf", required = false) String cpf,
        @RequestParam(value = "phone", required = false) String phone,
        @RequestParam(value = "birthday", required = false) LocalDate birthday,
        @RequestParam(value = "email", required = false) String email

    ){
        return ResponseEntity.ok(professionalService.getAll(
            pageNumber,
            pageLength,
            name,
            cpf,
            phone,
            birthday,
            email
        ));
    }

    @DeleteMapping("{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER)
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        professionalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
