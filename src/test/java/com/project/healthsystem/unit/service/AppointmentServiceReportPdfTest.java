package com.project.healthsystem.unit.service;

import com.project.healthsystem.controller.dto.reports_patients.PatientReportFilterDTO;
import com.project.healthsystem.exceptions.InvalidDataException;
import com.project.healthsystem.repository.AppointmentRepository;
import com.project.healthsystem.repository.custom.AppointmentRepositoryCustom;
import com.project.healthsystem.service.AppointmentService;
import com.project.healthsystem.service.PatientReportPdfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceReportPdfTest {

  private static final long MAX_ROWS = 5000;

  @Mock
  private AppointmentRepository repository;

  @Mock
  private AppointmentRepositoryCustom repositoryCustom;

  @Mock
  private PatientReportPdfService patientReportPdfService;

  @InjectMocks
  private AppointmentService appointmentService;

  private final PatientReportFilterDTO emptyFilter = new PatientReportFilterDTO(
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null, null, null
  );

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(appointmentService, "reportPdfMaxRows", MAX_ROWS);
  }

  @Test
  void shouldThrowWhenReportPdfExceedsRowLimit() {
    when(repository.count(any(Specification.class))).thenReturn(MAX_ROWS + 1);

    InvalidDataException exception = assertThrows(
      InvalidDataException.class,
      () -> appointmentService.preparePatientReportPdf(emptyFilter, null, null)
    );

    assertEquals(
      "A busca retornou 5001 registros e o limite para gerar o PDF é 5000. Restrinja mais os filtros.",
      exception.getMessage()
    );
    verify(repositoryCustom, never()).findPatientReportRows(any(), any(), anyBoolean(), anyInt(), anyInt());
  }

  @Test
  void shouldAcceptReportPdfAtRowLimit() {
    when(repository.count(any(Specification.class))).thenReturn(MAX_ROWS);

    assertNotNull(appointmentService.preparePatientReportPdf(emptyFilter, List.of("patientName"), "scheduledAt,desc"));
  }

  @Test
  void shouldThrowWhenReportPdfFieldIsInvalid() {
    assertThrows(
      InvalidDataException.class,
      () -> appointmentService.preparePatientReportPdf(emptyFilter, List.of("invalidField"), null)
    );
    verify(repository, never()).count(any(Specification.class));
  }
  @Test
  void shouldPassFilteredStatisticsAndBatchLoaderToPdfWriter() {
    var statistics = new com.project.healthsystem.controller.dto.reports_patients.PatientReportStatisticsDTO(
        2, 1, 1, java.util.Map.of("Completo", 2L), java.util.Map.of("Eletivo", 2L), java.util.Map.of("Exame", 2L));
    when(repository.count(any(Specification.class))).thenReturn(2L);
    when(repositoryCustom.findPatientReportStatistics(any())).thenReturn(statistics);
    var output = new java.io.ByteArrayOutputStream();
    appointmentService.preparePatientReportPdf(emptyFilter, List.of("patientName"), null).accept(output);
    var specification = org.mockito.ArgumentCaptor.forClass(Specification.class);
    verify(repository).count(specification.capture());
    verify(repositoryCustom).findPatientReportStatistics(specification.getValue());
    verify(patientReportPdfService).write(org.mockito.ArgumentMatchers.same(output),
        org.mockito.ArgumentMatchers.eq(List.of(com.project.healthsystem.controller.dto.reports_patients.PatientReportField.PATIENT_NAME)),
        org.mockito.ArgumentMatchers.same(statistics), any());
  }
}
