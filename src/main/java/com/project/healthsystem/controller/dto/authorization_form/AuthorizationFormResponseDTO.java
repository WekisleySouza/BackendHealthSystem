package com.project.healthsystem.controller.dto.authorization_form;

import java.time.LocalDate;

public record AuthorizationFormResponseDTO(
    PatientDataResponseDTO patientDataResponseDTO,
    AuthorizationIndetifyingResponseDTO authorizationIndetifyingResponseDTO,
    ProceduringDataResponseDTO proceduringDataResponseDTO,
    String description,
    String employeeName,
    LocalDate emitionDate
) {
}
