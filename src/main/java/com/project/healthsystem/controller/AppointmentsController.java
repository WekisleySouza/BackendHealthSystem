package com.project.healthsystem.controller;

import com.project.healthsystem.controller.docs.AppointmentsApiDocs;

import com.project.healthsystem.controller.common.ControllerAuxFunctions;
import com.project.healthsystem.controller.common.Permissions;
import com.project.healthsystem.controller.dto.*;
import com.project.healthsystem.controller.dto.authorization_form.AuthorizationFormResponseDTO;
import com.project.healthsystem.controller.dto.basic_requests.AppointmentRequestDTO;
import com.project.healthsystem.controller.dto.appointment_get_by_id.AppointmentGetByIdResponseDTO;
import com.project.healthsystem.controller.dto.basic_requests.NewCommonStatusDTO;
import com.project.healthsystem.controller.dto.basic_requests.PreSchedulingRequestDTO;
import com.project.healthsystem.controller.dto.basic_responses.AppointmentResponseDTO;
import com.project.healthsystem.controller.dto.patient_page_responses.PatientAppointmentResponseDTO;
import com.project.healthsystem.controller.dto.reports_patients.PatientReportFilterDTO;
import com.project.healthsystem.controller.dto.reports_patients.PatientReportReponseDTO;
import com.project.healthsystem.controller.dto.reports_patients.ReportAppointmentGraphResponseDTO;
import com.project.healthsystem.controller.dto.reports_specialties.NumberExamsByStatusDTO;
import com.project.healthsystem.controller.dto.reports_specialties.NumberSpecialtiesByStatusDTO;
import com.project.healthsystem.model.Appointment;
import com.project.healthsystem.service.AppointmentService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor

public class AppointmentsController implements AppointmentsApiDocs {
  
  private final AppointmentService appointmentService;
  private final com.project.healthsystem.service.AppointmentStatusHistoryService statusHistoryService;

  @GetMapping("/{id}/status-history")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
  
  public ResponseEntity<Page<com.project.healthsystem.controller.dto.basic_responses.AppointmentStatusHistoryResponseDTO>> getStatusHistory(
    @PathVariable long id,
    @RequestParam(defaultValue = "0") int pageNumber,
    @RequestParam(defaultValue = "20") int pageLength
  ) {
    return ResponseEntity.ok(statusHistoryService.findByAppointment(id, pageNumber, pageLength));
  }

