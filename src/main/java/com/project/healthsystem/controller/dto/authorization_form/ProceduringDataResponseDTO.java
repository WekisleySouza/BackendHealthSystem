package com.project.healthsystem.controller.dto.authorization_form;

public record ProceduringDataResponseDTO(
    String serviceTypeName,
    String serviceType,
    String serviceTypeCode,
    String categoryName
) {
}
