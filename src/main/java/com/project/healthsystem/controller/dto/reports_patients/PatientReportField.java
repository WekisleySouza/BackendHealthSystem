package com.project.healthsystem.controller.dto.reports_patients;

import com.project.healthsystem.exceptions.InvalidDataException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public enum PatientReportField {
    PATIENT_NAME("patientName", "Paciente", 3f, PatientReportRowDTO::patientName),
    MOTHER_NAME("motherName", "Nome da mãe", 3f, PatientReportRowDTO::motherName),
    SCHEDULED_AT("scheduledAt", "Agendado para", 1.6f, row -> formatDate(row.scheduledAt())),
    CREATED_AT("createdAt", "Criado em", 1.6f, row -> formatDate(row.createdAt())),
    STATUS("status", "Status", 1.6f, row -> row.status() != null ? row.status().getLabel() : null),
    RESPONSIBLE_PROFESSIONAL_NAME("responsibleProfessionalName", "Profissional responsável", 2.5f, PatientReportRowDTO::responsibleProfessionalName),
    REQUESTING_PROFESSIONAL_NAME("requestingProfessionalName", "Profissional solicitante", 2.5f, PatientReportRowDTO::requestingProfessionalName),
    PRIORIT("priorit", "Prioridade", 1.3f, row -> row.priorit() != null ? row.priorit().getLabel() : null),
    SERVICE_NAME("serviceName", "Serviço", 3f, PatientReportRowDTO::serviceName),
    SERVICE_TYPE("serviceType", "Tipo", 1.3f, row -> row.serviceType() != null ? row.serviceType().getLabel() : null),
    AGREEMENT("agreement", "Convênio", 2f, PatientReportRowDTO::agreement);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final String key;
    private final String label;
    private final float width;
    private final Function<PatientReportRowDTO, String> extractor;

    PatientReportField(String key, String label, float width, Function<PatientReportRowDTO, String> extractor) {
        this.key = key;
        this.label = label;
        this.width = width;
        this.extractor = extractor;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public float getWidth() {
        return width;
    }

    public String extractFrom(PatientReportRowDTO row) {
        return extractor.apply(row);
    }

    public static PatientReportField fromKey(String key) {
        for (PatientReportField field : values()) {
            if (field.getKey().equalsIgnoreCase(key.trim())) {
                return field;
            }
        }
        throw new InvalidDataException(
            "Campo inválido: " + key + ". Campos disponíveis: " + availableKeys()
        );
    }

    public static List<PatientReportField> fromKeys(List<String> keys) {
        if (keys == null || keys.stream().allMatch(key -> key == null || key.isBlank())) {
            return List.of(values());
        }
        return keys.stream()
            .filter(key -> key != null && !key.isBlank())
            .map(PatientReportField::fromKey)
            .distinct()
            .toList();
    }

    public static String availableKeys() {
        return String.join(", ", Arrays.stream(values()).map(PatientReportField::getKey).toList());
    }

    private static String formatDate(LocalDateTime date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }
}
