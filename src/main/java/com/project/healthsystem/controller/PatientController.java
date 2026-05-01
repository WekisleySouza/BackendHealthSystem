package com.project.healthsystem.controller;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.common.Permissions;
import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.controller.dto.PatientInfoResponseDTO;
import com.project.healthsystem.controller.dto.basic_requests.PatientRequestDTO;
import com.project.healthsystem.controller.dto.basic_responses.PatientResponseDTO;
import com.project.healthsystem.controller.dto.simplified_info.PatientSimplifiedInfoDTO;
import com.project.healthsystem.controller.dto.simplified_info.PatientSimplifiedResponseDTO;
import com.project.healthsystem.model.Patient;
import com.project.healthsystem.service.PatientService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Operations related to patients management.")
public class PatientController {
    private final PatientService patientService;

    @PostMapping
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    @Operation(
            summary = "Create patient",
            description = "Create a new patient.",
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
                    description = "Related resource not found (responsible, etc.).",
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
            @RequestBody @Valid PatientRequestDTO person
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        Patient patientEntity = patientService.save(person, accessToken);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(patientEntity.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    @Operation(
            summary = "Update patient",
            description = "Update an existing patient.",
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
                    description = "Patient not found.",
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
            @RequestBody @Valid PatientRequestDTO patientRequestDTO
    ){
        String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
        patientService.update(patientRequestDTO, id, accessToken);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    @Operation(
            summary = "Find by id",
            description = "Find a patient by id."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient found successfully.",
                    content = @Content(schema = @Schema(implementation = PatientResponseDTO.class))
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
                    description = "Patient not found.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<PatientResponseDTO> read(@PathVariable("id") long id){
        PatientResponseDTO patientResponseDTO = patientService.findById(id);
        return ResponseEntity.ok(patientResponseDTO);
    }

    @GetMapping("/info-patient/{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    @Operation(
            summary = "Get patient info",
            description = "Get patient detailed info by id."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Patient info retrieved successfully.",
                    content = @Content(schema = @Schema(implementation = PatientInfoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<PatientInfoResponseDTO> getPatientInfo(@PathVariable("id") long id){
        PatientInfoResponseDTO patientInfoDTO = patientService.getPatientInfo(id);
        return ResponseEntity.ok(patientInfoDTO);
    }

    @GetMapping("/simplified-list")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE_OR_PATIENT)
    @Operation(
            summary = "Get patients simplified list",
            description = "Get patients list containing only id, name, cpf, and mother name."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Patients simplified list retrieved successfully.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PatientSimplifiedInfoDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<List<PatientSimplifiedInfoDTO>> getPatientInfo(){
        List<PatientSimplifiedInfoDTO> patientSimplifiedInfoDTOS = patientService.getSimplifiedPatients();
        return ResponseEntity.ok(patientSimplifiedInfoDTOS);
    }

    @GetMapping("/get-all-simplified")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    @Operation(
            summary = "Get all patients simplified",
            description = "Get paginated patients with simplified information."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Patients page (simplified) retrieved successfully.",
                    content = @Content(schema = @Schema(implementation = PatientSimplifiedResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<Page<PatientSimplifiedResponseDTO>> readAllSimplified(
            @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength
    ) {
        return ResponseEntity.ok(patientService.getAllSimplified(
                pageNumber,
                pageLength
        ));
    }

    @GetMapping
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    @Operation(
            summary = "Get all patients",
            description = "Get paginated patients with optional filters."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Patients page retrieved successfully.",
                    content = @Content(schema = @Schema(implementation = PatientResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<Page<PatientResponseDTO>> readAll(
            @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength,

            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "cpf", required = false) String cpf,
            @RequestParam(value = "birthday", required = false) LocalDate birthday,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "cns", required = false) String cns,
            @RequestParam(value = "mother-name", required = false) String motherName,

            @RequestParam(value = "team-name", required = false) String teamName,
            @RequestParam(value = "team-ine", required = false) String teamINE,
            @RequestParam(value = "micro-area", required = false) String microArea,
            @RequestParam(value = "origin", required = false) String origin,

            @RequestParam(value = "sex", required = false) String sex,
            @RequestParam(value = "cell-phone", required = false) String cellPhone,
            @RequestParam(value = "residential-phone", required = false) String residentialPhone,
            @RequestParam(value = "contact-phone", required = false) String contactPhone
    ) {

        return ResponseEntity.ok(patientService.getAll(
                pageNumber,
                pageLength,
                name,
                gender,
                cpf,
                birthday,
                email,
                cns,
                motherName,

                teamName,
                teamINE,
                microArea,
                origin,
                sex,
                cellPhone,
                residentialPhone,
                contactPhone
        ));
    }

    @DeleteMapping("{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    @Operation(
            summary = "Delete patient",
            description = "Delete a patient by id."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted successfully."),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Patient not found.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - integrity violation (patient in use, for example).",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        patientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER)
    @Operation(
            summary = "Import patients",
            description = "Import patients from a CSV file (multipart/form-data).",
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer access token",
                            required = true
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Imported successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid file or request.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - integrity violation during import.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<String> importExcel(
            @Parameter(
                    description = "CSV file",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam("file") MultipartFile file
    ) {
        patientService.importCsv(file);
        return ResponseEntity.ok("Importado com sucesso");
    }

    @PostMapping(value = "/import-esus", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER)
    @Operation(
            summary = "Import patients",
            description = "Import patients from a CSV file (multipart/form-data).",
            parameters = {
                    @Parameter(
                            name = "Authorization",
                            description = "Bearer access token",
                            required = true
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Imported successfully."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid file or request.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - integrity violation during import.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected server error.",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))
            )
    })
    public ResponseEntity<String> importExcelESUS(
            @Parameter(
                    description = "CSV file",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam("file") MultipartFile file
    ) {
        patientService.importESUSCsv(file);
        return ResponseEntity.ok("Importado ESUS com sucesso");
    }
}
