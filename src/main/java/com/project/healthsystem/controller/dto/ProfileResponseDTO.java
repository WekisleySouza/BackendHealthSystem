package com.project.healthsystem.controller.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileResponseDTO {

    // Common atributes
    private String name;
    private String cpf;
    private String address;
    private String phone;
    private LocalDate birthday;
    private String email;
    private String sex;
    private String cellPhone;
    private String residentialPhone;
    private String contactPhone;

    // Patient atributes
    private String cns;
    private String motherName;
    private String teamName;
    private String teamINE;
    private String microArea;
    private String origin;
}
