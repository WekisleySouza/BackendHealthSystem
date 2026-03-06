package com.project.healthsystem.controller;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.common.Permissions;
import com.project.healthsystem.controller.dto.*;
import com.project.healthsystem.controller.dto.basic_requests.AppointmentRequestDTO;
import com.project.healthsystem.controller.dto.appointment_get_by_id.AppointmentGetByIdResponseDTO;
import com.project.healthsystem.controller.dto.reports_professional.NumberAppointmentsByStatusAndProfessionalDTO;
import com.project.healthsystem.controller.dto.reports_specialties.NumberExamsByStatusDTO;
import com.project.healthsystem.controller.dto.reports_specialties.NumberSpecialtiesByStatusDTO;
import com.project.healthsystem.model.Appointment;
import com.project.healthsystem.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Tag(name = "Appointments", description = "Operations related to appointments management.")
public class AppointmentsController {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    @Operation(
            summary = "Create appointment",
            description = "Create a new appointment.",
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer access token",
                            required = true
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created successfully."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data sent in the request.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing credentials.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - user does not have permission.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Related resource not found (patient, professional, etc.).",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - duplicated register or integrity violation.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable Entity - validation errors on fields.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ErrorResponseDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
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
    @Operation(
            summary = "Update appointment",
            description = "Update an existing appointment.",
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer access token",
                            required = true
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Updated successfully."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data sent in the request.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing credentials.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - user does not have permission.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - integrity violation.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable Entity - validation errors on fields.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
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
    @Operation(
            summary = "Find by id",
            description = "Find appointment by id."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointment found successfully.",
                    content = @Content(schema = @Schema(implementation = AppointmentGetByIdResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing credentials.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - user does not have permission.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    public ResponseEntity<AppointmentGetByIdResponseDTO> read(@PathVariable("id") long id){
        return ResponseEntity.ok(appointmentService.findById(id));
    }

    @GetMapping
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    @Operation(
            summary = "Get all",
            description = "Get all appointments with filters and pagination."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Appointments page retrieved successfully.",
                    content = @Content(schema = @Schema(implementation = AppointmentResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing credentials.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - user does not have permission.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
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
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "is-sorted-by-name", required = false) boolean isSortedByName,
            @RequestParam(value = "is-descending", required = false) boolean isDescending
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
                patient,
                isSortedByName,
                isDescending
        );
        return ResponseEntity.ok(AppointmentResponseDTOs);
    }

    @GetMapping("/patients-report")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    @Operation(
            summary = "Get Report",
            description = "Get paginated appointment reports by patient."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Report retrieved successfully.",
                    content = @Content(schema = @Schema(implementation = ReportAppointmentByPatientResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing credentials.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - user does not have permission.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<ReportAppointmentByPatientResponseDTO> patientsReport(
            @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength
    ){
        ReportAppointmentByPatientResponseDTO reportAppointmentByPatientResponseDTOs = appointmentService.getPatientReport(
                pageNumber,
                pageLength
        );
        return ResponseEntity.ok(reportAppointmentByPatientResponseDTOs);
    }

    @GetMapping("/number-appointments-by-professional")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    public ResponseEntity<Page<NumberAppointmentsByStatusAndProfessionalDTO>> countAppointmentsByProfessional(
            @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength
    ){
        Page<NumberAppointmentsByStatusAndProfessionalDTO> reportAppointmentByPatientResponseDTOs = appointmentService.countAppointmentsByProfessional(
                pageNumber,
                pageLength
        );
        return ResponseEntity.ok(reportAppointmentByPatientResponseDTOs);
    }

    @GetMapping("/number-specialties-by-service-type")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    public ResponseEntity<Page<NumberSpecialtiesByStatusDTO>> countSpecialtiesByServiceType(
            @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength
    ){
        Page<NumberSpecialtiesByStatusDTO> reportAppointmentByPatientResponseDTOs = appointmentService.countSpecialtiesByStatus(
                pageNumber,
                pageLength
        );
        return ResponseEntity.ok(reportAppointmentByPatientResponseDTOs);
    }

    @GetMapping("/number-exams-by-service-type")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    public ResponseEntity<Page<NumberExamsByStatusDTO>> countExamsByServiceType(
        @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
        @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength
    ){
        Page<NumberExamsByStatusDTO> reportAppointmentByPatientResponseDTOs = appointmentService.countExamsByStatus(
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
    @Operation(
            summary = "Delete",
            description = "Delete an appointment by id."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted successfully."),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - invalid or missing credentials.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - user does not have permission.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - integrity violation.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        appointmentService.delete(id);
        return ResponseEntity.notFound().build();
    }
}
