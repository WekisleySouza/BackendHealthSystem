package com.project.healthsystem.controller.dto.basic_responses;

public record SigtapImportResponseDTO(
    boolean imported, String competence, String fileName,
    long groups, long subGroups, long organizations, long procedures
) {}
