package com.project.healthsystem.controller.dto;

public record PatientSimplifiedInfoDTO(
    long id,
    String name,
    String cpf,
    String motherName
){}
