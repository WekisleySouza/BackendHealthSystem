package com.project.healthsystem.controller;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.common.Permissions;
import com.project.healthsystem.controller.dto.SurgeryRequestDTO;
import com.project.healthsystem.controller.dto.SurgeryResponseDTO;
import com.project.healthsystem.model.Surgery;
import com.project.healthsystem.service.SurgeryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/surgeries")
@RequiredArgsConstructor
public class SurgeryController {

    private final SurgeryService surgeryService;

    @PostMapping
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    public ResponseEntity<Void> save(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody @Valid SurgeryRequestDTO surgeryRequestDTO
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        Surgery surgery = surgeryService.save(surgeryRequestDTO, accessToken);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(surgery.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    public ResponseEntity<Object> update(
        @RequestHeader("Authorization") String authHeader,
        @PathVariable("id") long id,
        @RequestBody @Valid SurgeryRequestDTO surgeryRequestDTO
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        surgeryService.update(surgeryRequestDTO, id, accessToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    public ResponseEntity<Object> read(@PathVariable("id") long id){
        return ResponseEntity.ok(surgeryService.findById(id));
    }

    @GetMapping
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    public ResponseEntity<Page<SurgeryResponseDTO>> readAll(
        @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
        @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength,

        @RequestParam(value = "date-time", required = false) LocalDateTime dateTime,
        @RequestParam(value = "person-name", required = false) String personName,
        @RequestParam(value = "surgery-risk", required = false) String surgeryRisk,
        @RequestParam(value = "location", required = false) String location,
        @RequestParam(value = "conclusion", required = false) String conclusion,
        @RequestParam(value = "sus-easy", required = false) String susEasy,
        @RequestParam(value = "sesap", required = false) String sesap,
        @RequestParam(value = "procedure-date", required = false) LocalDate procedureDate,
        @RequestParam(value = "anesthesic-risk", required = false) String anesthesicRisk,
        @RequestParam(value = "observation", required = false) String observation,
        @RequestParam(value = "surgery-type-id", required = false) Long surgeryTypeId
    ){
        return ResponseEntity.ok(
            surgeryService.getAll(
                pageNumber,
                pageLength,
                dateTime,
                personName,
                surgeryRisk,
                location,
                conclusion,
                susEasy,
                sesap,
                procedureDate,
                anesthesicRisk,
                observation,
                surgeryTypeId
        )
        );
    }

    @DeleteMapping("{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        surgeryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
