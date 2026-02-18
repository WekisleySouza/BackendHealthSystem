package com.project.healthsystem.controller;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.common.Permissions;
import com.project.healthsystem.controller.dto.PatientInfoResponseDTO;
import com.project.healthsystem.controller.dto.PatientRequestDTO;
import com.project.healthsystem.controller.dto.PatientResponseDTO;
import com.project.healthsystem.controller.dto.PatientSimplifiedInfoDTO;
import com.project.healthsystem.model.Patient;
import com.project.healthsystem.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
@Tag(name = "Patient")
public class PatientController {
    private final PatientService patientService;

    @PostMapping
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
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
    public ResponseEntity<PatientResponseDTO> read(@PathVariable("id") long id){
        PatientResponseDTO patientResponseDTO = patientService.findById(id);
        return ResponseEntity.ok(patientResponseDTO);
    }

    @GetMapping("/info-patient/{id}")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
    @Operation(summary = "Get patient info", description = "Get patient info by id.")
    public ResponseEntity<PatientInfoResponseDTO> getPatientInfo(@PathVariable("id") long id){
        PatientInfoResponseDTO patientInfoDTO = patientService.getPatientInfo(id);
        return ResponseEntity.ok(patientInfoDTO);
    }

    @GetMapping("/simplified-list")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE_OR_PATIENT)
    @Operation(summary = "Get patient simplified info", description = "Get patients just with id, name, cpf and mother name.")
    public ResponseEntity<List<PatientSimplifiedInfoDTO>> getPatientInfo(){
        List<PatientSimplifiedInfoDTO> patientSimplifiedInfoDTOS = patientService.getSimplifiedPatients();
        return ResponseEntity.ok(patientSimplifiedInfoDTOS);
    }

    @GetMapping
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
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

            // 🔹 NOVOS - Person
            @RequestParam(value = "team-name", required = false) String teamName,
            @RequestParam(value = "team-ine", required = false) String teamINE,
            @RequestParam(value = "micro-area", required = false) String microArea,
            @RequestParam(value = "origin", required = false) String origin,

            // 🔹 NOVOS - Base
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

                // novos
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
    public ResponseEntity<Object> delete(@PathVariable("id") long id){
        patientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    @PreAuthorize(Permissions.ADMIN_OR_MANAGER)
    public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file) {
        patientService.importCsv(file);
        return ResponseEntity.ok("Importado com sucesso");
    }

}
