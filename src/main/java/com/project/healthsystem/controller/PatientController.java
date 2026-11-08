package com.project.healthsystem.controller;

import com.project.healthsystem.controller.docs.PatientApiDocs;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.common.Permissions;
import com.project.healthsystem.controller.dto.ErrorResponseDTO;
import com.project.healthsystem.controller.dto.PatientInfoResponseDTO;
import com.project.healthsystem.controller.dto.basic_requests.ConditionRequestDTO;
import com.project.healthsystem.controller.dto.basic_requests.PatientCPFRequestDTO;
import com.project.healthsystem.controller.dto.basic_requests.PatientRequestDTO;
import com.project.healthsystem.controller.dto.basic_requests.PersonBackupInfoDTO;
import com.project.healthsystem.controller.dto.basic_responses.PatientResponseDTO;
import com.project.healthsystem.controller.dto.patient_page_responses.PatientAppointmentResponseDTO;
import com.project.healthsystem.controller.dto.pec_sync.PersonsInfoList;
import com.project.healthsystem.controller.dto.simplified_info.PatientSimplifiedInfoDTO;
import com.project.healthsystem.controller.dto.simplified_info.PatientSimplifiedResponseDTO;
import com.project.healthsystem.model.Patient;
import com.project.healthsystem.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
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

public class PatientController implements PatientApiDocs {
  private final PatientService patientService;
  
  @PostMapping
  @PreAuthorize(Permissions.ADMIN)

  public ResponseEntity<Void> save(
    @RequestHeader("Authorization") String authHeader,
    @RequestBody @Valid PatientRequestDTO person
  ) {
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
  @PreAuthorize(Permissions.ADMIN)

  public ResponseEntity<Object> update(
    @RequestHeader("Authorization") String authHeader,
    @PathVariable("id") long id,
    @RequestBody @Valid PatientRequestDTO patientRequestDTO
  ) {
    String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
    patientService.update(patientRequestDTO, id, accessToken);
    return ResponseEntity.noContent().build();
  }
  
  @GetMapping("{id}")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)

  public ResponseEntity<PatientResponseDTO> read(@PathVariable("id") long id) {
    PatientResponseDTO patientResponseDTO = patientService.findById(id);
    return ResponseEntity.ok(patientResponseDTO);
  }
  
  @GetMapping("/info-patient/{id}")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)

  public ResponseEntity<PatientInfoResponseDTO> getPatientInfoById(@PathVariable("id") long id) {
    PatientInfoResponseDTO patientInfoDTO = patientService.getPatientInfo(id);
    return ResponseEntity.ok(patientInfoDTO);
  }
  
  @GetMapping("/simplified-list")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)

  public ResponseEntity<List<PatientSimplifiedInfoDTO>> getAllPatientInfo() {
    List<PatientSimplifiedInfoDTO> patientSimplifiedInfoDTOS = patientService.getSimplifiedPatients();
    return ResponseEntity.ok(patientSimplifiedInfoDTOS);
  }
  
  @GetMapping("/get-all-simplified")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)

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
  @PreAuthorize(Permissions.ADMIN)

  public ResponseEntity<Object> delete(@PathVariable("id") long id) {
    patientService.delete(id);
    return ResponseEntity.noContent().build();
  }
  
  @PatchMapping("/update-login")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
  public ResponseEntity<Object> updatePatientCPF(
    @RequestHeader("Authorization") String authHeader,
    @RequestBody @Valid PatientCPFRequestDTO patientCPFRequestDTO
  ) {
    String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
    this.patientService.updateCPF(patientCPFRequestDTO, accessToken);
    return ResponseEntity.ok().build();
  }
  @PostMapping("/sync-patients")
  @PreAuthorize("hasRole('API')")

  public ResponseEntity<Void> syncPatients(@RequestBody @Valid List<PersonBackupInfoDTO> patients) {
    patientService.syncExternalDataBase(patients);
    return ResponseEntity.ok().build();
  }
}
