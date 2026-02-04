package com.project.healthsystem.controller.dto;

import com.project.healthsystem.model.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ProfessionalResponseDTO {
    private long id;

    private String name;
    private String gender;
    private LocalDate birthday;
    private String cpf;
    private String address;
    private String phone;
    private String email;
}
