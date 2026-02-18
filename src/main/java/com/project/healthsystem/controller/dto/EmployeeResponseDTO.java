package com.project.healthsystem.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeResponseDTO {
    private long id;
    private String name;
    private String gender;
    private String sex;
    private String cellPhone;
    private String residentialPhone;
    private String contactPhone;
    private String cpf;
    private LocalDate birthday;
    private String email;
    private String address;
    private String role;
}
