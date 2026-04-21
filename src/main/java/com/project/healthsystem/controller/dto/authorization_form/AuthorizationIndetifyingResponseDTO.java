package com.project.healthsystem.controller.dto.authorization_form;

import java.time.LocalDateTime;

public record AuthorizationIndetifyingResponseDTO(
    String origin,
    String destiny,
    LocalDateTime appointmentScheduledDateTime,
    InstituitionResponseDTO instituitionResponseDTO
) {
}
