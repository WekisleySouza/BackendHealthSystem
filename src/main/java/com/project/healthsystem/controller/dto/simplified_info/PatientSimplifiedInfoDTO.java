package com.project.healthsystem.controller.dto.simplified_info;

public record PatientSimplifiedInfoDTO(
    long id,
    String name,
    String cpf,
    String motherName
){}
