package com.project.healthsystem.controller;

import com.project.healthsystem.controller.common.Permissions;
import com.project.healthsystem.service.datasus.CnesService;
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
  
  @PostMapping("/import-cnes-estabilishments")
  @PreAuthorize(Permissions.ADMIN)
  @Operation(
    summary = "Import estabilishments",
    description = "Import estabilishments from CNES.",
    parameters = {
      @Parameter(
        name = "Authorization",
        description = "Bearer access token",
        required = true
      )
    }
  )
  public ResponseEntity<Void> importCnesEstabilishments(@RequestParam("file") MultipartFile file) {
    this.cnesService.importDataEstabilishments(file);
    return ResponseEntity.ok().build();
  }
  
  @PostMapping("/import-cnes-professionals")
  @PreAuthorize(Permissions.ADMIN)
  @Operation(
    summary = "Import professionals",
    description = "Import professionals from CNES.",
    parameters = {
      @Parameter(
        name = "Authorization",
        description = "Bearer access token",
        required = true
      )
    }
  )
  public ResponseEntity<Void> importCnesProfessionals(@RequestParam("file") MultipartFile file) {
    this.cnesService.importDataProfessionals(file);
    return ResponseEntity.ok().build();
  }
}
