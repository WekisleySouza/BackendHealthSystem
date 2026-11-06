package com.project.healthsystem.controller;

import com.project.healthsystem.controller.common.Permissions;
import com.project.healthsystem.service.datasus.CnesService;
import com.project.healthsystem.service.datasus.SigtapService;
import com.project.healthsystem.service.datasus.SigtapProcedureService;
import com.project.healthsystem.controller.dto.basic_responses.SigtapProcedureResponseDTO;
import org.springframework.web.bind.annotation.PathVariable;
import com.project.healthsystem.controller.dto.basic_responses.SigtapImportResponseDTO;
import java.io.IOException;
import com.project.healthsystem.controller.dto.basic_responses.CnesImportResponseDTO;
import com.project.healthsystem.service.datasus.Cid10Service;
import com.project.healthsystem.service.datasus.Ciap2Service;
import com.project.healthsystem.controller.dto.basic_responses.Cid10ResponseDTO;
import com.project.healthsystem.controller.dto.basic_responses.Ciap2ResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/datasus")
@RequiredArgsConstructor
@Tag(name = "DATASUS Endpoints", description = "Operations related to DATASUS syncing.")
public class DataSusController {
  private final CnesService cnesService;
  private final Cid10Service cid10Service;
  private final Ciap2Service ciap2Service;
  private final SigtapService sigtapService;
  private final SigtapProcedureService sigtapProcedureService;

  @GetMapping("/sigtap/procedures")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
  @Operation(summary = "Search SIGTAP procedures",
    description = "Partial code or name search, ignoring case. Ordered by code. "
      + "page-number starts at 0; page-length is 1 to 100 (default 10). Invalid pagination returns 400.")
  public ResponseEntity<Page<SigtapProcedureResponseDTO>> findSigtapProcedures(
      @RequestParam(value = "page-number", defaultValue = "0") int pageNumber,
      @RequestParam(value = "page-length", defaultValue = "10") int pageLength,
      @RequestParam(value = "search", required = false) String search
  ) {
    return ResponseEntity.ok(sigtapProcedureService.findAll(pageNumber, pageLength, search));
  }

  @GetMapping("/sigtap/procedures/{id}")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
  @Operation(summary = "Find SIGTAP procedure",
    description = "Returns the imported procedure and its hierarchy. Returns 404 when the ID does not exist.")
  public ResponseEntity<SigtapProcedureResponseDTO> findSigtapProcedure(@PathVariable("id") long id) {
    return ResponseEntity.ok(sigtapProcedureService.findById(id));
  }

  // Temporary endpoint for validating the SIGTAP import.
  @PostMapping("/import-sigtap")
  @PreAuthorize(Permissions.ADMIN)
  @Operation(summary = "Force SIGTAP import (temporary test endpoint)",
    description = "Downloads and imports the latest SIGTAP ZIP, even if already imported. "
      + "Returns only after commit, with the file, competence and imported counts. Administrator only.")
  public ResponseEntity<SigtapImportResponseDTO>
      importSigtap() throws IOException {
    return ResponseEntity.ok(sigtapService.importSigtap());
  }

  @GetMapping("/cid10")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
  @Operation(summary = "Search CID-10 catalog",
    description = "Search by partial code or description, ignoring case. Ordered by code. "
      + "page-number starts at 0; page-length must be between 1 and 100. "
      + "Includes inactive PEC entries. Invalid pagination returns 400.")
  public ResponseEntity<Page<Cid10ResponseDTO>> findCid10(
      @RequestParam(value = "page-number", defaultValue = "0") int pageNumber,
      @RequestParam(value = "page-length", defaultValue = "10") int pageLength,
      @RequestParam(value = "search", required = false) String search
  ) {
    return ResponseEntity.ok(cid10Service.findAll(pageNumber, pageLength, search));
  }

  @GetMapping("/ciap2")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
  @Operation(summary = "Search CIAP-2 catalog",
    description = "Search by partial code or description, ignoring case. Ordered by code. "
      + "page-number starts at 0; page-length must be between 1 and 100. "
      + "Includes all imported codes, without asserting RNDS eligibility. Invalid pagination returns 400.")
  public ResponseEntity<Page<Ciap2ResponseDTO>> findCiap2(
      @RequestParam(value = "page-number", defaultValue = "0") int pageNumber,
      @RequestParam(value = "page-length", defaultValue = "10") int pageLength,
      @RequestParam(value = "search", required = false) String search
  ) {
    return ResponseEntity.ok(ciap2Service.findAll(pageNumber, pageLength, search));
  }
  
  @PostMapping(value = "/import-cnes-estabilishments", consumes = "multipart/form-data")
  @PreAuthorize(Permissions.ADMIN)
  @Operation(
    summary = "Import estabilishments",
    description = "Replace the CNES establishment catalog using a UTF-8 CSV with the expected header and semicolon delimiter. Invalid files return 400 without replacing data. Returns importedRecords after commit.",
    parameters = {
      @Parameter(
        name = "Authorization",
        description = "Bearer access token",
        required = true
      )
    }
  )
  public ResponseEntity<CnesImportResponseDTO> importCnesEstabilishments(@RequestParam("file") MultipartFile file) {
    return ResponseEntity.ok(this.cnesService.importDataEstabilishments(file));
  }
  
  @PostMapping(value = "/import-cnes-professionals", consumes = "multipart/form-data")
  @PreAuthorize(Permissions.ADMIN)
  @Operation(
    summary = "Import professionals",
    description = "Replace CNES professional bonds using a UTF-8 CSV with the expected header and semicolon delimiter. Alphanumeric CBO and multiple CNS bonds are preserved. Invalid files return 400 without replacing data. Returns importedRecords after commit.",
    parameters = {
      @Parameter(
        name = "Authorization",
        description = "Bearer access token",
        required = true
      )
    }
  )
  public ResponseEntity<CnesImportResponseDTO> importCnesProfessionals(@RequestParam("file") MultipartFile file) {
    return ResponseEntity.ok(this.cnesService.importDataProfessionals(file));
  }
}
