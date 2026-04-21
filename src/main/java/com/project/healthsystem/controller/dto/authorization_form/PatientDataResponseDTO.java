package com.project.healthsystem.controller.dto.authorization_form;

import java.time.LocalDate;

public record PatientDataResponseDTO(
    String name,
    String esusCard,
    String Sex,
    LocalDate birthday,
    Integer age,
    String address
) {
}
