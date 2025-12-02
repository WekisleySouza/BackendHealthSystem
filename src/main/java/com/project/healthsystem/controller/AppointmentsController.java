package com.project.healthsystem.controller;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.dto.AppointmentRequestDTO;
import com.project.healthsystem.model.Appointment;
import com.project.healthsystem.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentsController {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Void> save(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody @Valid AppointmentRequestDTO appointmentRequestDTO
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        Appointment appointment = appointmentService.save(appointmentRequestDTO, accessToken);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(appointment.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Object> update(
        @RequestHeader("Authorization") String authHeader,
        @PathVariable("id") long id,
        @RequestBody @Valid AppointmentRequestDTO appointmentRequestDTO
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        appointmentService.update(appointmentRequestDTO, id, accessToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Page<AppointmentRequestDTO>> readAll(
        @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
        @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength,
        @RequestParam(value = "notes", required = false) String notes,
        @RequestParam(value = "scheduledAt", required = false) LocalDateTime scheduledAt,
        @RequestParam(value = "createdAt", required = false) LocalDateTime createdAt,
        @RequestParam(value = "priorit", required = false) String priorit,
        @RequestParam(value = "status", required = false) String status
    ){
        Page<AppointmentRequestDTO> appointmentRequestDTOS = appointmentService.getAll(
            pageNumber,
            pageLength,
            notes,
            scheduledAt,
            createdAt,
            priorit,
            status
        );
        return ResponseEntity.ok(appointmentRequestDTOS);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        appointmentService.delete(id);
        return ResponseEntity.notFound().build();
    }
}
