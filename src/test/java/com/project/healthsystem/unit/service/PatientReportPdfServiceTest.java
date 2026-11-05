package com.project.healthsystem.unit.service;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import com.project.healthsystem.controller.dto.reports_patients.*;
import com.project.healthsystem.model.*;
import com.project.healthsystem.service.PatientReportPdfService;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class PatientReportPdfServiceTest {
    private final PatientReportPdfService service = new PatientReportPdfService();

    @Test
    void shouldRenderEmptyReportWithZeroStatistics() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        service.write(output, List.of(PatientReportField.PATIENT_NAME),
            new PatientReportStatisticsDTO(0, 0, 0, Map.of(), Map.of(), Map.of()), offset -> List.of());
        try (PdfReader reader = new PdfReader(output.toByteArray())) {
            String text = new PdfTextExtractor(reader).getTextFromPage(1);
            assertEquals(1, reader.getNumberOfPages());
            assertTrue(text.contains("Nenhum registro encontrado"));
            assertTrue(text.contains("Pacientes únicos: 0"));
            assertTrue(text.contains("Relatório Geral"));
        }
    }

    @Test
    void shouldRespectSelectedColumnsAndRenderMissingValues() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PatientReportRowDTO row = new PatientReportRowDTO("Paciente fictício", null, null, null,
            null, null, null, null, "Serviço oculto", null, null);
        service.write(output, List.of(PatientReportField.PATIENT_NAME, PatientReportField.MOTHER_NAME),
            new PatientReportStatisticsDTO(1, 1, 0, Map.of(), Map.of(), Map.of()),
            offset -> offset == 0 ? List.of(row) : List.of());
        try (PdfReader reader = new PdfReader(output.toByteArray())) {
            String text = new PdfTextExtractor(reader).getTextFromPage(1);
            assertTrue(text.contains("Paciente fictício"));
            assertTrue(text.contains("Nome da mãe"));
            assertTrue(text.contains("-"));
            assertFalse(text.contains("Serviço oculto"));
        }
    }

    @Test
    void shouldRepeatHeadersAndNumberAllPagesWithoutLosingBatchedRows() throws Exception {
        List<PatientReportRowDTO> rows = IntStream.range(0, 235).mapToObj(i -> new PatientReportRowDTO(
            String.format("Paciente fictício %03d", i), "Maria de Souza", LocalDateTime.of(2026, 10, 7, 10, 30),
            LocalDateTime.of(2026, 10, 1, 9, 0), Status.COMPLETED, "Ana Oliveira", "Carlos Santos",
            Priority.ELECTIVE, "Consulta de acompanhamento", ServiceTypes.SPECIALTY, "SUS"
        )).toList();
        List<Integer> offsets = new ArrayList<>();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        service.write(output, List.of(PatientReportField.values()),
            new PatientReportStatisticsDTO(235, 235, 2, Map.of("Completo", 235L),
                Map.of("Eletivo", 235L), Map.of("Especialidade", 235L)),
            offset -> {
                offsets.add(offset);
                return rows.subList(offset, Math.min(offset + 73, rows.size()));
            });
        assertEquals(List.of(0, 73, 146, 219, 235), offsets);
        try (PdfReader reader = new PdfReader(output.toByteArray())) {
            assertTrue(reader.getNumberOfPages() > 1);
            StringBuilder all = new StringBuilder();
            PdfTextExtractor extractor = new PdfTextExtractor(reader);
            for (int page = 1; page <= reader.getNumberOfPages(); page++) {
                String text = extractor.getTextFromPage(page);
                assertEquals(page == 1, text.contains("Relatório Geral"));
                assertTrue(text.contains("Paciente"));
                assertTrue(text.replaceAll("\\s+", " ").contains("Página " + page + " de " + reader.getNumberOfPages()), text);
                all.append(text).append(' ');
            }
            String normalized = all.toString().replaceAll("\\s+", " ");
            for (int i = 0; i < rows.size(); i++) {
                String name = String.format("Paciente fictício %03d", i);
                assertTrue(normalized.contains(name), name);
                assertEquals(normalized.indexOf(name), normalized.lastIndexOf(name), name);
            }
        }
        String preview = System.getProperty("report.pdf.preview");
        if (preview != null) {
            Path path = Path.of(preview);
            Files.createDirectories(path.getParent());
            Files.write(path, output.toByteArray());
        }
    }
}