package com.project.healthsystem.controller;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.common.Permissions;
import com.project.healthsystem.controller.dto.ReportAppointmentByPatientResponseDTO;
import com.project.healthsystem.controller.dto.AppointmentRequestDTO;
import com.project.healthsystem.controller.dto.AppointmentResponseDTO;
import com.project.healthsystem.model.Appointment;
import com.project.healthsystem.model.ServiceTypes;
import com.project.healthsystem.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Appointment")
public class AppointmentsController {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    @Operation(summary = "Create appointment", description = "Create a new appointment.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Created successfully.")
    })
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
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    @Operation(summary = "Update appointment", description = "Update appointment.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Updated successfully.")
    })
    public ResponseEntity<Object> update(
        @RequestHeader("Authorization") String authHeader,
        @PathVariable("id") long id,
        @RequestBody @Valid AppointmentRequestDTO appointmentRequestDTO
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        appointmentService.update(appointmentRequestDTO, id, accessToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find by id", description = "Find appointment by id.")
    @ApiResponses({
        @ApiResponse(responseCode = "404", description = "Appointment not found.")
    })
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    public ResponseEntity<Object> read(@PathVariable("id") long id){
        return ResponseEntity.ok(appointmentService.findById(id));
    }

    @GetMapping
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    @Operation(summary = "Get all", description = "Get all appointments.")
    public ResponseEntity<Page<AppointmentResponseDTO>> readAll(
        @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
        @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength,
        @RequestParam(value = "notes", required = false) String notes,
        @RequestParam(value = "scheduledAt", required = false) LocalDateTime scheduledAt,
        @RequestParam(value = "createdAt", required = false) LocalDateTime createdAt,
        @RequestParam(value = "priorit", required = false) String priorit,
        @RequestParam(value = "status", required = false) String status,
        @RequestParam(value = "professional-name", required = false) String professional,
        @RequestParam(value = "employee-name", required = false) String employee,
        @RequestParam(value = "patient-name", required = false) String patient,
        @RequestParam(value = "type", required = false) String type
    ){
        Page<AppointmentResponseDTO> AppointmentResponseDTOs = appointmentService.getAll(
            type,
            pageNumber,
            pageLength,
            notes,
            scheduledAt,
            createdAt,
            priorit,
            status,
            professional,
            employee,
            patient
        );
        return ResponseEntity.ok(AppointmentResponseDTOs);
    }

    @GetMapping("/patients-report")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    @Operation(summary = "Get Report", description = "Get appointment reports.")
    public ResponseEntity<ReportAppointmentByPatientResponseDTO> readAll(
            @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength
    ){
        ReportAppointmentByPatientResponseDTO reportAppointmentByPatientResponseDTOs = appointmentService.getPatientReport(
            pageNumber,
            pageLength
        );
        return ResponseEntity.ok(reportAppointmentByPatientResponseDTOs);
    }

//    @GetMapping("/test")
//    @PreAuthorize(Permissions.PERMIT_ALL)
//    @Operation(summary = "Teste", description = "Just to test.")
//    public ResponseEntity<Page<TestDTO>> test(
//            @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
//            @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength
//    ){
//        Page<TestDTO> appointment = appointmentService.test(
//            pageNumber,
//            pageLength
//        );
//        return ResponseEntity.ok(appointment);
//    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete", description = "Delete an appointment by id.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Deleted successfully."),
        @ApiResponse(responseCode = "404", description = "Appointment not found.")
    })
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        appointmentService.delete(id);
        return ResponseEntity.notFound().build();
    }
}
