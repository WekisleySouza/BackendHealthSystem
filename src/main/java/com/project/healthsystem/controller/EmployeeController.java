package com.project.healthsystem.controller;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.common.Permissions;
import com.project.healthsystem.controller.dto.basic_requests.EmployeeRequestDTO;
import com.project.healthsystem.controller.dto.basic_responses.EmployeeResponseDTO;
import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.controller.dto.simplified_info.EmployeeSimplifiedResponseDTO;
import com.project.healthsystem.model.Employee;
import com.project.healthsystem.service.EmployeeService;
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
import java.time.LocalDate;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@Tag(name = "Employees", description = "Operations related to employees management.")
public class EmployeeController {
    private final EmployeeService service;

    @PostMapping
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER)
    @Operation(
            summary = "Create employee",
            description = "Create a new employee.",
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
                    description = "Unexpected server error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<Void> save(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid EmployeeRequestDTO employeeRequestDTO
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        Employee employeeEntity = service.save(employeeRequestDTO, accessToken);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(employeeEntity.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER)
    @Operation(
            summary = "Update employee",
            description = "Update an existing employee.",
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
                    description = "Employee not found.",
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
                    description = "Unexpected server error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<Object> update(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("id") long id,
            @RequestBody @Valid EmployeeRequestDTO employeeRequestDTO
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        service.update(employeeRequestDTO, id, accessToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    @Operation(
            summary = "Find by id",
            description = "Find an employee by id."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Employee found successfully.",
                    content = @Content(schema = @Schema(implementation = EmployeeResponseDTO.class))
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
                    description = "Employee not found.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<Object> read(@PathVariable("id") long id){
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/get-all-simplified")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    @Operation(
            summary = "Get all simplified",
            description = "Get paginated employees with simplified information."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Employees page (simplified) retrieved successfully.",
                    content = @Content(schema = @Schema(implementation = EmployeeSimplifiedResponseDTO.class))
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
                    description = "Unexpected server error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<Page<EmployeeSimplifiedResponseDTO>> readAllSimplified(
            @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "page-length", defaultValue = "20") Integer pageLength
    ){
        Page<EmployeeSimplifiedResponseDTO> employeeRequestDTOS = service.getAllSimplified(
                pageNumber,
                pageLength
        );
        return ResponseEntity.ok(employeeRequestDTOS);
    }

    @GetMapping
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    @Operation(
            summary = "Get all",
            description = "Get paginated employees with optional filters."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Employees page retrieved successfully.",
                    content = @Content(schema = @Schema(implementation = EmployeeResponseDTO.class))
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
                    description = "Unexpected server error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<Page<EmployeeResponseDTO>> readAll(
            @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "cpf", required = false) String cpf,
            @RequestParam(value = "birthday", required = false) LocalDate birthday,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "sex", required = false) String sex,
            @RequestParam(value = "cell-phone", required = false) String cellPhone,
            @RequestParam(value = "residential-phone", required = false) String residentialPhone,
            @RequestParam(value = "contact-phone", required = false) String contactPhone
    ){
        Page<EmployeeResponseDTO> employeeRequestDTOS = service.getAll(
                pageNumber,
                pageLength,
                name,
                gender,
                cpf,
                birthday,
                email,
                sex,
                cellPhone,
                residentialPhone,
                contactPhone
        );
        return ResponseEntity.ok(employeeRequestDTOS);
    }

    @DeleteMapping("{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER)
    @Operation(
            summary = "Delete",
            description = "Delete an employee by id."
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
                    description = "Employee not found.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - integrity violation (employee in use, for example).",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
