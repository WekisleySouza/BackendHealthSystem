package com.project.healthsystem.controller.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileResponseDTO {

    // Common atributes
    private String name;
    private String cpf;
    private String phone;
    private LocalDate birthday;
    private String email;

    // Patient atributes
    private String cns;
    private String motherName;
}
