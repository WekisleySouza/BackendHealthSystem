package com.project.healthsystem.controller;

import com.project.healthsystem.controller.docs.DataSusApiDocs;

import com.project.healthsystem.controller.common.Permissions;
import com.project.healthsystem.service.datasus.CnesService;
import com.project.healthsystem.service.datasus.CnesProfessionalService;
import com.project.healthsystem.service.datasus.CnesEstabilishmentService;
import com.project.healthsystem.controller.dto.basic_responses.CnesProfessionalResponseDTO;
import com.project.healthsystem.controller.dto.basic_responses.CnesEstabilishmentResponseDTO;
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

public class DataSusController implements DataSusApiDocs {
  private final CnesService cnesService;
  private final Cid10Service cid10Service;
  private final Ciap2Service ciap2Service;
  private final SigtapService sigtapService;
  private final SigtapProcedureService sigtapProcedureService;
  private final CnesProfessionalService cnesProfessionalService;
  private final CnesEstabilishmentService cnesEstabilishmentService;

  @GetMapping("/sigtap/procedures")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
  
  public ResponseEntity<Page<SigtapProcedureResponseDTO>> findSigtapProcedures(
      @RequestParam(value = "page-number", defaultValue = "0") int pageNumber,
      @RequestParam(value = "page-length", defaultValue = "10") int pageLength,
      @RequestParam(value = "search", required = false) String search
  ) {
    return ResponseEntity.ok(sigtapProcedureService.findAll(pageNumber, pageLength, search));
  }

  @GetMapping("/sigtap/procedures/{id}")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
  
  public ResponseEntity<SigtapProcedureResponseDTO> findSigtapProcedure(@PathVariable("id") long id) {
    return ResponseEntity.ok(sigtapProcedureService.findById(id));
  }

  // Temporary endpoint for validating the SIGTAP import.
  @PostMapping("/import-sigtap")
  @PreAuthorize(Permissions.ADMIN)
  
  public ResponseEntity<SigtapImportResponseDTO>
      importSigtap() throws IOException {
    return ResponseEntity.ok(sigtapService.importSigtap());
  }

  @GetMapping("/cid10")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
  
  public ResponseEntity<Page<Cid10ResponseDTO>> findCid10(
      @RequestParam(value = "page-number", defaultValue = "0") int pageNumber,
      @RequestParam(value = "page-length", defaultValue = "10") int pageLength,
      @RequestParam(value = "search", required = false) String search
  ) {
    return ResponseEntity.ok(cid10Service.findAll(pageNumber, pageLength, search));
  }

  @GetMapping("/ciap2")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
  
  public ResponseEntity<Page<Ciap2ResponseDTO>> findCiap2(
      @RequestParam(value = "page-number", defaultValue = "0") int pageNumber,
      @RequestParam(value = "page-length", defaultValue = "10") int pageLength,
      @RequestParam(value = "search", required = false) String search
  ) {
    return ResponseEntity.ok(ciap2Service.findAll(pageNumber, pageLength, search));
  }
  
  @PostMapping(value = "/import-cnes-estabilishments", consumes = "multipart/form-data")
  @PreAuthorize(Permissions.ADMIN)
  
  public ResponseEntity<CnesImportResponseDTO> importCnesEstabilishments(@RequestParam("file") MultipartFile file) {
    return ResponseEntity.ok(this.cnesService.importDataEstabilishments(file));
  }
  
  @PostMapping(value = "/import-cnes-professionals", consumes = "multipart/form-data")
  @PreAuthorize(Permissions.ADMIN)
  
  public ResponseEntity<CnesImportResponseDTO> importCnesProfessionals(@RequestParam("file") MultipartFile file) {
    return ResponseEntity.ok(this.cnesService.importDataProfessionals(file));
  }

  @GetMapping("/cnes/professionals")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
  
  public ResponseEntity<Page<CnesProfessionalResponseDTO>> findProfessionals(
      @RequestParam(value = "page-number", defaultValue = "0") int pageNumber,
      @RequestParam(value = "page-length", defaultValue = "10") int pageLength,
      @RequestParam(value = "search", required = false) String search,
      @RequestParam(value = "cnes", required = false) String cnes,
      @RequestParam(value = "cbo", required = false) String cbo
  ) {
    return ResponseEntity.ok(cnesProfessionalService.findAll(pageNumber, pageLength, search, cnes, cbo));
  }

  @GetMapping("/cnes/professionals/{id}")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
  
  public ResponseEntity<CnesProfessionalResponseDTO> findProfessional(@PathVariable("id") long id) {
    return ResponseEntity.ok(cnesProfessionalService.findById(id));
  }

  @GetMapping("/cnes/estabilishments")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
  
  public ResponseEntity<Page<CnesEstabilishmentResponseDTO>> findEstabilishments(
      @RequestParam(value = "page-number", defaultValue = "0") int pageNumber,
      @RequestParam(value = "page-length", defaultValue = "10") int pageLength,
      @RequestParam(value = "search", required = false) String search
  ) {
    return ResponseEntity.ok(cnesEstabilishmentService.findAll(pageNumber, pageLength, search));
  }

  @GetMapping("/cnes/estabilishments/{id}")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
  
  public ResponseEntity<CnesEstabilishmentResponseDTO> findEstabilishment(@PathVariable("id") long id) {
    return ResponseEntity.ok(cnesEstabilishmentService.findById(id));
  }
}