  @PostMapping
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)

  public ResponseEntity<Void> save(
    @RequestHeader("Authorization") String authHeader,
    @RequestBody @Valid AppointmentRequestDTO appointmentRequestDTO
  ) {
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

  public ResponseEntity<Object> update(
    @RequestHeader("Authorization") String authHeader,
    @PathVariable("id") long id,
    @RequestBody @Valid AppointmentRequestDTO appointmentRequestDTO
  ) {
    String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
    appointmentService.update(appointmentRequestDTO, id, accessToken);
    return ResponseEntity.noContent().build();
  }
  
  @GetMapping("/{id}")

  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
  public ResponseEntity<AppointmentGetByIdResponseDTO> read(@PathVariable("id") long id) {
    return ResponseEntity.ok(appointmentService.findById(id));
  }
  
  @GetMapping
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)

  public ResponseEntity<Page<AppointmentResponseDTO>> readAll(
    @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
    @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength,
    @RequestParam(value = "notes", required = false) String notes,
    @RequestParam(value = "scheduledAt", required = false) LocalDateTime scheduledAt,
    @RequestParam(value = "scheduled-at-start", required = false) LocalDateTime scheduledAtStart,
    @RequestParam(value = "scheduled-at-end", required = false) LocalDateTime scheduledAtEnd,
    @RequestParam(value = "createdAt", required = false) LocalDateTime createdAt,
    @RequestParam(value = "created-at-start", required = false) LocalDateTime createdAtStart,
    @RequestParam(value = "created-at-end", required = false) LocalDateTime createdAtEnd,
    @RequestParam(value = "scheduled-forecast", required = false) LocalDateTime scheduledForecast,
    @RequestParam(value = "scheduled-forecast-start", required = false) LocalDateTime scheduledForecastStart,
    @RequestParam(value = "scheduled-forecast-end", required = false) LocalDateTime scheduledForecastEnd,
    @RequestParam(value = "priorit", required = false) String priorit,
    @RequestParam(value = "agreement", required = false) String agreement,
    @RequestParam(value = "status", required = false) String status,
    @RequestParam(value = "responsible-professional-name", required = false) String responsibleProfessional,
    @RequestParam(value = "requesting-professional-name", required = false) String requestingProfessional,
    @RequestParam(value = "employee-name", required = false) String employee,
    @RequestParam(value = "patient-name", required = false) String patient,
    @RequestParam(value = "type", required = false) String type,
    @RequestParam(value = "category-name", required = false) String categoryName,
    @RequestParam(value = "service-type-name", required = false) String serviceTypeName,
    @RequestParam(value = "instituition-name", required = false) String instituitionName,
    @RequestParam(value = "is-sorted-by-name", required = false) boolean isSortedByName,
    @RequestParam(value = "is-return", required = false) Boolean isReturn,
    @RequestParam(value = "is-descending", required = false) boolean isDescending
  ) {
    Page<AppointmentResponseDTO> AppointmentResponseDTOs = appointmentService.getAll(
      type,
      serviceTypeName,
      categoryName,
      pageNumber,
      pageLength,
      notes,
      scheduledAt,
      scheduledAtStart,
      scheduledAtEnd,
      createdAt,
      createdAtStart,
      createdAtEnd,
      scheduledForecast,
      scheduledForecastStart,
      scheduledForecastEnd,
      priorit,
      status,
      agreement,
      responsibleProfessional,
      requestingProfessional,
      employee,
      patient,
      instituitionName,
      isSortedByName,
      isDescending,
      isReturn
    );
    return ResponseEntity.ok(AppointmentResponseDTOs);
  }
  
  @GetMapping("/report-patients-graph")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
  public ResponseEntity<ReportAppointmentGraphResponseDTO> reportAppointmentsPatientGraph(
    @RequestParam(value = "patient-name", required = false) String patientName,
    @RequestParam(value = "mother-name", required = false) String patientMotherName,
    @RequestParam(value = "responsible-professional-name", required = false) String responsibleProfessional,
    @RequestParam(value = "requesting-professional-name", required = false) String requestingProfessional,
    @RequestParam(value = "status", required = false) String status,
    @RequestParam(value = "agreement", required = false) String agreement,
    @RequestParam(value = "priorit", required = false) String priorit,
    @RequestParam(value = "type", required = false) String type,
    @RequestParam(value = "service-name", required = false) String serviceName,
    @RequestParam(value = "instituition-name", required = false) String instituitionName,
    @RequestParam(value = "scheduled-at", required = false) LocalDateTime scheduledAt,
    @RequestParam(value = "scheduled-at-start", required = false) LocalDateTime scheduledAtStart,
    @RequestParam(value = "scheduled-at-end", required = false) LocalDateTime scheduledAtEnd,
    @RequestParam(value = "created-at", required = false) LocalDateTime createdAt,
    @RequestParam(value = "created-at-start", required = false) LocalDateTime createdAtStart,
    @RequestParam(value = "created-at-end", required = false) LocalDateTime createdAtEnd
  ) {
    return ResponseEntity.ok(appointmentService.getPatientReportGraphInfo(
      patientName,
      patientMotherName,
      responsibleProfessional,
      requestingProfessional,
      status,
      agreement,
      priorit,
      type,
      serviceName,
      instituitionName,
      scheduledAt,
      scheduledAtStart,
      scheduledAtEnd,
      createdAt,
      createdAtStart,
      createdAtEnd
    ));
  }
  
  @GetMapping("/report-patients-page")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
  public ResponseEntity<Page<PatientReportReponseDTO>> reportAppointmentsPatientPage(
    @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
    @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength,
    @RequestParam(value = "patient-name", required = false) String patientName,
    @RequestParam(value = "mother-name", required = false) String patientMotherName,
    @RequestParam(value = "responsible-professional-name", required = false) String responsibleProfessional,
    @RequestParam(value = "requesting-professional-name", required = false) String requestingProfessional,
    @RequestParam(value = "status", required = false) String status,
    @RequestParam(value = "agreement", required = false) String agreement,
    @RequestParam(value = "priorit", required = false) String priorit,
    @RequestParam(value = "type", required = false) String type,
    @RequestParam(value = "service-name", required = false) String serviceName,
    @RequestParam(value = "instituition-name", required = false) String instituitionName,
    @RequestParam(value = "scheduled-at", required = false) LocalDateTime scheduledAt,
    @RequestParam(value = "scheduled-at-start", required = false) LocalDateTime scheduledAtStart,
    @RequestParam(value = "scheduled-at-end", required = false) LocalDateTime scheduledAtEnd,
    @RequestParam(value = "created-at", required = false) LocalDateTime createdAt,
    @RequestParam(value = "created-at-start", required = false) LocalDateTime createdAtStart,
    @RequestParam(value = "created-at-end", required = false) LocalDateTime createdAtEnd
  ) {
    return ResponseEntity.ok(appointmentService.getPatientReportPages(
      pageNumber,
      pageLength,
      patientName,
      patientMotherName,
      responsibleProfessional,
      requestingProfessional,
      status,
      agreement,
      priorit,
      type,
      serviceName,
      instituitionName,
      scheduledAt,
      scheduledAtStart,
      scheduledAtEnd,
      createdAt,
      createdAtStart,
      createdAtEnd
    ));
  }

  @GetMapping("/report-patients-pdf")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)

  public void reportAppointmentsPatientPdf(
    @ParameterObject PatientReportFilterDTO filter,
    @RequestParam(value = "fields", required = false) List<String> fields,
    @RequestParam(value = "sort", required = false) String sort,
    HttpServletResponse response
  ) throws IOException {
    Consumer<OutputStream> pdfWriter = appointmentService.preparePatientReportPdf(filter, fields, sort);

    response.setContentType(MediaType.APPLICATION_PDF_VALUE);
    response.setHeader(
      HttpHeaders.CONTENT_DISPOSITION,
      ContentDisposition.attachment()
        .filename("relatorio-pacientes-" + LocalDate.now() + ".pdf")
        .build()
        .toString()
    );
    pdfWriter.accept(response.getOutputStream());
  }

  @DeleteMapping("{id}")

  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
  public ResponseEntity<Object> delete(@PathVariable("id") long id) {
    appointmentService.delete(id);
    return ResponseEntity.noContent().build();
  }
  
  @PatchMapping("/set-appointments-to-pre-scheduled")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER)

  public ResponseEntity<Object> setAppointmentsToPreScheduled(
    @RequestHeader("Authorization") String authHeader,
    @RequestBody @Valid PreSchedulingRequestDTO preSchedulingRequestDTO
  ) {
    appointmentService.setPreSchedulingTo(preSchedulingRequestDTO, ControllerAuxFunctions.getTokenFrom(authHeader));
    return ResponseEntity.noContent().build();
  }
  
  @PatchMapping("/set-appointments-to-new-status")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
  
  public ResponseEntity<Object> setAppointmentsToOtherStatus(
    @RequestHeader("Authorization") String authHeader,
    @RequestBody @Valid NewCommonStatusDTO preSchedulingRequestDTO
  ) {
    appointmentService.setNewStatusTo(preSchedulingRequestDTO, ControllerAuxFunctions.getTokenFrom(authHeader));
    return ResponseEntity.noContent().build();
  }
  
  @GetMapping("generate-authorization-form/{id}")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE)
  public ResponseEntity<AuthorizationFormResponseDTO> countExamsByServiceType(
    @PathVariable("id") long id
  ) {
    return ResponseEntity.ok(
      this.appointmentService.getAuthorizationForm(id)
    );
  }
  
  @GetMapping("/get-patient-appointments")
  @PreAuthorize(Permissions.ADMIN_OR_MANAGER_OR_EMPLOYEE_OR_PATIENT)
  public ResponseEntity<Page<PatientAppointmentResponseDTO>> updatePatients(
    @RequestHeader("Authorization") String authHeader,
    @RequestParam(value = "page-number", defaultValue = "0") Integer pageNumber,
    @RequestParam(value = "page-length", defaultValue = "10") Integer pageLength
  ) {
    String accessToken = ControllerAuxFunctions.getTokenFrom(authHeader);
    return ResponseEntity.ok(
      this.appointmentService.getAllPatientAppointments(
        accessToken,
        pageNumber,
        pageLength
      )
    );
  }
}
